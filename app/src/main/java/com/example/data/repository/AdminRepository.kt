package com.example.data.repository

import android.content.Context
import com.example.data.crypto.CryptoManager
import com.example.data.local.AdminAuditLogEntity
import com.example.data.local.AdminDao
import com.example.data.local.AdminUserRoleEntity
import com.example.data.local.AppDatabase
import com.example.data.local.ModerationTicketEntity
import com.example.data.local.PostDao
import com.example.data.local.SystemConfigEntity
import com.example.data.rbac.AdminPersona
import com.example.data.rbac.RbacCheckResult
import com.example.data.rbac.RbacPermission
import com.example.data.rbac.RbacPolicy
import com.example.data.rbac.RbacRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminRepository(
    context: Context,
    private val appDb: AppDatabase = AppDatabase.getDatabase(context)
) {
    private val adminDao: AdminDao = appDb.adminDao()
    private val postDao: PostDao = appDb.postDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    // Pre-seeded switchable personas to test RBAC in real-time
    val availablePersonas = listOf(
        AdminPersona(
            id = "user_me",
            name = "Nova Sterling",
            handle = "@novasterling",
            role = RbacRole.SUPER_ADMIN,
            avatarDrawable = "post_cyber_creator_1790200662289",
            titleBadge = "Super Admin (Root)"
        ),
        AdminPersona(
            id = "user_marcus",
            name = "Marcus Vance",
            handle = "@marcus.v",
            role = RbacRole.SECURITY_ADMIN,
            avatarDrawable = "post_crystal_city_1790200645179",
            titleBadge = "Security Admin"
        ),
        AdminPersona(
            id = "user_elena",
            name = "Elena Rostova",
            handle = "@elena.synapse",
            role = RbacRole.CONTENT_MODERATOR,
            avatarDrawable = "post_cyber_creator_1790200662289",
            titleBadge = "Content Moderator"
        ),
        AdminPersona(
            id = "user_sarah",
            name = "Sarah Chen",
            handle = "@schen_art",
            role = RbacRole.ADS_MANAGER,
            avatarDrawable = "post_aurora_vibes_1790200680892",
            titleBadge = "Ads Manager"
        ),
        AdminPersona(
            id = "user_guest",
            name = "Guest Wanderer",
            handle = "@guest_cypher",
            role = RbacRole.STANDARD_USER,
            avatarDrawable = "post_crystal_city_1790200645179",
            titleBadge = "Standard User (No Admin Privileges)"
        )
    )

    private val _activePersona = MutableStateFlow(availablePersonas.first())
    val activePersona = _activePersona.asStateFlow()

    // Shared flow to publish Access Denied alerts to UI
    private val _accessDeniedEvent = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val accessDeniedEvent = _accessDeniedEvent.asSharedFlow()

    val allUserRoles: Flow<List<AdminUserRoleEntity>> = adminDao.getAllUserRoles()
    val allAuditLogs: Flow<List<AdminAuditLogEntity>> = adminDao.getAllAuditLogs()
    val allTickets: Flow<List<ModerationTicketEntity>> = adminDao.getAllTickets()
    val allConfigs: Flow<List<SystemConfigEntity>> = adminDao.getAllConfigs()

    init {
        scope.launch {
            seedAdminDataIfEmpty()
        }
    }

    fun switchPersona(persona: AdminPersona) {
        _activePersona.value = persona
        scope.launch {
            logAuditAction(
                actionType = "PERSONA_SWITCHED",
                targetType = "SESSION",
                targetId = persona.id,
                details = "Admin switched identity to ${persona.name} with role ${persona.role.title}"
            )
        }
    }

    fun checkPermission(permission: RbacPermission): RbacCheckResult {
        val currentRole = _activePersona.value.role
        val allowed = RbacPolicy.hasPermission(currentRole, permission)
        return if (allowed) {
            RbacCheckResult(isAllowed = true, requiredPermission = permission, actorRole = currentRole)
        } else {
            val errorMsg = "ACCESS DENIED: Role '${currentRole.title}' requires [${permission.displayName}] (${permission.code}) privilege."
            _accessDeniedEvent.tryEmit(errorMsg)
            RbacCheckResult(
                isAllowed = false,
                requiredPermission = permission,
                actorRole = currentRole,
                errorMessage = errorMsg
            )
        }
    }

    suspend fun assignUserRole(
        targetUserId: String,
        targetUserName: String,
        newRole: RbacRole
    ): Result<String> = withContext(Dispatchers.IO) {
        val check = checkPermission(RbacPermission.MANAGE_ROLES)
        if (!check.isAllowed) {
            logAuditAction(
                actionType = "ACCESS_DENIED_ROLE_CHANGE",
                targetType = "USER",
                targetId = targetUserId,
                details = "Blocked attempt by ${_activePersona.value.name} (${_activePersona.value.role}) to assign $newRole to $targetUserName"
            )
            return@withContext Result.failure(SecurityException(check.errorMessage))
        }

        val actor = _activePersona.value
        val now = System.currentTimeMillis()
        adminDao.updateUserRole(
            userId = targetUserId,
            newRole = newRole.name,
            assignedBy = actor.name,
            assignedAt = now
        )

        logAuditAction(
            actionType = "ROLE_ASSIGNED",
            targetType = "USER",
            targetId = targetUserId,
            details = "Assigned role '${newRole.title}' to user $targetUserName by ${actor.name}"
        )
        Result.success("Role '${newRole.title}' assigned to $targetUserName")
    }

    suspend fun toggleUserBan(
        targetUserId: String,
        targetUserName: String,
        isBanned: Boolean,
        reason: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        val check = checkPermission(RbacPermission.MANAGE_USERS)
        if (!check.isAllowed) {
            logAuditAction(
                actionType = "ACCESS_DENIED_USER_BAN",
                targetType = "USER",
                targetId = targetUserId,
                details = "Blocked unauthorized ban/unban attempt on $targetUserName by ${_activePersona.value.name}"
            )
            return@withContext Result.failure(SecurityException(check.errorMessage))
        }

        adminDao.updateUserBanStatus(targetUserId, isBanned, reason)
        val action = if (isBanned) "BANNED" else "UNBANNED"
        logAuditAction(
            actionType = "USER_$action",
            targetType = "USER",
            targetId = targetUserId,
            details = "User $targetUserName was $action by ${_activePersona.value.name}. Reason: ${reason ?: "No reason given"}"
        )
        Result.success("User $targetUserName $action successfully.")
    }

    suspend fun resolveModerationTicket(
        ticketId: String,
        newStatus: String, // QUARANTINED, DISMISSED, RESOLVED
        notes: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        val check = checkPermission(RbacPermission.MODERATE_POSTS)
        if (!check.isAllowed) {
            logAuditAction(
                actionType = "ACCESS_DENIED_TICKET_RESOLVE",
                targetType = "TICKET",
                targetId = ticketId,
                details = "Blocked unauthorized ticket resolution attempt by ${_activePersona.value.name}"
            )
            return@withContext Result.failure(SecurityException(check.errorMessage))
        }

        val ticket = adminDao.getTicketById(ticketId)
        val now = System.currentTimeMillis()
        adminDao.updateTicketStatus(
            ticketId = ticketId,
            status = newStatus,
            notes = notes,
            reviewedBy = _activePersona.value.name,
            reviewedAt = now
        )

        // If ticket targets a post and is quarantined, delete from postDao
        if (ticket?.targetType == "POST" && newStatus == "QUARANTINED") {
            try {
                postDao.deletePostById(ticket.targetId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        logAuditAction(
            actionType = "TICKET_$newStatus",
            targetType = "TICKET",
            targetId = ticketId,
            details = "Moderation ticket #$ticketId marked as $newStatus by ${_activePersona.value.name}. Notes: ${notes ?: "N/A"}"
        )
        Result.success("Ticket #$ticketId resolved as $newStatus.")
    }

    suspend fun quarantinePostDirectly(
        postId: String,
        reason: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val check = checkPermission(RbacPermission.MODERATE_POSTS)
        if (!check.isAllowed) {
            return@withContext Result.failure(SecurityException(check.errorMessage))
        }

        postDao.deletePostById(postId)
        val ticketId = "ticket_auto_${System.currentTimeMillis()}"
        adminDao.insertTicket(
            ModerationTicketEntity(
                id = ticketId,
                targetType = "POST",
                targetId = postId,
                targetPreviewText = "Quarantined post: $postId",
                targetAuthor = "Author",
                reporterHandle = _activePersona.value.handle,
                reportReason = reason,
                aiSeverityScore = 95,
                status = "QUARANTINED",
                reviewerNotes = "Immediately quarantined by moderator",
                reviewedBy = _activePersona.value.name,
                reviewedAt = System.currentTimeMillis()
            )
        )

        logAuditAction(
            actionType = "POST_QUARANTINED",
            targetType = "POST",
            targetId = postId,
            details = "Post $postId removed and quarantined. Reason: $reason"
        )
        Result.success("Post $postId successfully quarantined from feed.")
    }

    suspend fun updateSystemConfig(
        key: String,
        newValue: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val check = checkPermission(RbacPermission.SYSTEM_CONFIG)
        if (!check.isAllowed) {
            logAuditAction(
                actionType = "ACCESS_DENIED_CONFIG_CHANGE",
                targetType = "CONFIG",
                targetId = key,
                details = "Blocked unauthorized attempt to update config $key to $newValue by ${_activePersona.value.name}"
            )
            return@withContext Result.failure(SecurityException(check.errorMessage))
        }

        val now = System.currentTimeMillis()
        adminDao.updateConfigValue(key, newValue, now, _activePersona.value.name)
        logAuditAction(
            actionType = "CONFIG_UPDATED",
            targetType = "SYSTEM_CONFIG",
            targetId = key,
            details = "System parameter '$key' updated to '$newValue' by ${_activePersona.value.name}"
        )
        Result.success("System config '$key' updated to '$newValue'.")
    }

    suspend fun rotateMasterSecurityKeys(): Result<String> = withContext(Dispatchers.IO) {
        val check = checkPermission(RbacPermission.SECURITY_KEYS)
        if (!check.isAllowed) {
            logAuditAction(
                actionType = "ACCESS_DENIED_KEY_ROTATION",
                targetType = "SECURITY",
                targetId = "MASTER_KEY",
                details = "Blocked unauthorized master key rotation attempt by ${_activePersona.value.name}"
            )
            return@withContext Result.failure(SecurityException(check.errorMessage))
        }

        logAuditAction(
            actionType = "MASTER_KEY_ROTATED",
            targetType = "CRYPTOGRAPHY",
            targetId = "RSA-2048/GCM",
            details = "2048-bit platform cryptographic session root rotated by Security Admin ${_activePersona.value.name}"
        )
        Result.success("Master cryptographic keys rotated and re-seeded successfully.")
    }

    suspend fun approveOrRejectSponsoredAd(
        adId: String,
        brandName: String,
        isApproved: Boolean
    ): Result<String> = withContext(Dispatchers.IO) {
        val check = checkPermission(RbacPermission.MANAGE_ADS)
        if (!check.isAllowed) {
            return@withContext Result.failure(SecurityException(check.errorMessage))
        }

        val status = if (isApproved) "APPROVED" else "REJECTED"
        logAuditAction(
            actionType = "SPONSORED_AD_$status",
            targetType = "AD_CAMPAIGN",
            targetId = adId,
            details = "Campaign by '$brandName' was marked $status by ${_activePersona.value.name}"
        )
        Result.success("Campaign by $brandName was $status.")
    }

    private suspend fun logAuditAction(
        actionType: String,
        targetType: String,
        targetId: String,
        details: String
    ) {
        val now = System.currentTimeMillis()
        val actor = _activePersona.value
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
        val formattedTime = formatter.format(Date(now))
        val rawPayload = "$now:$actionType:${actor.id}:${actor.role.name}:$targetType:$targetId:$details"
        val signature = CryptoManager.signAuditRecord(rawPayload)

        val logEntity = AdminAuditLogEntity(
            id = "audit_${now}_${(1000..9999).random()}",
            timestamp = now,
            timestampFormatted = formattedTime,
            actorId = actor.id,
            actorName = actor.name,
            actorRole = actor.role.name,
            actionType = actionType,
            targetType = targetType,
            targetId = targetId,
            details = details,
            signatureHash = signature
        )
        adminDao.insertAuditLog(logEntity)
    }

    private suspend fun seedAdminDataIfEmpty() {
        // 1. Seed User Roles if empty
        val existingRoles = adminDao.getAllUserRoles().first()
        if (existingRoles.isEmpty()) {
            val now = System.currentTimeMillis()
            val initialRoles = listOf(
                AdminUserRoleEntity(
                    userId = "user_me",
                    userName = "Nova Sterling",
                    userHandle = "@novasterling",
                    avatarDrawable = "post_cyber_creator_1790200662289",
                    role = RbacRole.SUPER_ADMIN.name,
                    assignedBy = "System Root",
                    assignedAt = now - 86400000 * 30,
                    isBanned = false,
                    auraLevel = 99
                ),
                AdminUserRoleEntity(
                    userId = "user_marcus",
                    userName = "Marcus Vance",
                    userHandle = "@marcus.v",
                    avatarDrawable = "post_crystal_city_1790200645179",
                    role = RbacRole.SECURITY_ADMIN.name,
                    assignedBy = "Nova Sterling",
                    assignedAt = now - 86400000 * 14,
                    isBanned = false,
                    auraLevel = 94
                ),
                AdminUserRoleEntity(
                    userId = "user_elena",
                    userName = "Elena Rostova",
                    userHandle = "@elena.synapse",
                    avatarDrawable = "post_cyber_creator_1790200662289",
                    role = RbacRole.CONTENT_MODERATOR.name,
                    assignedBy = "Nova Sterling",
                    assignedAt = now - 86400000 * 10,
                    isBanned = false,
                    auraLevel = 92
                ),
                AdminUserRoleEntity(
                    userId = "user_sarah",
                    userName = "Sarah Chen",
                    userHandle = "@schen_art",
                    avatarDrawable = "post_aurora_vibes_1790200680892",
                    role = RbacRole.ADS_MANAGER.name,
                    assignedBy = "Nova Sterling",
                    assignedAt = now - 86400000 * 7,
                    isBanned = false,
                    auraLevel = 95
                ),
                AdminUserRoleEntity(
                    userId = "user_zenith",
                    userName = "Zenith AI Synthesizer",
                    userHandle = "@zenith_bot",
                    avatarDrawable = "post_crystal_city_1790200645179",
                    role = RbacRole.CREATOR_PARTNER.name,
                    assignedBy = "Elena Rostova",
                    assignedAt = now - 86400000 * 3,
                    isBanned = false,
                    auraLevel = 88
                ),
                AdminUserRoleEntity(
                    userId = "user_sybil",
                    userName = "Phishing Scrape Node",
                    userHandle = "@free_tokens_bot",
                    avatarDrawable = "post_crystal_city_1790200645179",
                    role = RbacRole.STANDARD_USER.name,
                    assignedBy = "Auto-Registrar",
                    assignedAt = now - 86400000 * 1,
                    isBanned = true,
                    banReason = "Suspicious sybil automated bot activity & phishing attempts",
                    auraLevel = 10
                )
            )
            adminDao.insertUserRoles(initialRoles)
        }

        // 2. Seed Moderation Tickets if empty
        val existingTickets = adminDao.getAllTickets().first()
        if (existingTickets.isEmpty()) {
            val initialTickets = listOf(
                ModerationTicketEntity(
                    id = "mod_ticket_001",
                    targetType = "POST",
                    targetId = "post_suspicious_101",
                    targetPreviewText = "GUARANTEED 1000x Sparks multiplier glitch! Click external relay now...",
                    targetAuthor = "@free_tokens_bot",
                    reporterHandle = "@elena.synapse",
                    reportReason = "Phishing & Fraudulent Financial Scheme",
                    aiSeverityScore = 96,
                    status = "PENDING"
                ),
                ModerationTicketEntity(
                    id = "mod_ticket_002",
                    targetType = "COMMENT",
                    targetId = "comm_flagged_202",
                    targetPreviewText = "Your shaders are garbage, delete your account immediately loser.",
                    targetAuthor = "@troll_mesh_09",
                    reporterHandle = "@marcus.v",
                    reportReason = "Targeted Harassment & Hostile Language",
                    aiSeverityScore = 87,
                    status = "PENDING"
                ),
                ModerationTicketEntity(
                    id = "mod_ticket_003",
                    targetType = "REEL",
                    targetId = "reel_unlicensed_303",
                    targetPreviewText = "Audio remix containing uncredited spatial audio stem",
                    targetAuthor = "@remix_stealer",
                    reporterHandle = "@schen_art",
                    reportReason = "Copyright & Neural Attribution Infringement",
                    aiSeverityScore = 72,
                    status = "DISMISSED",
                    reviewerNotes = "Audio is within Creative Commons Attribution spectrum",
                    reviewedBy = "Elena Rostova",
                    reviewedAt = System.currentTimeMillis() - 3600000 * 4
                )
            )
            adminDao.insertTickets(initialTickets)
        }

        // 3. Seed System Configs if empty
        val existingConfigs = adminDao.getAllConfigs().first()
        if (existingConfigs.isEmpty()) {
            val initialConfigs = listOf(
                SystemConfigEntity(
                    key = "FEED_AI_KILLSWITCH",
                    value = "DISABLED",
                    category = "SAFETY",
                    description = "Emergency killswitch: instantly pauses algorithmic AI feed recommendation rankings",
                    lastUpdated = System.currentTimeMillis() - 86400000,
                    updatedBy = "Nova Sterling"
                ),
                SystemConfigEntity(
                    key = "E2EE_RATCHET_STRICTNESS",
                    value = "STRICT_256",
                    category = "SECURITY",
                    description = "Enforces zero-knowledge forward secrecy ratcheting on all Vault peer messages",
                    lastUpdated = System.currentTimeMillis() - 86400000 * 2,
                    updatedBy = "Marcus Vance"
                ),
                SystemConfigEntity(
                    key = "ADS_PACING_RATIO",
                    value = "1_PER_4_POSTS",
                    category = "ADS",
                    description = "Frequency multiplier controlling how often sponsored cards appear in public feed",
                    lastUpdated = System.currentTimeMillis() - 86400000 * 5,
                    updatedBy = "Sarah Chen"
                ),
                SystemConfigEntity(
                    key = "COLLAB_HUDDLE_LIMIT",
                    value = "8_PEERS",
                    category = "COLLAB",
                    description = "Maximum simultaneous creators allowed in real-time WebRTC studio canvas",
                    lastUpdated = System.currentTimeMillis() - 86400000 * 3,
                    updatedBy = "Nova Sterling"
                )
            )
            adminDao.insertConfigs(initialConfigs)
        }

        // 4. Seed Initial Audit Logs if empty
        val existingLogs = adminDao.getAllAuditLogs().first()
        if (existingLogs.isEmpty()) {
            val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
            val now = System.currentTimeMillis()
            val initialLogs = listOf(
                AdminAuditLogEntity(
                    id = "audit_init_1",
                    timestamp = now - 3600000 * 5,
                    timestampFormatted = formatter.format(Date(now - 3600000 * 5)),
                    actorId = "user_me",
                    actorName = "Nova Sterling",
                    actorRole = RbacRole.SUPER_ADMIN.name,
                    actionType = "ROLE_ASSIGNED",
                    targetType = "USER",
                    targetId = "user_marcus",
                    details = "Promoted Marcus Vance to Security Admin with key rotation privileges",
                    signatureHash = CryptoManager.signAuditRecord("init_1")
                ),
                AdminAuditLogEntity(
                    id = "audit_init_2",
                    timestamp = now - 3600000 * 2,
                    timestampFormatted = formatter.format(Date(now - 3600000 * 2)),
                    actorId = "user_marcus",
                    actorName = "Marcus Vance",
                    actorRole = RbacRole.SECURITY_ADMIN.name,
                    actionType = "USER_BANNED",
                    targetType = "USER",
                    targetId = "user_sybil",
                    details = "Banned automated phishing node @free_tokens_bot",
                    signatureHash = CryptoManager.signAuditRecord("init_2")
                )
            )
            adminDao.insertAuditLogs(initialLogs)
        }
    }
}

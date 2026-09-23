package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_user_roles")
data class AdminUserRoleEntity(
    @PrimaryKey val userId: String,
    val userName: String,
    val userHandle: String,
    val avatarDrawable: String,
    val role: String,
    val assignedBy: String,
    val assignedAt: Long,
    val isBanned: Boolean = false,
    val banReason: String? = null,
    val auraLevel: Int = 85
)

@Entity(tableName = "admin_audit_logs")
data class AdminAuditLogEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val timestampFormatted: String,
    val actorId: String,
    val actorName: String,
    val actorRole: String,
    val actionType: String,
    val targetType: String,
    val targetId: String,
    val details: String,
    val signatureHash: String
)

@Entity(tableName = "moderation_tickets")
data class ModerationTicketEntity(
    @PrimaryKey val id: String,
    val targetType: String, // POST, REEL, COMMENT, USER
    val targetId: String,
    val targetPreviewText: String,
    val targetAuthor: String,
    val reporterHandle: String,
    val reportReason: String,
    val aiSeverityScore: Int, // 0 - 100
    val status: String, // PENDING, QUARANTINED, DISMISSED, RESOLVED
    val reviewerNotes: String? = null,
    val reviewedBy: String? = null,
    val reviewedAt: Long? = null
)

@Entity(tableName = "system_configs")
data class SystemConfigEntity(
    @PrimaryKey val key: String,
    val value: String,
    val category: String,
    val description: String,
    val lastUpdated: Long,
    val updatedBy: String
)

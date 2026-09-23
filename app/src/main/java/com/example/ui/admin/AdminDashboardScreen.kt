package com.example.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AdminAuditLogEntity
import com.example.data.local.AdminUserRoleEntity
import com.example.data.local.ModerationTicketEntity
import com.example.data.local.SystemConfigEntity
import com.example.data.rbac.AdminPersona
import com.example.data.rbac.RbacPermission
import com.example.data.rbac.RbacPolicy
import com.example.data.rbac.RbacRole
import com.example.ui.SocialViewModel
import com.example.ui.components.AuraAvatar
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet
import kotlinx.coroutines.delay

@Composable
fun AdminDashboardScreen(
    viewModel: SocialViewModel,
    onNavigateBack: () -> Unit
) {
    val activePersona by viewModel.activeAdminPersona.collectAsStateWithLifecycle()
    val userRoles by viewModel.adminUserRoles.collectAsStateWithLifecycle()
    val auditLogs by viewModel.adminAuditLogs.collectAsStateWithLifecycle()
    val tickets by viewModel.adminTickets.collectAsStateWithLifecycle()
    val configs by viewModel.adminConfigs.collectAsStateWithLifecycle()
    val statusMessage by viewModel.adminActionStatusMessage.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Users & Roles", "Moderation", "Governance", "Audit Trail")

    // Role assignment dialog state
    var editingUserRole by remember { mutableStateOf<AdminUserRoleEntity?>(null) }
    // Ban confirmation dialog state
    var banningUser by remember { mutableStateOf<AdminUserRoleEntity?>(null) }
    var banReasonInput by remember { mutableStateOf("Violation of Neural Community Guidelines") }
    // Show role matrix modal
    var showMatrixDialog by remember { mutableStateOf(false) }

    // Auto-dismiss status message after 4.5 seconds
    LaunchedEffect(statusMessage) {
        if (statusMessage != null) {
            delay(4500)
            viewModel.dismissAdminActionStatus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ================= Header =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = AuraNeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AURA BACKEND",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "ROLE-BASED ACCESS CONTROL (RBAC)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AuraNeonCyan,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // View Matrix Button
                OutlinedButton(
                    onClick = { showMatrixDialog = true },
                    modifier = Modifier.testTag("open_role_matrix_button"),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AuraNeonCyan),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(AuraNeonViolet, AuraNeonCyan))
                    )
                ) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Matrix", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // ================= Persona Switcher (RBAC Tester) =================
            PersonaSwitcherBar(
                personas = viewModel.availableAdminPersonas,
                activePersona = activePersona,
                onSelectPersona = { viewModel.switchAdminPersona(it) }
            )

            // ================= Status / Access Denied Notification Banner =================
            AnimatedVisibility(
                visible = statusMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                statusMessage?.let { msg ->
                    val isDenied = msg.contains("ACCESS DENIED", ignoreCase = true)
                    val bannerColor = if (isDenied) Color(0xFFEF4444) else AuraEmerald

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bannerColor.copy(alpha = 0.15f))
                            .border(1.dp, bannerColor, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = if (isDenied) Icons.Default.Block else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = bannerColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        IconButton(
                            onClick = { viewModel.dismissAdminActionStatus() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color.LightGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // ================= Navigation Tabs =================
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = AuraDarkSurface,
                contentColor = AuraNeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = AuraNeonCyan,
                        height = 2.5.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) Color.White else Color.Gray
                            )
                        }
                    )
                }
            }

            // ================= Tab Contents =================
            when (selectedTabIndex) {
                0 -> UsersAndRolesTab(
                    userRoles = userRoles,
                    activePersona = activePersona,
                    onEditRole = { editingUserRole = it },
                    onToggleBan = { user ->
                        if (user.isBanned) {
                            viewModel.toggleUserBan(user.userId, user.userName, false, null)
                        } else {
                            banningUser = user
                        }
                    }
                )
                1 -> ModerationQueueTab(
                    tickets = tickets,
                    activePersona = activePersona,
                    onResolveTicket = { id, status, notes ->
                        viewModel.resolveModerationTicket(id, status, notes)
                    }
                )
                2 -> SystemGovernanceTab(
                    configs = configs,
                    activePersona = activePersona,
                    onUpdateConfig = { k, v -> viewModel.updateSystemConfig(k, v) },
                    onRotateKeys = { viewModel.rotateMasterSecurityKeys() }
                )
                3 -> AuditTrailTab(
                    logs = auditLogs
                )
            }
        }

        // ================= Role Assignment Modal =================
        editingUserRole?.let { user ->
            RoleAssignmentDialog(
                user = user,
                onDismiss = { editingUserRole = null },
                onConfirm = { newRole ->
                    viewModel.assignUserRole(user.userId, user.userName, newRole)
                    editingUserRole = null
                }
            )
        }

        // ================= User Ban Confirmation Modal =================
        banningUser?.let { user ->
            AlertDialog(
                onDismissRequest = { banningUser = null },
                containerColor = AuraDarkCard,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirm User Ban", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Are you sure you want to ban ${user.userName} (${user.userHandle})? They will be immediately disconnected and blocked from posting.",
                            color = Color.LightGray,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = banReasonInput,
                            onValueChange = { banReasonInput = it },
                            label = { Text("Reason for Ban") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFEF4444),
                                unfocusedBorderColor = Color.DarkGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.toggleUserBan(user.userId, user.userName, true, banReasonInput)
                            banningUser = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("Ban Account", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { banningUser = null }) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
        }

        // ================= Permission Matrix Modal =================
        if (showMatrixDialog) {
            RoleMatrixDialog(onDismiss = { showMatrixDialog = false })
        }
    }
}

@Composable
private fun PersonaSwitcherBar(
    personas: List<AdminPersona>,
    activePersona: AdminPersona,
    onSelectPersona: (AdminPersona) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AuraDarkSurface)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SwitchAccount,
                    contentDescription = null,
                    tint = AuraNeonCyan,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ACTIVE PERSONA (RBAC TESTER):",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
            }

            RoleBadge(role = activePersona.role)
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(personas) { persona ->
                val isSelected = persona.id == activePersona.id
                val borderColor = if (isSelected) AuraNeonCyan else Color.DarkGray

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) AuraDarkCard else Color(0xFF131722))
                        .border(1.2.dp, borderColor, RoundedCornerShape(20.dp))
                        .clickable { onSelectPersona(persona) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("persona_chip_${persona.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AuraAvatar(drawableName = persona.avatarDrawable, size = 20.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = persona.name.split(" ").first(),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "• ${persona.role.name.take(3)}",
                            fontSize = 9.sp,
                            color = getRoleColor(persona.role),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UsersAndRolesTab(
    userRoles: List<AdminUserRoleEntity>,
    activePersona: AdminPersona,
    onEditRole: (AdminUserRoleEntity) -> Unit,
    onToggleBan: (AdminUserRoleEntity) -> Unit
) {
    val canManageRoles = RbacPolicy.hasPermission(activePersona.role, RbacPermission.MANAGE_ROLES)
    val canManageUsers = RbacPolicy.hasPermission(activePersona.role, RbacPermission.MANAGE_USERS)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_users_list"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Identity Privileges", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            text = "Role: ${activePersona.role.title} (Tier ${activePersona.role.tierLevel})",
                            fontSize = 11.sp,
                            color = getRoleColor(activePersona.role)
                        )
                    }
                    Row {
                        PrivilegeChip("Roles", canManageRoles)
                        Spacer(modifier = Modifier.width(4.dp))
                        PrivilegeChip("Users", canManageUsers)
                    }
                }
            }
        }

        items(userRoles, key = { it.userId }) { user ->
            val roleEnum = try { RbacRole.valueOf(user.role) } catch (e: Exception) { RbacRole.STANDARD_USER }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (user.isBanned) Color(0xFF261214) else AuraDarkCard
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (user.isBanned) Color(0xFFEF4444).copy(alpha = 0.5f) else Color(0xFF2D3748),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            AuraAvatar(drawableName = user.avatarDrawable, size = 40.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = user.userName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = user.userHandle,
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            RoleBadge(role = roleEnum)
                            if (user.isBanned) {
                                Text(
                                    text = "BANNED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFEF4444),
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }

                    if (user.isBanned && !user.banReason.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ban Reason: ${user.banReason}",
                            fontSize = 10.sp,
                            color = Color(0xFFFCA5A5),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    HorizontalDivider(
                        color = Color.DarkGray.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Assigned by ${user.assignedBy}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )

                        Row {
                            // Assign Role Button
                            OutlinedButton(
                                onClick = { onEditRole(user) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("assign_role_button_${user.userId}"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AuraNeonCyan)
                            ) {
                                Text("Assign Role", fontSize = 10.sp)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Ban/Unban Button
                            Button(
                                onClick = { onToggleBan(user) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("toggle_ban_button_${user.userId}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (user.isBanned) AuraEmerald else Color(0xFFEF4444)
                                )
                            ) {
                                Text(
                                    text = if (user.isBanned) "Unban" else "Ban",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModerationQueueTab(
    tickets: List<ModerationTicketEntity>,
    activePersona: AdminPersona,
    onResolveTicket: (String, String, String?) -> Unit
) {
    val canModerate = RbacPolicy.hasPermission(activePersona.role, RbacPermission.MODERATE_POSTS)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_moderation_list"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Moderation Authority", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            text = if (canModerate) "Active: Authorized to quarantine media" else "Restricted: Requires [post:moderate]",
                            fontSize = 11.sp,
                            color = if (canModerate) AuraEmerald else Color(0xFFEF4444)
                        )
                    }
                    PrivilegeChip("post:moderate", canModerate)
                }
            }
        }

        items(tickets, key = { it.id }) { ticket ->
            val isPending = ticket.status == "PENDING"
            val isQuarantined = ticket.status == "QUARANTINED"
            val statusColor = when (ticket.status) {
                "PENDING" -> Color(0xFFF59E0B)
                "QUARANTINED" -> Color(0xFFEF4444)
                "DISMISSED" -> Color.Gray
                else -> AuraEmerald
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AuraNeonViolet.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(ticket.targetType, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AuraNeonViolet)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ticket #${ticket.id.takeLast(6)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(statusColor.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(ticket.status, fontSize = 9.sp, fontWeight = FontWeight.Black, color = statusColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // AI Severity meter
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI Violation Score: ${ticket.aiSeverityScore}% (High Anomaly)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ticket.aiSeverityScore > 85) Color(0xFFEF4444) else Color(0xFFF59E0B)
                        )
                    }

                    Text(
                        text = "Reason: ${ticket.reportReason}",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Text(
                        text = "Reported by: ${ticket.reporterHandle} • Target: ${ticket.targetAuthor}",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preview snippet
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "\"${ticket.targetPreviewText}\"",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (isPending) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { onResolveTicket(ticket.id, "DISMISSED", "Dismissed by moderator") },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray)
                            ) {
                                Text("Dismiss", fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { onResolveTicket(ticket.id, "QUARANTINED", "Violates platform community standards") },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(30.dp)
                                    .testTag("quarantine_ticket_button_${ticket.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                            ) {
                                Text("Quarantine & Remove", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (ticket.reviewedBy != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Resolved by ${ticket.reviewedBy} • Notes: ${ticket.reviewerNotes ?: "N/A"}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemGovernanceTab(
    configs: List<SystemConfigEntity>,
    activePersona: AdminPersona,
    onUpdateConfig: (String, String) -> Unit,
    onRotateKeys: () -> Unit
) {
    val canConfig = RbacPolicy.hasPermission(activePersona.role, RbacPermission.SYSTEM_CONFIG)
    val canRotateKeys = RbacPolicy.hasPermission(activePersona.role, RbacPermission.SECURITY_KEYS)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_governance_list"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("System Config & Root Security", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            text = if (canConfig) "Config: Authorized" else "Config: Access Denied",
                            fontSize = 11.sp,
                            color = if (canConfig) AuraEmerald else Color(0xFFEF4444)
                        )
                    }
                    Row {
                        PrivilegeChip("sys:config", canConfig)
                        Spacer(modifier = Modifier.width(4.dp))
                        PrivilegeChip("sec:keys", canRotateKeys)
                    }
                }
            }
        }

        // Cryptographic Key Rotation Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131C2E)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AuraNeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = AuraNeonCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Root Cryptographic Session Keys", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AuraNeonCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("RSA-2048 / GCM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AuraNeonCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Forces an emergency zero-knowledge master key rotation across all client and peer nodes. Requires [sec:keys] authorization.",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onRotateKeys,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rotate_keys_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canRotateKeys) AuraNeonCyan else Color.DarkGray
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Rotate Master Cryptographic Keys", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Config Items
        items(configs, key = { it.key }) { config ->
            Card(
                colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF2D3748), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = config.key,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )

                        // If it's a boolean toggle
                        if (config.value == "ENABLED" || config.value == "DISABLED") {
                            val isChecked = config.value == "ENABLED"
                            Switch(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    val next = if (checked) "ENABLED" else "DISABLED"
                                    onUpdateConfig(config.key, next)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AuraNeonCyan,
                                    checkedTrackColor = AuraNeonCyan.copy(alpha = 0.4f),
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color.DarkGray
                                ),
                                modifier = Modifier.testTag("toggle_config_${config.key}")
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AuraNeonViolet.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = config.value,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AuraNeonViolet
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = config.description,
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Updated by ${config.updatedBy} • Category: ${config.category}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun AuditTrailTab(
    logs: List<AdminAuditLogEntity>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_audit_logs_list"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cryptographic Audit Stream (${logs.size} Events)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = AuraEmerald, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TAMPER-EVIDENT (SHA-256)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AuraEmerald)
                }
            }
        }

        items(logs, key = { it.id }) { log ->
            Card(
                colors = CardDefaults.cardColors(containerColor = AuraDarkCard),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when {
                                            log.actionType.contains("DENIED") -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                            log.actionType.contains("BAN") -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                            log.actionType.contains("ROLE") -> AuraNeonCyan.copy(alpha = 0.2f)
                                            else -> AuraNeonViolet.copy(alpha = 0.2f)
                                        }
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = log.actionType,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = when {
                                        log.actionType.contains("DENIED") -> Color(0xFFEF4444)
                                        log.actionType.contains("BAN") -> Color(0xFFEF4444)
                                        log.actionType.contains("ROLE") -> AuraNeonCyan
                                        else -> AuraNeonViolet
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${log.actorName} (${log.actorRole})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = log.timestampFormatted.split(" ").lastOrNull() ?: "",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = log.details,
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target: ${log.targetType}#${log.targetId.take(12)}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = log.signatureHash,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = AuraEmerald
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleAssignmentDialog(
    user: AdminUserRoleEntity,
    onDismiss: () -> Unit,
    onConfirm: (RbacRole) -> Unit
) {
    var selectedRole by remember {
        mutableStateOf(
            try { RbacRole.valueOf(user.role) } catch (e: Exception) { RbacRole.STANDARD_USER }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AuraDarkCard,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = AuraNeonCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Modify Administrative Role", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        text = {
            Column {
                Text(
                    text = "Assign a new RBAC governance tier to ${user.userName}:",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(12.dp))

                RbacRole.values().forEach { role ->
                    val isChosen = selectedRole == role
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isChosen) AuraDarkSurface else Color.Transparent)
                            .border(
                                1.dp,
                                if (isChosen) AuraNeonCyan else Color.DarkGray.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedRole = role }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(role.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(role.description, fontSize = 10.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        if (isChosen) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AuraNeonCyan, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedRole) },
                colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan)
            ) {
                Text("Apply Role", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

@Composable
private fun RoleMatrixDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AuraDarkCard,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = AuraNeonCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("RBAC Permissions Matrix", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    Text(
                        text = "Global Policy Mapping across all administrative roles:",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(RbacRole.values()) { role ->
                    val perms = RbacPolicy.getPermissionsForRole(role)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AuraDarkSurface),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(role.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = getRoleColor(role))
                                Text("Tier ${role.tierLevel}", fontSize = 10.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            if (perms.isEmpty()) {
                                Text("No Administrative Privileges", fontSize = 10.sp, color = Color.DarkGray)
                            } else {
                                Text(
                                    text = perms.joinToString(", ") { it.displayName },
                                    fontSize = 10.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan)) {
                Text("Close", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun PrivilegeChip(label: String, granted: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (granted) AuraEmerald.copy(alpha = 0.15f) else Color.DarkGray.copy(alpha = 0.3f))
            .border(
                1.dp,
                if (granted) AuraEmerald.copy(alpha = 0.5f) else Color.DarkGray,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (granted) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (granted) AuraEmerald else Color.Gray,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (granted) AuraEmerald else Color.Gray
            )
        }
    }
}

@Composable
private fun RoleBadge(role: RbacRole) {
    val color = getRoleColor(role)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = role.title.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            color = color
        )
    }
}

private fun getRoleColor(role: RbacRole): Color {
    return when (role) {
        RbacRole.SUPER_ADMIN -> Color(0xFFEF4444)
        RbacRole.SECURITY_ADMIN -> AuraEmerald
        RbacRole.CONTENT_MODERATOR -> AuraNeonCyan
        RbacRole.ADS_MANAGER -> Color(0xFFF59E0B)
        RbacRole.CREATOR_PARTNER -> AuraHotPink
        RbacRole.STANDARD_USER -> Color.Gray
    }
}

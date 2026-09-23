package com.example.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AuraNotification
import com.example.data.model.NotificationType
import com.example.ui.SocialViewModel
import com.example.ui.components.AuraAvatar
import com.example.ui.theme.AuraDarkBackground
import com.example.ui.theme.AuraDarkCard
import com.example.ui.theme.AuraDarkCardBorder
import com.example.ui.theme.AuraDarkSurface
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraHotPink
import com.example.ui.theme.AuraNeonCyan
import com.example.ui.theme.AuraNeonViolet

@Composable
fun NotificationsScreen(
    viewModel: SocialViewModel,
    onNavigate: (String) -> Unit
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("All") }

    val filters = listOf("All", "Collabs & Invites", "Encrypted DMs", "Sparks & AI")

    val filteredList = when (selectedFilter) {
        "Collabs & Invites" -> notifications.filter { it.type == NotificationType.COLLAB_INVITE }
        "Encrypted DMs" -> notifications.filter { it.type == NotificationType.ENCRYPTED_MESSAGE || it.type == NotificationType.SAFETY_AUDIT }
        "Sparks & AI" -> notifications.filter { it.type == NotificationType.SPARK_LIKE || it.type == NotificationType.AI_REMIX }
        else -> notifications
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraDarkBackground)
            .testTag("notifications_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(AuraNeonViolet, AuraNeonCyan))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Text(
                    text = "AURA NOTIFICATIONS",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            IconButton(
                onClick = { viewModel.markAllNotificationsAsRead() },
                modifier = Modifier.testTag("mark_all_read_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Mark All Read",
                    tint = AuraNeonCyan,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            items(filters) { f ->
                val isSelected = selectedFilter == f
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) AuraNeonViolet else AuraDarkCard)
                        .clickable { selectedFilter = f }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = f,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color.LightGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Notifications List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No notifications in this frequency.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { notif ->
                    NotificationCard(
                        notif = notif,
                        onRead = { viewModel.markNotificationAsRead(notif.id) },
                        onAction = {
                            viewModel.markNotificationAsRead(notif.id)
                            notif.targetRoute?.let { onNavigate(it) }
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notif: AuraNotification,
    onRead: () -> Unit,
    onAction: () -> Unit
) {
    val typeIcon = when (notif.type) {
        NotificationType.COLLAB_INVITE -> Icons.Default.Group
        NotificationType.ENCRYPTED_MESSAGE -> Icons.Default.Lock
        NotificationType.SPARK_LIKE -> Icons.Default.Favorite
        NotificationType.AI_REMIX -> Icons.Default.AutoAwesome
        NotificationType.SAFETY_AUDIT -> Icons.Default.Lock
        NotificationType.SYSTEM_ALERT -> Icons.Default.Notifications
    }

    val typeColor = when (notif.type) {
        NotificationType.COLLAB_INVITE -> AuraNeonCyan
        NotificationType.ENCRYPTED_MESSAGE -> AuraNeonViolet
        NotificationType.SPARK_LIKE -> AuraHotPink
        NotificationType.AI_REMIX -> AuraEmerald
        NotificationType.SAFETY_AUDIT -> AuraNeonCyan
        NotificationType.SYSTEM_ALERT -> AuraHotPink
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (!notif.isRead) AuraDarkCard else AuraDarkSurface)
            .border(
                width = if (!notif.isRead) 1.dp else 0.5.dp,
                color = if (!notif.isRead) typeColor.copy(alpha = 0.4f) else AuraDarkCardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onRead() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Actor Avatar with tiny type badge
            Box(contentAlignment = Alignment.BottomEnd) {
                AuraAvatar(
                    drawableName = notif.actorAvatarDrawable,
                    size = 44.dp,
                    hasGlowBorder = !notif.isRead
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(typeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = typeIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                }
            }

            // Description
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = notif.actorName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = " • ${notif.timestampText}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = notif.description,
                    fontSize = 12.sp,
                    color = if (!notif.isRead) Color.White.copy(alpha = 0.9f) else Color.LightGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Action Button
            notif.actionLabel?.let { actionLabel ->
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = typeColor.copy(alpha = 0.2f),
                        contentColor = typeColor
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = actionLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

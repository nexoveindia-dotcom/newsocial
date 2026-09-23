package com.example.data.model

enum class NotificationType {
    COLLAB_INVITE,
    ENCRYPTED_MESSAGE,
    SPARK_LIKE,
    AI_REMIX,
    SAFETY_AUDIT,
    SYSTEM_ALERT
}

data class AuraNotification(
    val id: String,
    val type: NotificationType,
    val actorName: String,
    val actorAvatarDrawable: String,
    val title: String,
    val description: String,
    val timestampText: String,
    val isRead: Boolean = false,
    val actionLabel: String? = null,
    val targetRoute: String? = null,
    val targetId: String? = null
)

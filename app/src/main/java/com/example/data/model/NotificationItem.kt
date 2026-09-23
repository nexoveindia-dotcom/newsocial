package com.example.data.model

enum class NotificationType {
    COLLAB_INVITE,
    SPARK_LIKE,
    E2EE_SECURITY,
    MENTION,
    AI_CURATION,
    CREATOR_TIP
}

data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val subtitle: String,
    val timestampText: String,
    val isRead: Boolean = false,
    val avatarDrawableName: String,
    val actionButtonText: String? = null,
    val targetPostId: String? = null,
    val targetConversationId: String? = null,
    val isCollabAccepted: Boolean? = null // null = pending, true = accepted, false = declined
)

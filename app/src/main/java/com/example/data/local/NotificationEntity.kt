package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val type: String,
    val actorName: String,
    val actorAvatarDrawable: String,
    val title: String,
    val description: String,
    val timestampText: String,
    val isRead: Boolean,
    val actionLabel: String?,
    val targetRoute: String?,
    val targetId: String?,
    val createdAt: Long = System.currentTimeMillis()
)

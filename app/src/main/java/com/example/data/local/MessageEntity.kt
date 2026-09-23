package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val isFromMe: Boolean,
    val encryptedPayload: String,
    val decryptedText: String,
    val timestamp: Long,
    val timestampFormatted: String,
    val isEncrypted: Boolean,
    val selfDestructSeconds: Int,
    val expiresAt: Long,
    val isIncinerated: Boolean,
    val isVoiceNote: Boolean,
    val voiceDurationSeconds: Int,
    val reaction: String? = null,
    val deliveryStatus: String = "READ",
    val attachmentDrawable: String? = null
)

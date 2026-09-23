package com.example.data.model

data class ChatConversation(
    val id: String,
    val participant: UserProfile,
    val lastMessageText: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isE2eeVerified: Boolean = true,
    val hasSelfDestructActive: Boolean = false,
    val safetyNumber: String = "49201 82941 77301 22910",
    val isGroup: Boolean = false,
    val groupMembersCount: Int = 1,
    val isTyping: Boolean = false,
    val typingStatusText: String = ""
)

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val isFromMe: Boolean,
    val encryptedPayload: String, // Ciphertext in Base64
    val decryptedText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val timestampFormatted: String,
    val isEncrypted: Boolean = true,
    val selfDestructSeconds: Int = 0, // 0 = no expiration, 5, 30, 60
    val expiresAt: Long = 0L,
    val isIncinerated: Boolean = false,
    val isVoiceNote: Boolean = false,
    val voiceDurationSeconds: Int = 0,
    val isVoicePlaying: Boolean = false,
    val showCipherRaw: Boolean = false,
    val reaction: String? = null,
    val deliveryStatus: String = "READ", // "SENDING", "SENT", "DELIVERED", "READ"
    val attachmentDrawable: String? = null
)

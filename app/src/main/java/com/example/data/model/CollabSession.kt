package com.example.data.model

data class Collaborator(
    val id: String,
    val name: String,
    val avatarDrawableName: String,
    val colorHex: Long,
    val currentAction: String,
    val isOnline: Boolean = true,
    val isVoiceConnected: Boolean = false
)

enum class LayerType {
    TEXT,
    STICKER,
    GLOW_EFFECT,
    AUDIO_TAG
}

data class CanvasLayer(
    val id: String,
    val type: LayerType,
    val title: String,
    val content: String,
    val offsetX: Float,
    val offsetY: Float,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val colorHex: Long = 0xFFFFFFFF,
    val lastEditedBy: String = "You"
)

data class CollabActivityEvent(
    val id: String,
    val collaboratorName: String,
    val actionText: String,
    val timestampText: String
)

data class CollabSession(
    val id: String,
    val title: String,
    val baseImageResName: String,
    val collaborators: List<Collaborator>,
    val layers: List<CanvasLayer>,
    val activityEvents: List<CollabActivityEvent>,
    val activeFilterName: String = "Prism Glow",
    val filterIntensity: Float = 0.7f,
    val isVoiceHuddleActive: Boolean = false
)

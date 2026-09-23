package com.example.data.model

data class Reel(
    val id: String,
    val author: UserProfile,
    val videoDrawableName: String,
    val audioTrackTitle: String,
    val audioTrackArtist: String,
    val caption: String,
    val likesCount: Int = 1840,
    val commentsCount: Int = 142,
    val sharesCount: Int = 98,
    val remixesCount: Int = 34,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val filterName: String = "Neon Glitch",
    val durationSec: Int = 15,
    val isCollaborative: Boolean = false,
    val coAuthorName: String? = null,
    val tags: List<String> = listOf("#NeuralPulse", "#AuraReel", "#CyberAesthetic")
)

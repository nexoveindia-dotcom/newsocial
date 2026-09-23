package com.example.data.model

data class UserProfile(
    val id: String,
    val name: String,
    val handle: String,
    val avatarDrawableName: String = "post_cyber_creator_1790200662289",
    val isVerified: Boolean = true,
    val auraLevel: Int = 94,
    val bio: String = "Digital Alchemist • Co-creating visual realms across Web3 & Neural Networks",
    val followersCount: Int = 14200,
    val followingCount: Int = 412,
    val e2eeKeyFingerprint: String = "49201 82941 77301 22910 84910 11928"
)

data class Comment(
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val text: String,
    val timestamp: String,
    val aiSentiment: String = "Inspiring", // Inspiring, Constructive, Hyped, Philosophical
    val likesCount: Int = 12
)

data class Post(
    val id: String,
    val author: UserProfile,
    val coAuthors: List<UserProfile> = emptyList(), // Collaborative Multi-creator posts!
    val imageDrawableName: String,
    val caption: String,
    val timestampText: String,
    val likesCount: Int,
    val commentsCount: Int,
    val vibeTag: String, // "Cyberpunk", "Neo-Chill", "Quantum Tech", "Ambient Art", "Deep Lore"
    val aiAffinityScore: Int = 98, // 0-100% personalized match
    val aiExplanation: String = "Matches your high affinity for neural aesthetics and collaborative digital art.",
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val isCollaborative: Boolean = false,
    val collabDraftTitle: String? = null
)

data class StoryItem(
    val id: String,
    val creatorName: String,
    val creatorHandle: String,
    val avatarDrawableName: String,
    val imageDrawableName: String,
    val isSeen: Boolean = false,
    val isLiveCollab: Boolean = false,
    val liveCollabParticipants: Int = 0,
    val caption: String = "",
    val musicTrack: String? = null,
    val stickerText: String? = null,
    val timestampText: String = "2h ago",
    val filterName: String = "Normal"
)

data class LiveStreamSession(
    val id: String,
    val host: UserProfile,
    val coHost: UserProfile? = null,
    val title: String,
    val category: String,
    val viewerCount: Int = 1420,
    val isBroadcasting: Boolean = true,
    val videoBackgroundDrawable: String = "reel_cyber_synth_1790201507030",
    val coHostBackgroundDrawable: String? = "reel_neon_oasis_1790201522014"
)

data class LiveComment(
    val id: String,
    val senderName: String,
    val senderAvatar: String,
    val text: String,
    val isHost: Boolean = false,
    val isSparked: Boolean = false
)

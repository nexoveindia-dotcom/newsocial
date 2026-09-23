package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarRes: String,
    val coAuthorsFormatted: String, // comma separated
    val imageResName: String,
    val caption: String,
    val timestampText: String,
    val likesCount: Int,
    val commentsCount: Int,
    val vibeTag: String,
    val aiAffinityScore: Int,
    val aiExplanation: String,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val isCollaborative: Boolean
)

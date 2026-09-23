package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reels")
data class ReelEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarRes: String,
    val videoDrawableName: String,
    val audioTrackTitle: String,
    val audioTrackArtist: String,
    val caption: String,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val remixesCount: Int,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val filterName: String,
    val durationSec: Int,
    val isCollaborative: Boolean,
    val coAuthorName: String?,
    val timestamp: Long = System.currentTimeMillis()
)

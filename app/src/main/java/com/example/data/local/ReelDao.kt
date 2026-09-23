package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelDao {
    @Query("SELECT * FROM reels ORDER BY timestamp DESC")
    fun getAllReels(): Flow<List<ReelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReels(reels: List<ReelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReel(reel: ReelEntity)

    @Query("UPDATE reels SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :id")
    suspend fun updateLike(id: String, isLiked: Boolean, delta: Int)

    @Query("UPDATE reels SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSave(id: String, isSaved: Boolean)

    @Query("DELETE FROM reels WHERE id = :id")
    suspend fun deleteReel(id: String)
}

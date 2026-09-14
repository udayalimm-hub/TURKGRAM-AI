package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPostDao {
    @Query("SELECT * FROM saved_posts ORDER BY createdAt DESC")
    fun getAllSavedPosts(): Flow<List<SavedPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: SavedPostEntity): Long

    @Query("DELETE FROM saved_posts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM saved_posts WHERE concept = :concept AND caption = :caption")
    suspend fun deleteByContent(concept: String, caption: String)

    @Query("SELECT COUNT(*) FROM saved_posts WHERE concept = :concept")
    suspend fun countByConcept(concept: String): Int
}

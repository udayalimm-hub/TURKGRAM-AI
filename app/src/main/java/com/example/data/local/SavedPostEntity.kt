package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_posts")
data class SavedPostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val concept: String,
    val scenario: String,
    val caption: String,
    val hashtagsJson: String, // Comma-separated or serialized
    val aiPrompt: String? = null,
    val category: String = "IDEAS",
    val createdAt: Long = System.currentTimeMillis()
)

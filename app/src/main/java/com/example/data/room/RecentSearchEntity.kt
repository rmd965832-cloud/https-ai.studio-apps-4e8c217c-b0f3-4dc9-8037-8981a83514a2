package com.example.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val query: String,
    val intentAction: String,
    val languageCode: String,
    val timestamp: Long = System.currentTimeMillis()
)

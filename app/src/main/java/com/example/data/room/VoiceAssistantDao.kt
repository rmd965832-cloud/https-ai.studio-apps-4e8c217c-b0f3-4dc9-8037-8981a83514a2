package com.example.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceAssistantDao {

    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT 20")
    fun getRecentSearches(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(search: RecentSearchEntity)

    @Query("DELETE FROM recent_searches WHERE id = :id")
    suspend fun deleteRecentSearch(id: Long)

    @Query("DELETE FROM recent_searches")
    suspend fun clearRecentSearches()

    @Query("SELECT * FROM favorite_songs ORDER BY addedAt DESC")
    fun getFavoriteSongs(): Flow<List<FavoriteSongEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_songs WHERE videoId = :videoId)")
    fun isFavorite(videoId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(song: FavoriteSongEntity)

    @Query("DELETE FROM favorite_songs WHERE videoId = :videoId")
    suspend fun removeFavorite(videoId: String)
}

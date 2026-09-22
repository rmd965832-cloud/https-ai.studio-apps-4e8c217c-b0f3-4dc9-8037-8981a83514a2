package com.example.data.room

import com.example.model.SongItem
import kotlinx.coroutines.flow.Flow

class VoiceAssistantRepository(private val dao: VoiceAssistantDao) {

    val recentSearches: Flow<List<RecentSearchEntity>> = dao.getRecentSearches()
    val favoriteSongs: Flow<List<FavoriteSongEntity>> = dao.getFavoriteSongs()

    fun isFavorite(videoId: String): Flow<Boolean> = dao.isFavorite(videoId)

    suspend fun saveSearch(query: String, intentAction: String, languageCode: String) {
        if (query.isNotBlank()) {
            dao.insertRecentSearch(
                RecentSearchEntity(
                    query = query.trim(),
                    intentAction = intentAction,
                    languageCode = languageCode
                )
            )
        }
    }

    suspend fun deleteSearch(id: Long) = dao.deleteRecentSearch(id)

    suspend fun clearAllSearches() = dao.clearRecentSearches()

    suspend fun toggleFavorite(song: SongItem, isFav: Boolean) {
        if (isFav) {
            dao.removeFavorite(song.videoId)
        } else {
            dao.addFavorite(FavoriteSongEntity.fromSongItem(song))
        }
    }
}

package com.example.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.SongItem

@Entity(tableName = "favorite_songs")
data class FavoriteSongEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val channelTitle: String,
    val thumbnailUrl: String,
    val addedAt: Long = System.currentTimeMillis()
) {
    fun toSongItem(): SongItem = SongItem(
        videoId = videoId,
        title = title,
        channelTitle = channelTitle,
        thumbnailUrl = thumbnailUrl,
        isFavorite = true
    )

    companion object {
        fun fromSongItem(item: SongItem): FavoriteSongEntity = FavoriteSongEntity(
            videoId = item.videoId,
            title = item.title,
            channelTitle = item.channelTitle,
            thumbnailUrl = item.thumbnailUrl
        )
    }
}

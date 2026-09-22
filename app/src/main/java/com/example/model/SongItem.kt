package com.example.model

data class SongItem(
    val videoId: String,
    val title: String,
    val channelTitle: String = "",
    val thumbnailUrl: String = "",
    val description: String = "",
    val durationText: String = "",
    val isFavorite: Boolean = false
) {
    val youtubeWatchUrl: String get() = "https://www.youtube.com/watch?v=$videoId"
    val youtubeShareText: String get() = "🎵 $title\n$youtubeWatchUrl"
}

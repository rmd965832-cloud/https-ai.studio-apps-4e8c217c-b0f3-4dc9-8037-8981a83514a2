package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YouTubeSearchResponse(
    @Json(name = "items") val items: List<YouTubeSearchItem>? = null
)

@JsonClass(generateAdapter = true)
data class YouTubeSearchItem(
    @Json(name = "id") val id: YouTubeId? = null,
    @Json(name = "snippet") val snippet: YouTubeSnippet? = null
)

@JsonClass(generateAdapter = true)
data class YouTubeId(
    @Json(name = "kind") val kind: String? = null,
    @Json(name = "videoId") val videoId: String? = null
)

@JsonClass(generateAdapter = true)
data class YouTubeSnippet(
    @Json(name = "title") val title: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "channelTitle") val channelTitle: String? = null,
    @Json(name = "publishedAt") val publishedAt: String? = null,
    @Json(name = "thumbnails") val thumbnails: YouTubeThumbnails? = null
)

@JsonClass(generateAdapter = true)
data class YouTubeThumbnails(
    @Json(name = "high") val high: YouTubeThumbnailDetail? = null,
    @Json(name = "medium") val medium: YouTubeThumbnailDetail? = null,
    @Json(name = "default") val defaultThumb: YouTubeThumbnailDetail? = null
)

@JsonClass(generateAdapter = true)
data class YouTubeThumbnailDetail(
    @Json(name = "url") val url: String? = null
)

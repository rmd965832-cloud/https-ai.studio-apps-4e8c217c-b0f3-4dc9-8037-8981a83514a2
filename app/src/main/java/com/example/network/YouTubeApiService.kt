package com.example.network

import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {

    @GET("youtube/v3/search")
    suspend fun searchVideos(
        @Query("key") apiKey: String,
        @Query("part") part: String = "snippet",
        @Query("type") type: String = "video",
        @Query("videoCategoryId") videoCategoryId: String = "10", // Music category
        @Query("maxResults") maxResults: Int = 15,
        @Query("q") query: String
    ): YouTubeSearchResponse
}

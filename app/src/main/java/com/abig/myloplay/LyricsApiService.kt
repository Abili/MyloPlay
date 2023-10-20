package com.abig.myloplay

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LyricsApiService {
    @GET("v1/{title}") // Define the dynamic part of the URL as a placeholder
    fun searchLyricsByTitle(
        @Path("title") title: String
    ): Call<LyricsResponse> // Replace with the response model class

    @GET("v1/{artist}/{title}") // Define the dynamic parts of the URL as placeholders
    fun searchLyricsByTitleAndArtist(
        @Path("artist") artist: String,
        @Path("title") title: String
    ): Call<LyricsResponse> // Replace with the response model class

    // Add other API endpoints and methods as needed
}


package com.abig.myloplay

import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Define a data class to represent the API response
data class LyricsResponse(val lyrics: String)

// LyricsApiClient class to manage API requests
class LyricsApiClient(private val lyricsTextView: TextView) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.lyrics.ovh/v1/") // Update the base URL without the artist and title
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val lyricsApi = retrofit.create(LyricsApiService::class.java)

    fun fetchLyrics(songTitle: String, artistName: String) {
        val call = if (artistName.isEmpty()) {
            lyricsApi.searchLyricsByTitle(songTitle)
        } else {
            lyricsApi.searchLyricsByTitleAndArtist(artistName, songTitle)
        }

        call.enqueue(object : Callback<LyricsResponse> {
            override fun onResponse(call: Call<LyricsResponse>, response: Response<LyricsResponse>) {
                if (response.isSuccessful) {
                    val lyrics = response.body()?.lyrics ?: "Lyrics not found"
                    // Update your UI to display the lyrics
                    lyricsTextView.text = lyrics
                } else {
                    // Handle errors or display a message if lyrics are not found
                    lyricsTextView.text = "Lyrics not found"
                }
            }

            override fun onFailure(call: Call<LyricsResponse>, t: Throwable) {
                // Handle network request failure
                lyricsTextView.text = "Failed to fetch lyrics"
            }
        })
    }
}

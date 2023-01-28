package com.abig.myloplay

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.abig.myloplay.databinding.ActivityMyloPlayerBinding
import com.bumptech.glide.Glide

class MyloPlayer : AppCompatActivity() {
    private lateinit var binding: ActivityMyloPlayerBinding
    private lateinit var player: MediaPlayer
    private var currentIndex: Int = 0
    private var songIndex: Int = 0
    private val playlists = mutableListOf<Song>()
    private val songs = mutableListOf<Song>()
    private var songUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyloPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        songIndex = intent.getIntExtra(PlaylistDetailsActivity.EXTRA_PLAYLIST_ID, 0)
        currentIndex = songIndex
        // Retrieve the song URI from the intent extra
        songUri = intent.getStringExtra(EXTRA_SONG_URI)

        binding.playBtn.setOnClickListener {
            //play song
            playSong()
        }
        binding.nextBtn.setOnClickListener {
            //skip to next song
            skipToNextSong()
        }
        binding.prevBtn.setOnClickListener {
            //skip to prev song
            SkipToPrevSong()
        }


    }

    private fun playSong() {
        player = MediaPlayer.create(this, Uri.parse(songUri))
        player.start()
        binding.playBtn.visibility = View.GONE
        Glide.with(this)
            .load(R.drawable.pause)
            .into(binding.playBtn)
    }

    private fun skipToNextSong() {
        // Here you should check if there is a next song,
        // and then update the player to play that song
        // and also update the UI to reflect the change.
        if (player.isPlaying) {
            player.stop()
        }
        //Assuming you have a list of songs
        if (currentIndex < playlists.size - 1) { // check if not already at the last song
            currentIndex++
            playlists[currentIndex]
            //do something with the next song, like playing it
        }
        val nextIndex = (currentIndex + 1) % songs.size
        currentIndex = nextIndex
        val nextSong = songs[nextIndex]
        player.reset()
        player.setDataSource(nextSong.uri)
        player.prepare()
        player.start()
        // update the ui
        binding.songTitle.text = nextSong.name
        binding.songArtist.text = nextSong.artist
        binding.songDuration.text = nextSong.duration
        Glide.with(binding.root)
            .load(nextSong.uri)
            .centerCrop()
            .placeholder(R.drawable.utubemusic)
            .into(binding.playerBackground)
    }

    private fun SkipToPrevSong() {
        // Here you should check if there is a previous song,
        // and then update the player to play thatsong
        // and also update the UI to reflect the change.
        if (player.isPlaying) {
            player.stop()
        }
        val prevIndex = (currentIndex - 1 + songs.size) % songs.size
        currentIndex = prevIndex
        val prevSong = songs[prevIndex]
        player.reset()
        player.setDataSource(prevSong.uri)
        player.prepare()
        player.start()
        // update the ui
        binding.songTitle.text = prevSong.name
        binding.songArtist.text = prevSong.artist
        binding.songDuration.text = prevSong.duration
        Glide.with(binding.root)
            .load(prevSong.uri)
            .centerCrop()
            .placeholder(R.drawable.utubemusic)
            .into(binding.playerBackground)
    }

    companion object {
        const val EXTRA_SONG_URI = "song_uri"
    }
}
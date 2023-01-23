package com.abig.myloplay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.abig.myloplay.databinding.ActivityEditPlaylistBinding

class EditPlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPlaylistBinding
    private lateinit var viewModel: EditPlaylistViewModel
    private lateinit var playlist: Playlist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[EditPlaylistViewModel::class.java]

        // Get the playlist ID from the intent
        val playlistId = intent.getLongExtra(EXTRA_PLAYLIST_ID, 0)
        if (playlistId == 0L) {
            // If no playlist ID was provided, finish the activity
            finish()
            return
        }

        // Get the playlist from the view model
        viewModel.playlist.observe(this) {
            this.playlist = it
            binding.activity!!.playlist
        }

        // Set up the save button click listener
        binding.saveButton.setOnClickListener {
            savePlaylist()
        }
    }

    private fun savePlaylist() {
        // Update the playlist name and song IDs
        playlist.name = binding.editTextPlaylistName.text.toString()
        playlist.songIds = (viewModel.selectedSongIds.value ?: emptyList()) as List<String>

        // Update the playlist in the database
        viewModel.updatePlaylist(playlist)

        // Finish the activity
        finish()
    }

    companion object {
        const val EXTRA_PLAYLIST_ID = "playlist_id"
        const val EXTRA_PLAYLIST = "EXTRA_PLAYLIST"

    }
}


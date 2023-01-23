package com.abig.myloplay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.abig.myloplay.databinding.ActivityPlaylistDetailsBinding

class PlaylistDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var playlist: Playlist
    private lateinit var playlistId: String
    private lateinit var repository: PlaylistRepository

    private val viewModel: PlaylistDetailsViewModel by viewModels {
        PlaylistDetailsViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playlistId = intent.getStringExtra(EXTRA_PLAYLIST_ID)!!
        viewModel.playlist.observe(this, Observer {
            this.playlist = it
            binding.textViewPlaylistName.text = playlist.name
            // Other UI updates
        })

        binding.buttonEditPlaylist.setOnClickListener {
            val intent = Intent(this, EditPlaylistActivity::class.java).apply {
                intent.putExtra(EditPlaylistActivity.EXTRA_PLAYLIST, playlist)
            }
            startActivityForResult(intent, REQUEST_EDIT_PLAYLIST)
        }

        // Other code for setting up the activity
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT_PLAYLIST && resultCode == RESULT_OK) {
            // Update the playlist in the view model and UI
        }
    }

    companion object {
        const val EXTRA_PLAYLIST_ID = "playlist_id"
        const val REQUEST_EDIT_PLAYLIST = 1

    }

}

package com.abig.myloplay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.ActivityPlaylistDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class PlaylistDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var playlist: Playlist
    private lateinit var playlistId: String
    private lateinit var repository: PlaylistRepository
    private lateinit var playlistsRef: DatabaseReference
    private lateinit var viewModel: PlaylistDetailsViewModel
    private lateinit var auth:FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = PlaylistRepository()
        auth = FirebaseAuth.getInstance()

        val viewModelFactory = PlaylistDetailsViewModel.Factory(repository)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(PlaylistDetailsViewModel::class.java)


        playlistsRef = FirebaseDatabase.getInstance().getReference("playlists")

        playlistId = intent.getStringExtra(EXTRA_PLAYLIST_ID)!!
        val playlistRef = playlistsRef.child(playlistId)

        viewModel.playlist.observe(this) {
            playlist = it
            binding.textViewPlaylistName.text = playlist.name
            // Other UI updates
        }


        val songsAdapter = SongsAdapter()

        // Set the adapters for the RecyclerViews
        binding.recyclerViewSongs.adapter = songsAdapter


        // Set the layout managers for the RecyclerViews
        binding.recyclerViewSongs.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false
        )
        binding.buttonEditPlaylist.setOnClickListener{
            editPlaylist()
        }


        playlistRef.child("songIds").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                songsAdapter.clear()
                val songIds = snapshot.value as List<String>

                for (songId in songIds) {
                    songsAdapter.add(Song(songId))
                }
                songsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })



        // Other code for setting up the activity
    }

    private fun editPlaylist() {
        val userID = auth.currentUser!!.uid
        val editPlaylistRef = FirebaseDatabase.getInstance().reference.child("users")
            .child(userID).child("playlists")
        editPlaylistRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (plSnapshot in snapshot.children) {
                    val playlistName = plSnapshot.child("playlistName").getValue(String::class.java)


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    // Inside PlaylistDetailsActivity, where you handle the action to add a song


    @Deprecated("Deprecated in Java")
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

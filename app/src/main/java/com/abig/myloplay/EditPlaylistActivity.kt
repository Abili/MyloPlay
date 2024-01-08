package com.abig.myloplay

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.ActivityEditPlaylistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// In EditPlaylistActivity

class EditPlaylistActivity : AppCompatActivity() {
    // Constants
    companion object {
        const val EXTRA_PLAYLIST_ID = "extra_playlist_id"
    }

    // Variables
    private lateinit var playlistId: String
    private lateinit var binding: ActivityEditPlaylistBinding
    private lateinit var songsAdapter: EditPlaylistSongsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve playlist ID from intent
        playlistId = intent.getStringExtra(EXTRA_PLAYLIST_ID) ?: ""

        // Initialize UI elements
        songsAdapter = EditPlaylistSongsAdapter()

        // Load playlist details and populate UI
        loadPlaylistDetails()

        // Implement Save button click listener
    }

    private fun loadPlaylistDetails() {
        // Retrieve playlist details based on the playlistId
        // Update UI elements with playlist name and songs

        val databaseRef = FirebaseDatabase.getInstance().reference
            .child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("playlists").child(playlistId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playlistName = snapshot.child("playlistName").getValue(String::class.java)
                binding.editTextPlaylistName.setText(playlistName)

                // Load and display songs
                val songsSnapshot = snapshot.child("songs")
                val songs = mutableListOf<AudioFile>()

                for (songSnapshot in songsSnapshot.children) {
                    // Parse song details and add to the songs list
                    val song = AudioFile(
                        songSnapshot.key,
                        songSnapshot.child("downloadUrl").getValue(String::class.java) ?: "",
                        songSnapshot.child("displayName").getValue(String::class.java) ?: "",
                        songSnapshot.child("artist").getValue(String::class.java) ?: "",
                        songSnapshot.child("duration").getValue(String::class.java) ?: "",
                        //songSnapshot.child("albumArt").getValue(Bitmap::class.java) ?: ""
                    )
                    songs.add(song)
                }

                // Update the RecyclerView adapter with the songs
                songsAdapter.setSongs(songs)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }

    private fun savePlaylistChanges() {
        val newPlaylistName = binding.editTextPlaylistName.text.toString().trim()

        if (newPlaylistName.isNotBlank()) {
            // Update playlist name in the database
            val databaseRef = FirebaseDatabase.getInstance().reference
                .child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("playlists").child(playlistId)

            databaseRef.child("playlistName").setValue(newPlaylistName)

            // Update playlist songs in the database
            val songsRef = databaseRef.child("songs")
            val updatedSongs = songsAdapter.getSongs()

            // Clear the previous songs data for this playlist
            songsRef.removeValue()

            // Iterate through the updated songs and store them in the Firebase Realtime Database
            updatedSongs.forEach { audioFile ->
                val songDetails = mapOf(
                    "displayName" to audioFile.title,
                    "artist" to audioFile.artist,
                    "duration" to audioFile.duration,
                    "downloadUrl" to audioFile.downloadUrl,
                    "albumArt" to audioFile.albumArt
                )

                // Create a unique ID for the song entry
                val songEntryRef = songsRef.push()

                // Save the song details to the Firebase Realtime Database
                songEntryRef.setValue(songDetails)
            }

            showToast("Changes saved successfully.")
            finish() // Finish the activity after saving changes
        } else {
            showToast("Playlist name cannot be empty.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

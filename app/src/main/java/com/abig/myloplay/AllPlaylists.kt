package com.abig.myloplay

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.ActivityAllPlaylistsBinding
import com.abig.myloplay.databinding.DialogAddPlaylistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AllPlaylists : AppCompatActivity() {

    private lateinit var othersPlayListAdapter: AllPlaylistAdapter
    private lateinit var currentPlayListAdapter: AllPlaylistAdapter
    private val requestCodePermission = 123
    private val requestCodeAudioSelection = 456
    private lateinit var binding: ActivityAllPlaylistsBinding
    private val selectedAudioFiles = mutableListOf<AudioFile>()
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllPlaylistsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        binding.currentUserPlaylistsRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        currentPlayListAdapter = AllPlaylistAdapter()

//        binding.otherUsersPlaylistsRecycler.layoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        othersPlayListAdapter = AllPlaylistAdapter()

        binding.currentUserPlaylistsRecycler.adapter = currentPlayListAdapter
        binding.otherUsersPlaylistsRecycler.adapter = othersPlayListAdapter

        val displayMetrics = Resources.getSystem().displayMetrics
        val columnWidth = resources.getDimension(R.dimen.column_width).toInt()
        val numberOfColumns = displayMetrics.widthPixels / columnWidth

        val gridLayoutManager = GridLayoutManager(this, numberOfColumns)
        binding.otherUsersPlaylistsRecycler.layoutManager = gridLayoutManager


        binding.selectsong.setOnClickListener {
            if (checkPermissions()) {
                openAudioSelection()
            } else {
                requestPermissions()
            }
        }
        retrieveCurrentUserPlaylists()
        retrieveOtherUsersPlaylists()
    }


    private fun checkPermissions(): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return readPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCodePermission
        )
    }


    private fun openAudioSelection() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "audio/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, requestCodeAudioSelection)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestCodeAudioSelection && resultCode == RESULT_OK) {
            data?.let { intentData ->
                val selectedUris = intentData.clipData
                val singleUri = intentData.data

                if (selectedUris != null) {
                    for (i in 0 until selectedUris.itemCount) {
                        val uri = selectedUris.getItemAt(i).uri
                        val audioFile = getAudioFileFromUri(uri)

                        // Check if the song already exists in the list
                        if (audioFile != null && !selectedAudioFiles.contains(audioFile)) {
                            selectedAudioFiles.add(audioFile)
                        } else {
                            // Display a message indicating that the song already exists
                            showToast("Song '${audioFile?.title}' already exists.")
                        }
                    }

                    showPlaylistNameDialog(selectedAudioFiles) // Show the dialog
                } else if (singleUri != null) {
                    val audioFile = getAudioFileFromUri(singleUri)

                    // Check if the song already exists in the list
                    if (audioFile != null && !selectedAudioFiles.contains(audioFile)) {
                        selectedAudioFiles.add(audioFile)
                    } else {
                        // Display a message indicating that the song already exists
                        showToast("Song already exists.")
                    }

                    showPlaylistNameDialog(selectedAudioFiles) // Show the dialog
                    //selectedAudioFiles.clear()
                }
            }
        }
    }

    private fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }


    private fun getAudioFileFromUri(uri: Uri): AudioFile? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val id = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DOCUMENT_ID)

                // Check if the "artist" column exists before retrieving its value
                val artistColumnIndex = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val artist = if (artistColumnIndex != -1) {
                    it.getString(artistColumnIndex)
                } else {
                    "Unknown"
                }

                val title = it.getString(titleColumn)
                val duration = getDurationFromMediaStore(uri)

                return AudioFile(
                    id.toString(), uri.toString(), title, artist, Util.formatDuration(duration)
                )
            }
        }
        return null
    }


    private fun getDurationFromMediaStore(uri: Uri): Long {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(applicationContext, uri)
        mediaPlayer.prepare()
        val duration = mediaPlayer.duration.toLong()
        mediaPlayer.release()
        return duration
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodePermission && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openAudioSelection()
        }
    }

    private fun showPlaylistNameDialog(selectedSongs: List<AudioFile>) {
        val dialogBinding = DialogAddPlaylistBinding.inflate(layoutInflater)
        val dialog =
            AlertDialog.Builder(this).setView(dialogBinding.root).setTitle("Enter Playlist Name")
                .setPositiveButton("Save") { _, _ ->
                    val playlistName = dialogBinding.editTextPlaylistName.text.toString()

                    if (playlistName.isNotBlank()) {
                        savePlaylistToDatabase(selectedSongs, playlistName)
                        selectedAudioFiles.clear()

                    } else {
                        showToast("Playlist name cannot be empty.")
                    }
                }.setNegativeButton("Cancel") { _, _ ->
                    // Dismiss the dialog
                }.create()

        dialog.show()
    }

    private fun savePlaylistToDatabase(selectedSongs: List<AudioFile>, playlistName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Create a reference to the Firebase Realtime Database node for the user's playlists
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
            .child("playlists")

        // Create a new playlist entry in the Firebase Realtime Database
        val playlistRef = databaseRef.push()
        playlistRef.child("playlistName").setValue(playlistName)

        // Clear the previous songs data for this playlist
        val songsRef = playlistRef.child("songs")
        songsRef.removeValue() // This removes all data under "songs" for the current playlist

        // Iterate through the selected songs and store them in the Firebase Realtime Database
        selectedSongs.forEach { audioFile ->
            val songDetails = mutableMapOf(
                "displayName" to audioFile.title, "duration" to audioFile.duration
            )

            // Create a unique ID for the song entry
            val songEntryRef = songsRef.push()

            // Generate a unique filename for the song in Firebase Storage
            val fileName = UUID.randomUUID().toString() + ".mp3"

            // Create a reference to the Firebase Storage location for the song file
            val storageRef = FirebaseStorage.getInstance().reference
            val songRef = storageRef.child("songs").child(audioFile.title!!)

            // Upload the song file to Firebase Storage
            val fileUri = Uri.parse(audioFile.downloadUrl)
            val uploadTask = songRef.putFile(fileUri)

            uploadTask.addOnSuccessListener { _ ->
                // Get the download URL of the uploaded song
                songRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Store the download URL and other song details in the Firebase Realtime Database
                    songDetails += ("downloadUrl" to downloadUrl.toString())
                    songEntryRef.setValue(songDetails)
                }.addOnFailureListener { _ ->
                    showToast("Failed to retrieve the download URL for the song.")
                }
            }.addOnFailureListener { _ ->
                showToast("Failed to upload the song to Firebase Storage.")
            }
        }

        showToast("Playlist '$playlistName' saved successfully.")
    }

//    private fun retrieveSongsForPlaylist() {
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//
//        // Create a reference to the Firebase Realtime Database node for the user's playlists
//        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
//            .child("playlists")
//
//        // Create a reference to the specific playlist by its ID
//        databaseRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (playListShot in snapshot.children) {
//                    val playlistId = playListShot.key!!
//
//                    val playlistRef = databaseRef.child(playlistId)
//
//                    // Create a reference to the songs within the playlist
//                    val songsRef = playlistRef.child("songs")
//
//                    // Listen for changes in the songs data
//                    songsRef.addValueEventListener(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            val songs = mutableListOf<AudioFile>()
//
//                            for (songSnapshot in snapshot.children) {
//                                val displayName = songSnapshot.child("displayName").value.toString()
//                                val duration = songSnapshot.child("duration").value.toString()
//                                val downloadUrl = songSnapshot.child("downloadUrl").value.toString()
//
//                                val song = AudioFile("", downloadUrl, displayName, duration)
//                                songs.add(song)
//                            }
//
//                            // Now, 'songs' contains the list of songs for the specified playlist
//                            // You can use this list to play the songs or display them in your app
//                            audioListAdapter.setData(songs)
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            // Handle any errors
//                        }
//                    })
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//
//
//    }

    private fun retrieveCurrentUserPlaylists() {
        // Retrieve playlists for the current user
        // ...
        val userId = auth.currentUser!!.uid
        database.child("users").child(userId).child("playlists")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val playlists = mutableListOf<Playlist>()
                    for (playlistShot in snapshot.children) {
                        val playlistId = playlistShot.key
                        val playlistname =
                            playlistShot.child("playlistName").getValue(String::class.java)
                        val playlist = Playlist(playlistId!!, playlistname!!, userId, null, "")
                        playlists.add(playlist)
                        currentPlayListAdapter.setPlaylists(playlists)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

    private fun retrieveOtherUsersPlaylists() {
        // Retrieve playlists for other users
        // ...
        database.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playlists = mutableListOf<Playlist>()
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key

                    // Make sure you skip the current user's data
                    if (userId != auth.uid) {
                        val playlistsSnapshot = userSnapshot.child("playlists")
                        for (playlistShot in playlistsSnapshot.children) {
                            val playlistId = playlistShot.key
                            val playListName = playlistShot.child("playlistName")
                                .getValue(String::class.java)
                            val playlist =
                                Playlist(playlistId!!, playListName!!, userId!!, null, "")
                            playlists.add(playlist)
                        }
                    }
                }
                othersPlayListAdapter.setPlaylists(playlists)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }


}

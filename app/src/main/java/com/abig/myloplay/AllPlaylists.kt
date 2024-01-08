package com.abig.myloplay

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

class AllPlaylists : AppCompatActivity() {

    private lateinit var othersPlayListAdapter: OthersAdapter
    private lateinit var recommendationsAdapter: RecommendationsAdapter
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
        othersPlayListAdapter = OthersAdapter()

        retrieveRecommendedSongs()

        recommendationsAdapter = RecommendationsAdapter {
            removeRecommendedSong(it)
        }

        binding.currentUserPlaylistsRecycler.adapter = currentPlayListAdapter
        binding.otherUsersPlaylistsRecycler.adapter = othersPlayListAdapter
        binding.recommendationsRecy.adapter = recommendationsAdapter

        binding.recommendationsRecy.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val displayMetrics = Resources.getSystem().displayMetrics
        val columnWidth = resources.getDimension(R.dimen.column_width).toInt()
        val numberOfColumns = displayMetrics.widthPixels / columnWidth

        val gridLayoutManager = GridLayoutManager(this, numberOfColumns)
        binding.otherUsersPlaylistsRecycler.layoutManager = gridLayoutManager
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        binding.otherUsersPlaylistsRecycler.addItemDecoration(
            GridSpacingItemDecoration(
                numberOfColumns, spacingInPixels, false
            )
        )


        binding.selectsong.setOnClickListener {
            openAudioSelection()
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

    @Deprecated("Deprecated in Java")
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
                val albumArt = getAlbumArtFromMediaStore(uri)

                return AudioFile(
                    id.toString(),
                    uri.toString(),
                    title,
                    artist,
                    Util.formatDuration(duration),
                    albumArt
                )
            }
        }
        return null
    }

    private fun getAlbumArtFromMediaStore(uri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(this, uri)
            val albumArtByteArray = retriever.embeddedPicture
            if (albumArtByteArray != null) {
                // Convert the byte array to a Bitmap
                return BitmapFactory.decodeByteArray(albumArtByteArray, 0, albumArtByteArray.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            retriever.release()
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
                "displayName" to audioFile.title,
                "artist" to audioFile.artist,
                "duration" to audioFile.duration
            )

            // Create a unique ID for the song entry
            val songEntryRef = songsRef.push()

            // Generate a unique filename for the song in Firebase Storage
            val fileName = UUID.randomUUID().toString() + ".mp3"

            // Upload the song file to Firebase Storage
            val fileUri = Uri.parse(audioFile.downloadUrl)
            val uploadTask = FirebaseStorage.getInstance().reference.child("songs").child(fileName)
                .putFile(fileUri)

            uploadTask.addOnSuccessListener { _ ->
                // Get the download URL of the uploaded song
                FirebaseStorage.getInstance().reference.child("songs")
                    .child(fileName).downloadUrl.addOnSuccessListener { songDownloadUrl ->

                        // Store the download URL in the Firebase Realtime Database
                        songDetails += ("downloadUrl" to songDownloadUrl.toString())

                        // Now, upload the album art to Firebase Storage
                        val albumArtBitmap = audioFile.albumArt
                        if (albumArtBitmap != null) {
                            val albumArtFileName = UUID.randomUUID().toString() + ".jpg"
                            val albumArtRef =
                                FirebaseStorage.getInstance().reference.child("album_arts")
                                    .child(albumArtFileName)

                            val albumArtStream = ByteArrayOutputStream()
                            albumArtBitmap.compress(Bitmap.CompressFormat.JPEG, 100, albumArtStream)
                            val albumArtData = albumArtStream.toByteArray()

                            val albumArtUploadTask = albumArtRef.putBytes(albumArtData)

                            albumArtUploadTask.addOnSuccessListener { _ ->
                                // Get the download URL of the uploaded album art
                                albumArtRef.downloadUrl.addOnSuccessListener { albumArtDownloadUrl ->
                                    // Add the album art URL to the song details
                                    songDetails += ("albumArt" to albumArtDownloadUrl.toString())

                                    // Save the song details to the Firebase Realtime Database
                                    songEntryRef.setValue(songDetails)
                                }.addOnFailureListener { _ ->
                                    showToast("Failed to retrieve the download URL for the album art.")
                                }
                            }.addOnFailureListener { _ ->
                                showToast("Failed to upload the album art to Firebase Storage.")
                            }
                        } else {
                            // No album art, save the song details without it
                            songEntryRef.setValue(songDetails)
                        }
                    }.addOnFailureListener { _ ->
                        showToast("Failed to retrieve the download URL for the song.")
                    }
            }.addOnFailureListener { _ ->
                showToast("Failed to upload the song to Firebase Storage.")
            }
        }

        showToast("Playlist '$playlistName' created successfully.")
    }


    private fun retrieveCurrentUserPlaylists() {
        // Retrieve playlists for the current user
        // ...
        val userId = auth.currentUser!!.uid
        database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (usersnap in snapshot.children) {

                    val playlists = mutableListOf<Playlist>()
                    val username = usersnap.child("username").getValue(String::class.java)
                    val plsnapshot = snapshot.child(userId).child("playlists")

                    for (playlistShot in plsnapshot.children) {
                        val playlistId = playlistShot.key
                        val playlistname =
                            playlistShot.child("playlistName").getValue(String::class.java)
                        val playlist =
                            Playlist(playlistId!!, playlistname!!, userId, null, username)
                        playlists.add(playlist)
                        currentPlayListAdapter.setPlaylists(playlists)

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //error
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
                    val username = userSnapshot.child("username").getValue(String::class.java)

                    // Make sure you skip the current user's data
                    if (userId != auth.uid) {
                        val playlistsSnapshot = userSnapshot.child("playlists")
                        for (playlistShot in playlistsSnapshot.children) {
                            val playlistId = playlistShot.key
                            val playListName =
                                playlistShot.child("playlistName").getValue(String::class.java)
                            val playlist =
                                Playlist(playlistId!!, playListName!!, userId!!, null, username)
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

    private fun retrieveRecommendedSongs() {
        // Retrieve playlists for other users
        // ...
        database.child("users").child(auth.currentUser!!.uid).child("recommendations")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.recommendationsLayout.visibility = View.VISIBLE
                        val playlists = mutableListOf<Recommendation>()
                        for (recSnapshot in snapshot.children) {
                            //val userId = recSnapshot.key
                            val songId = recSnapshot.child("songId").getValue(String::class.java)
                            val songTitle =
                                recSnapshot.child("songTitle").getValue(String::class.java)
                            val artist = recSnapshot.child("artist").getValue(String::class.java)
                            val songUrl = recSnapshot.child("songUrl").getValue(String::class.java)
                            val duration =
                                recSnapshot.child("duration").getValue(String::class.java)
                            val recommendedBy =
                                recSnapshot.child("recommendedBy").getValue(String::class.java)
                            val recommendation =
                                Recommendation(
                                    songId,
                                    songUrl!!,
                                    songTitle!!,
                                    artist!!,
                                    duration!!,
                                    recommendedBy!!
                                )
                            playlists.add(recommendation)


                        }
                        recommendationsAdapter.setRecommendedSong(playlists)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
    }

    private fun removeRecommendedSong(selectedSong: Recommendation) {
        // Remove the song from the list of songIds in the Firebase Realtime Database
        val playlistRef = FirebaseDatabase.getInstance().reference.child("users")
            .child(auth.currentUser!!.uid).child("recommendations").child(selectedSong.songId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val songIds =
                        snapshot.getValue<List<String>>()?.toMutableList() ?: mutableListOf()
                    // Remove the selected songId
                    songIds.remove(selectedSong.songId)

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}

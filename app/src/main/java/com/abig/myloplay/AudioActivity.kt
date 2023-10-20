package com.abig.myloplay

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.ActivityAudioBinding
import com.abig.myloplay.databinding.DialogAddPlaylistBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.UUID
import kotlin.properties.Delegates

class AudioActivity : AppCompatActivity() {

    private lateinit var audioListAdapter: AudioListAdapter
    private val requestCodePermission = 123
    private val requestCodeAudioSelection = 456
    private lateinit var binding: ActivityAudioBinding
    private lateinit var playlistId: String
    private lateinit var userId: String
    private val selectedAudioFiles = mutableListOf<AudioFile>()
    private lateinit var preferences: SharedPreferences
    private var maxFileSizeBytes by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize SharedPreferences

        preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

// Retrieve the maxFileSizeBytes or use a default value
         maxFileSizeBytes = preferences.getInt("maxFileSizeBytes", DEFAULT_MAX_FILE_SIZE).toLong()

        binding.audioRecycler.layoutManager = LinearLayoutManager(this)
        audioListAdapter = AudioListAdapter()
        binding.audioRecycler.adapter = audioListAdapter

        playlistId = intent.getStringExtra(EXTRA_PLAYLIST_ID)!!
        userId = intent.getStringExtra(EXTRA_USER_ID)!!

//        if (checkPermissions()) {
//            openAudioSelection()
//        } else {
//            requestPermissions()
//        }
//
//        binding.selectsong.setOnClickListener {
//            openAudioSelection()
//        }
        retrieveSongsForPlaylist(playlistId, userId)
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
                val artistColumnIndex = it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ARTIST)
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

    private fun retrieveSongsForPlaylist(playlistId: String?, userId: String?) {
        //val userId = FirebaseAuth.getInstance().uid

        // Create a reference to the Firebase Realtime Database node for the user's playlists
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")

        // Create a reference to the specific playlist by its ID
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (playListShot in snapshot.children) {
                    val uID = playListShot.key!!
                    showToast(playlistId.toString())
                    val playlistRef = databaseRef.child(userId!!)

                    // Create a reference to the songs within the playlist
                    val songsRef = playlistRef.child("playlists").child(playlistId!!).child("songs")

                    // Listen for changes in the songs data
                    songsRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val songs = mutableListOf<AudioFile>()

                            for (songSnapshot in snapshot.children) {
                                val displayName = songSnapshot.child("displayName").value.toString()
                                val artist = songSnapshot.child("artist").value.toString()
                                val duration = songSnapshot.child("duration").value.toString()
                                val downloadUrl = songSnapshot.child("downloadUrl").value.toString()

                                val song = AudioFile("", downloadUrl, displayName, artist, duration)
                                songs.add(song)
                            }

                            // Now, 'songs' contains the list of songs for the specified playlist
                            // You can use this list to play the songs or display them in your app
                            audioListAdapter.setData(songs)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle any errors
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }

    private fun retrieveSongsForDownload(playlistId: String?, userId: String?) {
        //val userId = FirebaseAuth.getInstance().uid

        // Create a reference to the Firebase Realtime Database node for the user's playlists
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")

        // Create a reference to the specific playlist by its ID
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (playListShot in snapshot.children) {
                    val uID = playListShot.key!!
                    showToast(playlistId.toString())
                    val playlistRef = databaseRef.child(userId!!)

                    // Create a reference to the songs within the playlist
                    val songsRef = playlistRef.child("playlists").child(playlistId!!).child("songs")

                    // Listen for changes in the songs data
                    songsRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val songs = mutableListOf<AudioFile>()

                            for (songSnapshot in snapshot.children) {
                                val displayName = songSnapshot.child("displayName").value.toString()
                                val artist = songSnapshot.child("artist").value.toString()
                                val duration = songSnapshot.child("duration").value.toString()
                                val downloadUrl = songSnapshot.child("downloadUrl").value.toString()

                                val song = AudioFile("", downloadUrl, displayName, artist, duration)
                                songs.add(song)
                            }

                            // Now, 'songs' contains the list of songs for the specified playlist
                            // You can use this list to play the songs or display them in your app
                            audioListAdapter.setData(songs)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle any errors
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }

    fun downloadSong(downloadUrl: String, songName: String?) {
        // Retrieve the max file size preference
        //val maxFileSizeBytes = preferences.getLong("maxFileSizeBytes", DEFAULT_MAX_FILE_SIZE.toLong())

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Downloading...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.isIndeterminate = false
        progressDialog.max = 100
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Define the local directory where the songs will be saved
        val downloadDirectory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "myloplay")

        if (!downloadDirectory.exists()) {
            // If the directory doesn't exist, create it
            downloadDirectory.mkdirs()
        }

        // Define the local file where the song will be saved
        val localFile = File(downloadDirectory, "$songName")

        try {
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl)

            storageReference.metadata.addOnSuccessListener { metadata ->
                if (metadata.sizeBytes > maxFileSizeBytes) {
                    val snackbar = Snackbar.make(
                        binding.root,
                        "File size exceeds the limit (500 MB)",
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.setAction("Adjust Max File Size") {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    }
                    snackbar.show()
                    progressDialog.dismiss()
                } else {
                    val downloadTask = storageReference.getFile(localFile)

                    downloadTask.addOnSuccessListener {
                        Toast.makeText(this, "Download completed", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Download failed: $it", Toast.LENGTH_SHORT).show()
                    }.addOnProgressListener { taskSnapshot ->
                        val progress =
                            (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                        val limitedProgress = progress.coerceAtMost(100)
                        progressDialog.progress = limitedProgress
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error getting file metadata", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    companion object {
        const val EXTRA_PLAYLIST_ID = "playlistId"
        const val EXTRA_USER_ID = "userID"
        const val PERMISSION_REQUEST_CODE = 0
        const val DEFAULT_MAX_FILE_SIZE =1 * 1024 * 1024
    }

}

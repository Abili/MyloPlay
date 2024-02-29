package com.abig.myloplay

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.ActivityAudioBinding
import com.abig.myloplay.databinding.DialogAddPlaylistBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.UUID
import kotlin.math.min
import kotlin.properties.Delegates

class AudioActivity : AppCompatActivity() {

    private lateinit var audioListAdapter: AudioListAdapter
    private val requestCodePermission = 123
    private val requestCodeAudioSelection = 456
    private lateinit var binding: ActivityAudioBinding
    private lateinit var playlistName: String
    private lateinit var username: String
    private lateinit var playlistId: String
    private lateinit var playlistType: String
    private lateinit var seenOrNot: String
    private lateinit var userId: String
    private val selectedAudioFiles = mutableListOf<AudioFile>()
    private lateinit var preferences: SharedPreferences
    //private var maxFileSizeBytes by Delegates.notNull<Long>()
    private lateinit var playlistsRef: DatabaseReference
    lateinit var userPl: DatabaseReference
    lateinit var myloPlayer: MyloPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize SharedPreferences
        myloPlayer = MyloPlayer()

        preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

// Retrieve the maxFileSizeBytes or use a default value
        //maxFileSizeBytes = preferences.getLong("maxFileSizeBytes", DEFAULT_MAX_FILE_SIZE.toLong())
        playlistsRef = FirebaseDatabase.getInstance().getReference("playlists")
        userPl = FirebaseDatabase.getInstance().reference.child("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("playlists")


        binding.audioRecycler.layoutManager = LinearLayoutManager(this)
        audioListAdapter = AudioListAdapter(onRemoveClickListener = { selectedSong ->
            showRemoveConfirmationDialog(selectedSong)
        }, onRecommendClickListener = { selectedSong ->
            showRecommendationDialog(selectedSong)
        })
        binding.audioRecycler.adapter = audioListAdapter

        playlistId = intent.getStringExtra(EXTRA_PLAYLIST_ID)!!
        userId = intent.getStringExtra(EXTRA_USER_ID)!!
        playlistType = intent.getStringExtra(EXTRA_PLAYLIST_TYPE)!!
        //username = intent.getStringExtra(EXTRA_USER_NAME)!!
        playlistName = intent.getStringExtra(EXTRA_PLAYLIST_NAME)!!
        binding.playlistnames.text = playlistName

        if (playlistType == "Single") {
            retrieveCurrentUserPlaylists(playlistId, userId)
        } else {
            retrieveGroupPlaylists(playlistId, userId)
        }


//        if (checkPermissions()) {
//            openAudioSelection()
//        } else {
//            requestPermissions()
//        }
//
        binding.addSong.setOnClickListener {
            openAudioSelection()
        }
        if (playlistType == "group") {
            retrieveSongsForGroupPlaylist(playlistId, userId)
        } else {
            retrieveSongsForPlaylist(playlistId, userId)
            binding.friendsnames.visibility = View.GONE
        }

        if (userId != FirebaseAuth.getInstance().currentUser!!.uid) {
            binding.editPl.visibility = View.GONE
            binding.addSong.visibility = View.GONE
        }

        binding.editPl.setOnClickListener {
            val editLayout = layoutInflater.inflate(R.layout.activity_edit_playlist, null, false)
            val editTextPlaylistName = editLayout.findViewById<EditText>(R.id.editTextPlaylistName)

            // Set the initial text in the EditText
            editTextPlaylistName.setText(binding.playlistnames.text)

            AlertDialog.Builder(this).setView(editLayout).setPositiveButton("save") { dialog, id ->
                // Get the edited playlist name as a String
                val playlistName = editTextPlaylistName.text.toString()

                // Update the TextView
                binding.playlistnames.text = playlistName

                // Update the corresponding entry in Firebase Realtime Database
                val playName = userPl.child(playlistId)
                val playlistEdit = mapOf(
                    "playlistName" to playlistName
                )
                playName.updateChildren(playlistEdit)

                dialog.dismiss()
            }.setNegativeButton("cancel") { dialog, id ->
                dialog.dismiss()
            }.show()
        }

        binding.shuffleSong.setOnClickListener {
            // Shuffle the playlist when the shuffle button is clicked
            //startActivity(Intent(this, MyloPlayer::class.java))
            // myloPlayer.playShuffledSongs(PL)
            audioListAdapter.getShuffledSongs()
        }
        if (userId == FirebaseAuth.getInstance().currentUser!!.uid) {
            binding.commentLayout.visibility = View.GONE
        }

        binding.commentTv.setOnClickListener {
            binding.apply {
                commenEdt.visibility = View.VISIBLE
                binding.addSong.visibility = View.GONE
                binding.commentEdtlayout.visibility = View.VISIBLE
            }
        }

        binding.sendBtn.setOnClickListener {
            val comment = binding.commenEdt.text.toString().trim()
            commentOnPlaylist(comment, userId, playlistId, playlistType)
        }

        binding.likes.setOnClickListener {
            increasePlaylistLikes(playlistId, userId, playlistType)
        }
        retrieveLikes(playlistId, playlistType, userId)
        retrieveViews(playlistId, playlistType, userId)
        increasePlaylistViews(playlistId, userId, playlistType)


    }

    private fun hasUserLikedPlaylist(
        playlistId: String, userId: String, callback: (Boolean) -> Unit
    ) {
        if (playlistType == "group") {
            val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            val playlistRef =
                databaseRef.child("playlists").child("group").child("playlists").child(playlistId)
            val viewedPlaylistsRef = playlistRef.child("likedPlaylists")

            viewedPlaylistsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val hasLiked = snapshot.hasChild(playlistId)
                    callback(hasLiked)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any errors
                    callback(false) // Assume not viewed in case of error
                }
            })
        } else {
            val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            val playlistRef =
                databaseRef.child("playlists").child("single").child("playlists").child(playlistId)
            val viewedPlaylistsRef = playlistRef.child("likedPlaylists")

            viewedPlaylistsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val hasLiked = snapshot.hasChild(playlistId)
                    callback(hasLiked)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any errors
                    callback(false) // Assume not viewed in case of error
                }
            })
        }
    }


    private fun increasePlaylistLikes(playlistId: String, userId: String, playlistType: String) {
        if (playlistType == "single") {
            hasUserLikedPlaylist(playlistId, userId) { hasLiked ->
                if (!hasLiked) {
                    val databaseRef =
                        FirebaseDatabase.getInstance().reference.child("users").child(userId)
                    val playlistRef =
                        databaseRef.child("playlists").child("single").child("playlists")
                            .child(playlistId)

                    playlistRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        @RequiresApi(Build.VERSION_CODES.M)
                        override fun onDataChange(snapshot: DataSnapshot) {
                            // Retrieve the current view count
                            var likes = snapshot.child("likes").getValue(Int::class.java) ?: 0

                            // Increment the view count
                            while (likes < 1) {
                                likes++
                            }

                            // Update the view count in the database
                            playlistRef.child("likes").setValue(likes)

                            // Mark the playlist as viewed by the user
                            markPlaylistAsLiked(playlistId, userId)
                            binding.likedUsers.text = likes.toString()
                            binding.likes.setBackgroundColor(getColor(R.color.myloblue))
                            binding.likedUsers.setBackgroundColor(getColor(R.color.myloblue))

                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle any errors
                        }
                    })
                }
            }
        } else {
            hasUserLikedPlaylist(playlistId, userId) { hasLiked ->
                if (!hasLiked) {
                    val databaseRef =
                        FirebaseDatabase.getInstance().reference.child("users").child(userId)
                    val playlistRef =
                        databaseRef.child("playlists").child("group").child("playlists")
                            .child(playlistId)

                    playlistRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        @RequiresApi(Build.VERSION_CODES.M)
                        override fun onDataChange(snapshot: DataSnapshot) {
                            // Retrieve the current view count
                            var likes = snapshot.child("likes").getValue(Int::class.java) ?: 0

                            // Increment the view count

                            while (likes < 1) {
                                likes++
                            }

                            // Update the view count in the database
                            playlistRef.child("likes").setValue(likes)

                            // Mark the playlist as viewed by the user
                            markPlaylistAsLiked(playlistId, userId)
                            binding.likedUsers.text = likes.toString()
                            binding.likes.setBackgroundColor(getColor(R.color.myloblue))
                            binding.likedUsers.setBackgroundColor(getColor(R.color.myloblue))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle any errors
                        }
                    })
                }
            }
        }
    }

    private fun markPlaylistAsLiked(playlistId: String, userId: String) {
        if (playlistType == "group") {
            val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            val playlistRef =
                databaseRef.child("playlists").child("group").child("playlists").child(playlistId)
            val viewedPlaylistsRef = playlistRef.child("likedPlaylists")

            // Set the playlist ID as true to mark it as viewed
            viewedPlaylistsRef.child(playlistId).setValue(true)
        } else {
            val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            val playlistRef =
                databaseRef.child("playlists").child("single").child("playlists").child(playlistId)
            val viewedPlaylistsRef = playlistRef.child("likedPlaylists")

            // Set the playlist ID as true to mark it as viewed
            viewedPlaylistsRef.child(playlistId).setValue(true)
        }
    }

    private fun retrieveLikes(playlistId: String, playlistType: String, userId: String) {
        if (playlistType == "single") {

            val databaseRef =
                FirebaseDatabase.getInstance().reference.child("users").child(userId)
            val playlistRef =
                databaseRef.child("playlists").child("single").child("playlists")
                    .child(playlistId)

            playlistRef.addListenerForSingleValueEvent(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Retrieve the current view count
                    val likes = snapshot.child("likes").getValue(Int::class.java) ?: 0
                    binding.likedUsers.text = likes.toString()
                    //showToast(likes.toString())

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any errors
                }
            })

        } else {
            val databaseRef =
                FirebaseDatabase.getInstance().reference.child("users").child(userId)
            val playlistRef =
                databaseRef.child("playlists").child("group").child("playlists")
                    .child(playlistId)

            playlistRef.addListenerForSingleValueEvent(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Retrieve the current view count
                    val likes = snapshot.child("likes").getValue(Int::class.java)?:0
                    binding.likedUsers.text = likes.toString()
                    //showToast(likes.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any errors
                }
            })

        }

    }


    private fun retrieveViews(playlistId: String, playlistType: String, userId: String) {
        if (playlistType == "single") {

            val databaseRef =
                FirebaseDatabase.getInstance().reference.child("users").child(userId)
            val playlistRef =
                databaseRef.child("playlists").child("single").child("playlists")
                    .child(playlistId)

            playlistRef.addListenerForSingleValueEvent(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Retrieve the current view count
                    val views = snapshot.child("views").getValue(Int::class.java)
                    binding.seenUsers.text = views.toString()
                    //showToast(views.toString())

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any errors
                }
            })

        } else {
            val databaseRef =
                FirebaseDatabase.getInstance().reference.child("users").child(userId)
            val playlistRef =
                databaseRef.child("playlists").child("group").child("playlists")
                    .child(playlistId)

            playlistRef.addListenerForSingleValueEvent(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Retrieve the current view count
                    val views = snapshot.child("views").getValue(Int::class.java) ?: 0
                    binding.seenUsers.text = views.toString()
                    //showToast(views.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any errors
                }
            })

        }

    }

    private fun hasUserViewedPlaylist(
        playlistId: String, userId: String, callback: (Boolean) -> Unit
    ) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        val playlistRef =
            databaseRef.child("playlists").child("group").child("playlists").child(playlistId)
        val viewedPlaylistsRef = playlistRef.child("viewedPlaylists")

        viewedPlaylistsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hasViewed = snapshot.hasChild(playlistId)
                callback(hasViewed)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
                callback(false) // Assume not viewed in case of error
            }
        })
    }


    private fun increasePlaylistViews(playlistId: String, userId: String, playlistType: String) {
        if (playlistType == "single") {
            hasUserViewedPlaylist(playlistId, userId) { hasViewed ->
                if (!hasViewed) {
                    val databaseRef =
                        FirebaseDatabase.getInstance().reference.child("users").child(userId)
                    val playlistRef =
                        databaseRef.child("playlists").child("single").child("playlists")
                            .child(playlistId)

                    playlistRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        @RequiresApi(Build.VERSION_CODES.M)
                        override fun onDataChange(snapshot: DataSnapshot) {
                            // Retrieve the current view count
                            var views = snapshot.child("views").getValue(Int::class.java) ?: 0

                            // Increment the view count
                            views++

                            // Update the view count in the database
                            playlistRef.child("views").setValue(views)

                            // Mark the playlist as viewed by the user
                            markPlaylistAsViewed(playlistId, userId)
                            binding.seenUsers.text = views.toString()
                            binding.views.setBackgroundColor(getColor(R.color.myloblue))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle any errors
                        }
                    })
                }
            }
        } else {
            hasUserViewedPlaylist(playlistId, userId) { hasViewed ->
                if (!hasViewed) {
                    val databaseRef =
                        FirebaseDatabase.getInstance().reference.child("users").child(userId)
                    val playlistRef =
                        databaseRef.child("playlists").child("group").child("playlists")
                            .child(playlistId)

                    playlistRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        @RequiresApi(Build.VERSION_CODES.M)
                        override fun onDataChange(snapshot: DataSnapshot) {
                            // Retrieve the current view count
                            var views = snapshot.child("views").getValue(Int::class.java) ?: 0

                            // Increment the view count
                            views++

                            // Update the view count in the database
                            playlistRef.child("views").setValue(views)

                            // Mark the playlist as viewed by the user
                            markPlaylistAsViewed(playlistId, userId)
                            binding.seenUsers.text = views.toString()
                            binding.views.setBackgroundColor(getColor(R.color.myloblue))
                            binding.seenUsers.setBackgroundColor(getColor(R.color.myloblue))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle any errors
                        }
                    })
                }
            }
        }
    }

    private fun markPlaylistAsViewed(playlistId: String, userId: String) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        val playlistRef =
            databaseRef.child("playlists").child("group").child("playlists").child(playlistId)
        val viewedPlaylistsRef = playlistRef.child("viewedPlaylists")

        // Set the playlist ID as true to mark it as viewed
        viewedPlaylistsRef.child(playlistId).setValue(true)
    }


    private fun commentOnPlaylist(
        comment: String, userId: String, playlistId: String, playlistType: String
    ) {
        val commentRef =
            FirebaseDatabase.getInstance().reference.child("users").child(userId).child("playlists")

        if (playlistType == "single") {
            // Generate a unique key for the new comment
            val commentId = commentRef.child("single").child("playlists").push().key

            // Create a map representing the comment data
            val commentData = mapOf(
                "commentId" to commentId,
                "comment" to comment,
                "userId" to userId,
                "playlistId" to playlistId
            )

            // Set the comment data under the generated key
            commentRef.child("single").child("playlists").child(playlistId).child("comments")
                .child(commentId!!).setValue(commentData).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Comment sent")
                        binding.commenEdt.setText("")
                        binding.commentEdtlayout.visibility = View.GONE
                    } else {
                        // Handle the case where setting the value fails
                        showToast("Failed to send comment")
                    }
                }
        }
    }


    private fun showRecommendationDialog(song: AudioFile) {
        val intent = Intent(this, UserContacts::class.java)
        intent.putExtra("songUrl", song.downloadUrl)
        intent.putExtra("songTitle", song.title)
        intent.putExtra("artist", song.artist)
        intent.putExtra("duration", song.duration)
        intent.putExtra("userId", song.duration)
        startActivity(intent)
    }

    private fun showRemoveConfirmationDialog(selectedSong: AudioFile) {
        // User confirmed, remove the song from the playlist
        removeSongFromPlaylist(selectedSong)

    }


    private fun retrieveGroupPlaylists(playlistId: String, userId: String) {

        val database =
            FirebaseDatabase.getInstance().reference.child("users").child(userId).child("playlists")
                .child("group").child("playlists").child(playlistId)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playlistName = snapshot.child("playlistName").getValue(String::class.java)
                val songsSnapshot = snapshot.child("songs")
                val lastSongSnapshot = songsSnapshot.children.lastOrNull()
                val songsCount = songsSnapshot.childrenCount
                if (songsCount.toInt() == 1) {
                    binding.songCount.text = buildString {
                        append(songsCount.toString())
                        append(" Song")
                    }
                } else {
                    binding.songCount.text = buildString {
                        append(songsCount.toString())
                        append(" Songs")
                    }
                }

                //binding.playlistnames.text = playlistName
                val lastSongAlbumArtUrl =
                    lastSongSnapshot?.child("albumArt")?.getValue(String::class.java)
                if (lastSongAlbumArtUrl != null) {
                    // Load the album art into the ImageView using Glide
                    Glide.with(binding.root).load(lastSongAlbumArtUrl).centerCrop()
                        .into(binding.playlistImageView)
                } else {
                    Glide.with(binding.root).load(R.drawable.mylo_bg_logo).centerCrop()
                        .into(binding.playlistImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }

    private fun retrieveCurrentUserPlaylists(playlistId: String, userId: String) {
        val database =
            FirebaseDatabase.getInstance().reference.child("users").child(userId).child("playlists")
                .child("single").child("playlists").child(playlistId)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playlistName = snapshot.child("playlistName").getValue(String::class.java)
                val songsSnapshot = snapshot.child("songs")
                val lastSongSnapshot = songsSnapshot.children.lastOrNull()
                val songsCount = songsSnapshot.childrenCount
                if (songsCount.toInt() == 1) {
                    binding.songCount.text = buildString {
                        append(songsCount.toString())
                        append(" Song")
                    }
                } else {
                    binding.songCount.text = buildString {
                        append(songsCount.toString())
                        append(" Songs")
                    }
                }

                //binding.playlistnames.text = playlistName


                val lastSongAlbumArtUrl =
                    lastSongSnapshot?.child("albumArt")?.getValue(String::class.java)
                if (lastSongAlbumArtUrl != null) {
                    // Load the album art into the ImageView using Glide
                    Glide.with(binding.root).load(lastSongAlbumArtUrl).centerCrop()
                        .into(binding.playlistImageView)
                } else {
                    Glide.with(binding.root).load(R.drawable.mylo_bg_logo).centerCrop()
                        .into(binding.playlistImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }

    private fun removeSongFromPlaylist(selectedSong: AudioFile) {
        // Remove the song from the list of songIds in the Firebase Realtime Database
        val playlistRef = playlistsRef.child(playlistId)
        userPl.child(playlistId).child("songs")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val songIds =
                        snapshot.getValue<List<String>>()?.toMutableList() ?: mutableListOf()

                    // Remove the selected songId
                    songIds.remove(selectedSong.id)

                    // Update the songIds in the Firebase Realtime Database for the current playlist
//                playlistRef.child("songIds").setValue(songIds).addOnSuccessListener {
//                    showToast("Song removed from the playlist.")
//                }.addOnFailureListener {
//                    showToast("Failed to remove the song from the playlist.")
//                }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
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

    private fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun getAudioFileFromUri(uri: Uri): AudioFile? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val id = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DOCUMENT_ID)
                val title = it.getString(titleColumn)
                val duration = getDurationFromMediaStore(uri)
                val artist = getArtistFromUri(uri)

                return AudioFile(
                    id.toString(), uri.toString(), title, artist, Util.formatDuration(duration)
                )
            }
        }
        return null
    }

    private fun getArtistFromUri(uri: Uri): String {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(this, uri)
        val artist =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        mediaMetadataRetriever.release()
        return artist ?: "Unknown"
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
        val plalistType = intent.getStringExtra(EXTRA_PLAYLIST_TYPE)!!
        val dialog = AlertDialog.Builder(this).setTitle("Are you sure?")
            .setPositiveButton("confirm") { _, _ ->
                if (plalistType == "group") {
                    saveGroupPlaylistToDatabase(selectedSongs)
                    selectedAudioFiles.clear()
                } else {
                    savePlaylistToDatabase(selectedSongs)
                }


            }.setNegativeButton("Cancel") { _, _ ->
                // Dismiss the dialog
            }.create()

        dialog.show()
    }

    private fun saveGroupPlaylistToDatabase(selectedSongs: List<AudioFile>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Create a reference to the Firebase Realtime Database node for the user's playlists
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
            .child("playlists").child("group").child("playlists")


        // Create a new playlist entry in the Firebase Realtime Database
        val playlistRef = databaseRef.push()
        val key = playlistRef.key
        //databaseRef.child(playlistId).child("playlistName").setValue(playlistName)

        // Clear the previous songs data for this playlist
        val songsRef = databaseRef.child(playlistId).child("songs").child(key!!)
        songsRef.removeValue() // This removes all data under "songs" for the current playlist

        // Iterate through the selected songs and store them in the Firebase Realtime Database
        selectedSongs.forEach { audioFile ->
            val songDetails = mutableMapOf(
                "displayName" to audioFile.title, "duration" to audioFile.duration
            )

            // Create a unique ID for the song entry
            //val songEntryRef = songsRef.push()

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
                    songsRef.setValue(songDetails)
                }.addOnFailureListener { _ ->
                    showToast("Failed to retrieve the download URL for the song.")
                }
            }.addOnFailureListener { _ ->
                showToast("Failed to upload the song to Firebase Storage.")
            }
        }

        showToast("Song added successfully.")
    }


    private fun savePlaylistToDatabase(selectedSongs: List<AudioFile>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Create a reference to the Firebase Realtime Database node for the user's playlists
        val plypeRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
            .child("playlists")

        val databaseRef = plypeRef.child("single").child("playlists")


        // Create a new playlist entry in the Firebase Realtime Database
        val playlistRef = databaseRef.push()
        val key = playlistRef.key
        //databaseRef.child(playlistId).child("playlistName").setValue(playlistName)

        // Clear the previous songs data for this playlist
        val songsRef = databaseRef.child(playlistId).child("songs").child(key!!)
        songsRef.removeValue() // This removes all data under "songs" for the current playlist

        // Iterate through the selected songs and store them in the Firebase Realtime Database
        selectedSongs.forEach { audioFile ->
            val songDetails = mutableMapOf(
                "displayName" to audioFile.title, "duration" to audioFile.duration
            )

            // Create a unique ID for the song entry
            //val songEntryRef = songsRef.push()

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
                    songsRef.setValue(songDetails)
                }.addOnFailureListener { _ ->
                    showToast("Failed to retrieve the download URL for the song.")
                }
            }.addOnFailureListener { _ ->
                showToast("Failed to upload the song to Firebase Storage.")
            }
        }

        showToast("Song added successfully.")
    }

    private fun retrieveSongsForPlaylist(playlistId: String?, userId: String?) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
        val songsRef =
            databaseRef.child("playlists").child("single").child("playlists").child(playlistId!!)
                .child("songs")

        songsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val songs = mutableListOf<AudioFile>()

                for (songSnapshot in snapshot.children) {
                    val displayName = songSnapshot.child("displayName").value.toString()
                    val artist = songSnapshot.child("artist").value.toString()
                    val artistName = extractArtistName(displayName)
                    val duration = songSnapshot.child("duration").value.toString()
                    val downloadUrl = songSnapshot.child("downloadUrl").value.toString()

                    val song = AudioFile("", downloadUrl, displayName, artistName, duration)
                    songs.add(song)
                }

                audioListAdapter.setData(songs)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
            }
        })
    }

    private fun retrieveSongsForGroupPlaylist(playlistId: String?, userId: String?) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")
        val groupPlaylistRef =
            databaseRef.child(userId!!).child("playlists").child("group").child("playlists")
                .child(playlistId!!)
        val songsRef = groupPlaylistRef.child("songs")
        val userIdsRef = groupPlaylistRef.child("userIds")

        songsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val songs = mutableListOf<AudioFile>()

                for (songSnapshot in snapshot.children) {
                    val displayName = songSnapshot.child("displayName").value.toString()
                    val artist = songSnapshot.child("artist").value.toString()
                    val artistName = extractArtistName(displayName)
                    val duration = songSnapshot.child("duration").value.toString()
                    val downloadUrl = songSnapshot.child("downloadUrl").value.toString()

                    val song = AudioFile("", downloadUrl, displayName, artistName, duration)
                    songs.add(song)
                }

                audioListAdapter.setData(songs)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
            }
        })

        // Retrieve user names for the group playlist
        userIdsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userIds = snapshot.children.map { it.value.toString() }

                // Create a StringBuilder to concatenate usernames
                val usernamesBuilder = StringBuilder()

                // Counter to keep track of completed retrievals
                var retrievalCount = 0

                // Loop through userIds and retrieve user names
                for ((index, uid) in userIds.withIndex()) {
                    retrieveUserName(uid) { username ->
                        // Check if the UID is equal to the current user's UID
                        if (uid == FirebaseAuth.getInstance().currentUser?.uid) {
                            usernamesBuilder.append("You")
                        } else {
                            usernamesBuilder.append(username)
                        }

                        // Add comma if not the last username
                        if (index < userIds.size - 1) {
                            usernamesBuilder.append(", ")
                        }

                        // Update the UI with concatenated usernames
                        binding.friendsnames.text = "Listeners:\n" + usernamesBuilder.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
            }
        })
    }

    private fun retrieveUserName(uid: String, callback: (String) -> Unit) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")

        databaseRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersnap: DataSnapshot) {
                // Check if the "username" key exists directly under the user's node
                val username = usersnap.child("username").getValue(String::class.java)

                // Use the callback to pass the username
                callback(username ?: "N/A")
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
            }
        })
    }

    fun extractArtistName(displayName: String): String {
        val hyphenIndex = displayName.indexOf("-")
        val underscoreIndex = displayName.indexOf("_")

        val separatorIndex = when {
            hyphenIndex != -1 && underscoreIndex != -1 -> min(hyphenIndex, underscoreIndex)
            hyphenIndex != -1 -> hyphenIndex
            underscoreIndex != -1 -> underscoreIndex
            else -> -1
        }

        return if (separatorIndex != -1) {
            displayName.substring(0, separatorIndex).trim()
        } else {
            // If no separator found, return the whole display name or handle it as you prefer
            displayName.trim()
        }
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
        // val maxFileSizeBytes = preferences.getLong("maxFileSizeBytes", DEFAULT_MAX_FILE_SIZE.toLong())

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Downloading...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.isIndeterminate = false
        progressDialog.max = 100
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Define the local directory where the songs will be saved
        val downloadDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "myloplay"
        )

        if (!downloadDirectory.exists()) {
            // If the directory doesn't exist, create it
            downloadDirectory.mkdirs()
        }

        // Define the local file where the song will be saved
        val defaultName = "UnknownSong.mp3" // Default name if title metadata is not available
        val localFile = File(downloadDirectory, songName ?: defaultName)

        try {
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl)

            storageReference.metadata.addOnSuccessListener {
//                if (metadata.sizeBytes > maxFileSizeBytes) {
//                    val snackbar = Snackbar.make(
//                        binding.root, "File size exceeds the limit (500 MB)", Snackbar.LENGTH_LONG
//                    )
//                    snackbar.setAction("Adjust Max File Size") {
//                        showMaxFileSizeDialog()
//                    }
//                    snackbar.show()
//                    progressDialog.dismiss()
//                } else {
                    // Download the audio file to the specified location
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
            }.addOnFailureListener {
                Toast.makeText(this, "Error getting file metadata", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun showMaxFileSizeDialog() {
//        val dialogBinding = DialogAddPlaylistBinding.inflate(layoutInflater)
//        val dialog =
//            AlertDialog.Builder(this).setView(dialogBinding.root).setTitle("Adjust Max File Size")
//                .setPositiveButton("OK") { _, _ ->
//                    val maxSizeText = dialogBinding.editTextPlaylistName.text.toString()
//                    dialogBinding.editTextPlaylistName.hint = "Adjust file size..eg.. 500"
//                    if (maxSizeText.isNotBlank()) {
//                        val maxSizeBytes = maxSizeText.toLong() * 1024 * 1024
//                        updateMaxFileSize(maxSizeBytes)
//                    } else {
//                        showToast("Please enter a valid size.")
//                    }
//                }.setNegativeButton("Cancel") { _, _ -> }.create()
//
//        dialog.show()
//    }

//    private fun updateMaxFileSize(maxFileSizeBytes: Long) {
//        preferences.edit().putLong("maxFileSizeBytes", maxFileSizeBytes).apply()
//        this.maxFileSizeBytes =
//            preferences.getLong("maxFileSizeBytes", DEFAULT_MAX_FILE_SIZE.toLong())
//    }

    companion object {
        const val EXTRA_PLAYLIST_SEEN = "seen"
        const val EXTRA_USER_NAME = "userName"
        const val EXTRA_PLAYLIST_NAME = "playlistName"
        const val EXTRA_ALBUMART = "allbumArt"
        const val EXTRA_PLAYLIST_ID = "playlistId"
        const val EXTRA_PLAYLIST_TYPE = "group"
        const val EXTRA_USER_ID = "userID"
        const val PERMISSION_REQUEST_CODE = 0
        const val DEFAULT_MAX_FILE_SIZE = 1 * 1024 * 1024
    }

}

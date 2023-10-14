package com.abig.myloplay

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.DialogFragment
import com.abig.myloplay.databinding.DialogAddPlaylistBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class AddPlaylistDialogFragment(private var songs: List<Song>) : DialogFragment() {

    interface AddPlaylistDialogListener {
        fun onPlaylistCreated(playlist: Playlist)
    }

    private val imageUri = mutableStateOf<Uri?>(null)
    private lateinit var downloadUrls: List<Uri>
    private lateinit var listener: AddPlaylistDialogListener

    private lateinit var binding: DialogAddPlaylistBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddPlaylistBinding.inflate(layoutInflater)

        //songs = arguments?.getParcelableArrayList("songs") ?: emptyList()
        val builder = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle(R.string.add_playlist)
            .setPositiveButton(R.string.create) { _, _ ->
                // Create the playlist
                // Create a list of upload tasks
                val uploadTasks = mutableListOf<UploadTask>()

// Loop through the list of songs and create an upload task for each song
                for (song in songs) {
                    val storageRef = FirebaseStorage.getInstance().reference
                    val imageRef = storageRef.child("playlistsongs/${song}")
                    val uploadTask = imageRef.putFile(Uri.parse(song.uri))
                    uploadTasks.add(uploadTask)
                    uploadTask.continueWithTask { it ->
                        if (!it.isSuccessful) {
                            it.exception?.let {
                                throw it
                            }
                        }
                        imageRef.downloadUrl
                    }
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUrls = mutableListOf<String>()
                                // Loop through the list of upload tasks and get the download URL for each song
                                for (upt in uploadTasks) {
                                    if (upt.isSuccessful) {
                                        val downloadUrl = task.result
                                        downloadUrls.add(downloadUrl.toString())
                                    }
                                }
                                // Save the list of download URLs to the database
                                uploadImage(downloadUrls)
                            } else {
                                // Handle failure
                                Snackbar.make(
                                    binding.root,
                                    "Creating Failed...",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                }
            }
        return builder.create()
    }

    private fun uploadImage(downloadUrl: List<String>) {
        val playlistName = binding.editTextPlaylistName.text.toString()
        val playlist = Playlist(
            "",
            playlistName,
            FirebaseAuth.getInstance().currentUser!!.uid,
            downloadUrl,
            null
        )

        // Add the playlist to the Firebase database
        val playlistRef = FirebaseDatabase.getInstance().reference.child("playlists")
        val playlistId = playlistRef.push().key
        playlist.id = playlistId!!
        playlistRef.child(playlistId).setValue(playlist)

        // Notify the listener
        listener.onPlaylistCreated(playlist)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AddPlaylistDialogListener) {
            listener = context
        }
    }

    companion object {
        fun newInstance(context: Context, songIds: List<Song>): AddPlaylistDialogFragment {
            return AddPlaylistDialogFragment(songIds)
        }

    }


}

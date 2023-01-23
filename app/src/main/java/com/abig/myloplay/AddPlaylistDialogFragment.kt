package com.abig.myloplay

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.abig.myloplay.databinding.DialogAddPlaylistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddPlaylistDialogFragment(private var songs: List<String>) : DialogFragment() {

    interface AddPlaylistDialogListener {
        fun onPlaylistCreated(playlist: Playlist)
    }

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
                val playlistName = binding.editTextPlaylistName.text.toString()
                val playlist = Playlist(
                    null,
                    playlistName,
                    FirebaseAuth.getInstance().currentUser!!.uid,
                    songs,
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

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AddPlaylistDialogListener) {
            listener = context
        }
    }

    companion object {
        fun newInstance(context: Context, songIds: List<String>): AddPlaylistDialogFragment {
            return AddPlaylistDialogFragment(songIds)
        }

    }


}

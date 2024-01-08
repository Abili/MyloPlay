package com.abig.myloplay

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.OwnerPlItemBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllPlaylistAdapter : RecyclerView.Adapter<AllPlaylistAdapter.PlaylistViewHolder>() {
    private val playlists = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = OwnerPlItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
    }

    override fun getItemCount(): Int = playlists.size

    fun setPlaylists(playlists: List<Playlist>) {
        this.playlists.clear()
        this.playlists.addAll(playlists)
        notifyDataSetChanged()
    }

    inner class PlaylistViewHolder(private val binding: OwnerPlItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var auth: FirebaseAuth = FirebaseAuth.getInstance()
        //lateinit var lastSongAlbumArtUrl: String

        init {
            binding.root.setOnClickListener {
                // Handle playlist item click here
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedPlaylist = playlists[position]
                    // Implement logic to display songs for the selected playlist
                    val intent = Intent(binding.root.context, AudioActivity::class.java)
                    intent.putExtra(AudioActivity.EXTRA_PLAYLIST_ID, selectedPlaylist.id)
                    intent.putExtra(AudioActivity.EXTRA_USER_ID, selectedPlaylist.userId)
                    //intent.putExtra(AudioActivity.EXTRA_ALBUMART, lastSongAlbumArtUrl)
                    intent.putExtra(AudioActivity.EXTRA_USER_NAME, selectedPlaylist.userName)
                    intent.putExtra(AudioActivity.EXTRA_PLAYLIST_NAME, selectedPlaylist.userName)
                    binding.root.context.startActivity(intent)
                }
            }
        }

        fun bind(playlist: Playlist) {
            binding.textViewPlaylistName.text = playlist.name
            retrieveCurrentUserPlaylists(playlist.id!!, playlist.userId!!)
        }

        private fun retrieveCurrentUserPlaylists(playlistId: String, userId: String) {
            val database = FirebaseDatabase.getInstance().reference.child("users").child(userId)
                .child("playlists").child(playlistId)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val playlistName = snapshot.child("playlistName").getValue(String::class.java)
                    val songsSnapshot = snapshot.child("songs")
                    val lastSongSnapshot = songsSnapshot.children.lastOrNull()
                    val songsCount = songsSnapshot.childrenCount
                    if (songsCount.toInt() == 1) {
                        binding.textViewNumSongs.text = buildString {
                            append(songsCount)
                            append(" song")
                        }
                    } else {
                        binding.textViewNumSongs.text = buildString {
                            append(songsCount)
                            append(" songs")
                        }
                    }

                    val lastSongAlbumArtUrl =
                        lastSongSnapshot?.child("albumArt")?.getValue(String::class.java)
                    if (lastSongAlbumArtUrl != null) {
                        // Load the album art into the ImageView using Glide
                        Glide.with(binding.root).load(lastSongAlbumArtUrl).centerCrop()
                            .into(binding.ownersProfileImage)
                    } else {
                        Glide.with(binding.root).load(R.drawable.mylo_bg_logo).centerCrop()
                            .into(binding.ownersProfileImage)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
        }
    }
}

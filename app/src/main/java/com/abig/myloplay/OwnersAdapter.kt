package com.abig.myloplay

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.OwnerPlaylistItemBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OwnersAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val playlists = mutableListOf<Playlist>()

    fun add(playlist: Playlist) {
        playlists.add(playlist)
        notifyDataSetChanged()
    }

    fun clear() {
        playlists.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.owner_playlist_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var auth: FirebaseAuth
    private val binding: OwnerPlaylistItemBinding = OwnerPlaylistItemBinding.bind(itemView)

    fun bind(playlist: Playlist) {
        auth = FirebaseAuth.getInstance()
        retrieveCurrentUserPlaylists(playlist.id!!, playlist.userId) // Pass the playlist ID

        // Set the user name, playlist name, and number of songs
        binding.textViewPlaylistName.text = playlist.name
        binding.textViewNumSongs.text =
            itemView.context.getString(R.string.num_songs, playlist.songIds!!.size)

        // Set up click listener for the playlist
        itemView.setOnClickListener {
            // Open the playlist details screen
            val intent = Intent(itemView.context, PlaylistDetailsActivity::class.java)
            intent.putExtra(PlaylistDetailsActivity.EXTRA_PLAYLIST_ID, playlist.id)
            itemView.context.startActivity(intent)
        }
    }

    private fun retrieveCurrentUserPlaylists(playlistId: String, uid: String?) {
        val userId = auth.currentUser!!.uid
        val database =
            FirebaseDatabase.getInstance().reference.child("users")
                .child(userId).child("playlists").child(playlistId)
        if (userId == uid) {
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val playlistName = snapshot.child("playlistName").getValue(String::class.java)
                    val songsSnapshot = snapshot.child("songs")
                    val lastSongSnapshot = songsSnapshot.children.lastOrNull()
                    val lastSongAlbumArtUrl =
                        lastSongSnapshot?.child("albumArt")?.getValue(String::class.java)

                    // Load the album art into the ImageView using Glide
                    if (lastSongAlbumArtUrl != null) {
                        Glide.with(binding.root).load(lastSongAlbumArtUrl).centerCrop()
                            .into(binding.ownersProfileImage)
                    } else {
                        Glide.with(binding.root).load(auth.currentUser!!.photoUrl).centerCrop()
                            .placeholder(R.drawable.myloplay_logo).into(binding.ownersProfileImage)
                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }
    }
}

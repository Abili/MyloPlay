package com.abig.myloplay

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.OwnerPlaylistItemBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

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

        //current user's profile pic
        if (!auth.currentUser!!.photoUrl.toString().isEmpty()) {
            Glide
                .with(binding.root)
                .load(auth.currentUser!!.photoUrl)
                .centerCrop()
                .into(binding.ownersProfileImage);
        } else {
            Glide
                .with(binding.root)
                .load(auth.currentUser!!.photoUrl)
                .centerCrop()
                .placeholder(R.drawable.myloplay_logo)
                .into(binding.ownersProfileImage);
        }


        // Set the user name, playlist name, and number of songs

        //binding.textViewPlaylistName.text = playlist.userName
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
}


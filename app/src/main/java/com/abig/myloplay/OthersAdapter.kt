package com.abig.myloplay

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.OthersPlaylistItemBinding
import com.abig.myloplay.databinding.OwnerPlaylistItemBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class OthersAdapter : RecyclerView.Adapter<OthersViewHolder>() {


    private val playlists = mutableListOf<Playlist>()


    fun add(playlist: Playlist) {
        playlists.add(playlist)
        notifyDataSetChanged()
    }

    fun clear() {
        playlists.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OthersViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.others_playlist_item, parent, false)
        return OthersViewHolder(view)
    }

    override fun onBindViewHolder(holder: OthersViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}

class OthersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding: OthersPlaylistItemBinding = OthersPlaylistItemBinding.bind(itemView)
    private lateinit var auth: FirebaseAuth
    private val userProfile = FirebaseDatabase.getInstance().reference.child("users")
    fun bind(playlist: Playlist) {
        auth = FirebaseAuth.getInstance()
        //val userId = auth.uid
        val userId = userProfile.push().key
        if(userId.equals(auth.currentUser!!.uid)){

        }
        // Set the user name, playlist name, and number of songs
        if (!auth.currentUser!!.photoUrl.toString().isEmpty()) {
            Glide
                .with(binding.root)
                .load(auth.currentUser!!.photoUrl)
                .centerCrop()
                .into(binding.othersProfileImage);
        } else {
            Glide
                .with(binding.root)
                .load(auth.currentUser!!.photoUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_user)
                .into(binding.othersProfileImage);
        }
        //binding.profileImage.setImageURI(auth.currentUser!!.photoUrl)

        if (!auth.currentUser!!.displayName!!.isEmpty()) {
            binding.textViewUserName.text = auth.currentUser!!.displayName
        } else {
            binding.textViewUserName.text = "unknown"
        }
        binding.textViewPlaylistName.text = playlist.name
        binding.textViewNumSongs.text =
            itemView.context.getString(R.string.num_songs, playlist.songIds.size)

        // Set up click listener for the playlist
        itemView.setOnClickListener {
            // Open the playlist details screen
            val intent = Intent(itemView.context, PlaylistDetailsActivity::class.java)
            intent.putExtra(PlaylistDetailsActivity.EXTRA_PLAYLIST_ID, playlist.id)
            itemView.context.startActivity(intent)
        }
    }
}


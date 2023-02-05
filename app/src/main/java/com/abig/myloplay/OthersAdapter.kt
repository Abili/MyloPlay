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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
    private val playlistId =
        FirebaseDatabase.getInstance().reference.child("playlist").child("userId")

    fun bind(playlist: Playlist) {
        val userProfileRef = FirebaseDatabase.getInstance().reference.child("users")

        // Get the userId of the playlist
        val playlistUserId = playlist.userId
        var userName = ""
        var profileUrl = ""
        auth = FirebaseAuth.getInstance()
        //val userId = auth.uid
        val userId = auth.currentUser!!.uid
        if (!playlistId.equals(auth.currentUser!!.uid)) {
            userProfile.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profile = snapshot.getValue(User::class.java)
                    if (profile != null) {
                        userName = profile.username
                        profileUrl = profile.imageUrl

                        // Set the user name, playlist name, and number of songs
                        if (profileUrl.isNotEmpty()) {
                            Glide
                                .with(binding.root)
                                .load(profileUrl)
                                .centerCrop()
                                .into(binding.othersProfileImage);
                        } else {
                            Glide
                                .with(binding.root)
                                .load(R.drawable.ic_user)
                                .centerCrop()
                                .into(binding.othersProfileImage);
                        }
                        //binding.profileImage.setImageURI(auth.currentUser!!.photoUrl)

                        binding.textViewUserName.text = userName
                        binding.textViewPlaylistName.text = playlist.name
                        binding.textViewNumSongs.text =
                            itemView.context.getString(R.string.num_songs, playlist.songIds.size)


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }


        // Set up click listener for the playlist
        itemView.setOnClickListener {
            // Open the playlist details screen
            val intent = Intent(itemView.context, PlaylistDetailsActivity::class.java)
            intent.putExtra(PlaylistDetailsActivity.EXTRA_PLAYLIST_ID, playlist.id)
            itemView.context.startActivity(intent)
        }
    }
}


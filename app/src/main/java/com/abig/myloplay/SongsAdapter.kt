package com.abig.myloplay

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.SongItemBinding
import com.bumptech.glide.Glide

class SongsAdapter : RecyclerView.Adapter<SongsAdapter.PlaylistViewHolder>() {

    private val playlists = mutableListOf<Song>()



    fun add(song: Song) {
        playlists.add(song)
        notifyDataSetChanged()
    }

    fun clear() {
        playlists.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.song_item, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
    }

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: SongItemBinding = SongItemBinding.bind(itemView)
        private var currentIndex = -1 //initialize with -1 to indicate no song is currently playing
        fun bind(song: Song) {
            binding.songTitle.text = song.name
            binding.songArtist.text = song.artist
            binding.songDuration.text = song.duration

            Glide
                .with(binding.root)
                .load(song.uri)
                .centerCrop()
                .placeholder(R.drawable.utubemusic)
                .into(binding.thumbnail);


            // Set up click listener for the playlist
            itemView.setOnClickListener {
                // Open the playlist details screen
                //play the song from here
//                val intent = Intent(itemView.context, MyloPlayer::class.java)
//                intent.putExtra(PlaylistDetailsActivity.EXTRA_PLAYLIST_ID, song.uri)
//                itemView.context.startActivity(intent)

                val intent = Intent(itemView.context, MyloPlayer::class.java)
                intent.putExtra(MyloPlayer.EXTRA_SONG_URI,song.uri)
                itemView.context.startActivity(intent)
                currentIndex = adapterPosition
            }

        }
    }
}


package com.abig.myloplay

// EditPlaylistSongsAdapter.kt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.ItemSongBinding

class EditPlaylistSongsAdapter : RecyclerView.Adapter<EditPlaylistSongsAdapter.SongViewHolder>() {

    private var songs: List<AudioFile> = emptyList()

    fun setSongs(songs: List<AudioFile>) {
        this.songs = songs
        notifyDataSetChanged()
    }
    fun getSongs(): List<AudioFile> {
        return songs.toList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSongBinding.inflate(inflater, parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int = songs.size

    class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: AudioFile) {
            binding.textTitle.text = song.title
            binding.textArtist.text = song.artist
            binding.textDuration.text = song.duration
            // Add more bindings for other song details as needed
        }
    }
}

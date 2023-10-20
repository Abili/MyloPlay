package com.abig.myloplay

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.ItemSongBinding

class AudioListAdapter :
    RecyclerView.Adapter<AudioListAdapter.ViewHolder>() {
    private val songs = mutableListOf<AudioFile>()

    fun setData(song: MutableList<AudioFile>) {
        songs.clear()
        songs.addAll(song)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song)

    }

    override fun getItemCount(): Int {
        return songs.size
    }

    inner class ViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                // Handle playlist item click here
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedSong = songs[position]
                    // Implement logic to display songs for the selected playlist
                    val intent = Intent(itemView.context, MyloPlayer::class.java)
                    intent.putStringArrayListExtra(
                        MyloPlayer.PLAYLIST,
                        ArrayList(songs.map { it.downloadUrl })
                    )
                    intent.putExtra(MyloPlayer.CURRENT_POSITION, position)
                    intent.putExtra(MyloPlayer.SONG_ID, selectedSong.downloadUrl)
                    intent.putStringArrayListExtra(
                        MyloPlayer.SONG_TITLE,
                        ArrayList(songs.map { it.title })
                    )
                    intent.putStringArrayListExtra(
                        MyloPlayer.ARTIST,
                        ArrayList(songs.map { it.artist })
                    )
                    intent.putExtra(MyloPlayer.SONG_DURATION, selectedSong.duration)
                    itemView.context.startActivity(intent)
                }
            }

            binding.songMenu.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, binding.rootLayout)
                popupMenu.inflate(R.menu.song_menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.song_download -> {
                            // Handle the download action
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                val selectedSong = songs[position]
                                // Pass the download URL to the AudioActivity
                                (view.context as? AudioActivity)?.downloadSong(
                                    selectedSong.downloadUrl!!,
                                    selectedSong.title
                                )
                            }
                            true // Return true to indicate that the item is handled
                        }

                        R.id.song_recommend -> {
                            // Handle the recommend action
                            true
                        }

                        else -> false // Return false for unhandled items
                    }
                }
                popupMenu.show()
            }
        }

        fun bind(song: AudioFile) {
            binding.textTitle.text = song.title
            binding.textDuration.text = song.duration.toString()
            binding.textArtist.text = song.artist.toString()
            binding.textTitle.isSelected = true
        }
    }
}

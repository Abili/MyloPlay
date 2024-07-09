package com.abig.myloplay

import android.app.AlertDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.abig.myloplay.databinding.PlayerItemSongBinding

class MiniPlayerAdapter(
    private val context: Context,
    private val onSongClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<MiniPlayerAdapter.ViewHolder>() {

    private val songs = mutableListOf<AudioFile>()
    var currentPosition: Int = -1

    fun setData(songList: List<AudioFile>) {
        songs.clear()
        songs.addAll(songList)
        notifyDataSetChanged()
    }

    fun updateCurrentPosition(position: Int) {
        val previousPosition = currentPosition
        currentPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(currentPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlayerItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song, position)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    inner class ViewHolder(private val binding: PlayerItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onSongClickListener.invoke(position)
                }
            }

            binding.songMenu.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, binding.rootLayout)
                popupMenu.inflate(R.menu.song_menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.song_download -> {
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                val selectedSong = songs[position]
                                (context as? AudioActivity)?.downloadSong(
                                    selectedSong.downloadUrl!!,
                                    selectedSong.title
                                )
                            }
                            true
                        }

                        R.id.song_recommend -> {
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                val selectedSong = songs[position]
                                // Handle recommend action
                            }
                            true
                        }

                        R.id.deleteSong -> {
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                val selectedSong = songs[position]
                                showRemoveConfirmationDialog(selectedSong)
                            }
                            true
                        }

                        else -> false
                    }
                }
                popupMenu.show()
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(song: AudioFile, position: Int) {
            binding.textTitle.text = song.title
            binding.textDuration.text = song.duration.toString()
            binding.textArtist.text = song.artist.toString()
            binding.textTitle.isSelected = true

            if (position == currentPosition) {
                itemView.setBackgroundColor(context.getColor(com.hbb20.R.color.highlighted_text_material_dark))
            } else {
                itemView.setBackgroundColor(context.getColor(android.R.color.transparent))
            }
        }

        private fun showRemoveConfirmationDialog(song: AudioFile) {
            AlertDialog.Builder(context)
                .setTitle("Remove Song")
                .setMessage("Are you sure you want to remove this song from the playlist?")
                .setPositiveButton("Remove") { _, _ ->
                    // Handle removal
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}

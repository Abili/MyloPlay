package com.abig.myloplay

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.FragmentDownloadDetailsBinding
import java.io.File

class DownloadDetailsFragment : AppCompatActivity() {

    private lateinit var binding: FragmentDownloadDetailsBinding
    private lateinit var adapter: DownloadDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDownloadDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Get the folder name from arguments
        val folderName = intent?.getStringExtra(ARG_FOLDER_NAME)

        adapter = DownloadDetailsAdapter()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Fetch songs from the specified folder
        folderName?.let {
            val folder = File(this.filesDir, it)
            val songs = getSongsInFolder(folder, this, folderName)
            adapter.setSong(songs)
        }
    }

    private fun getSongsInFolder(
        folder: File,
        context: Context,
        folderName: String
    ): List<AudioFile> {
        val audioFiles = mutableListOf<AudioFile>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val title =
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                val duration =
                    it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val artist =
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val audioUri: Uri = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )

                val albumArt = getAlbumArtFromMediaStore(audioUri, context)

                val audioFile = AudioFile(
                    id.toString(),
                    audioUri.toString(),
                    title,
                    artist ?: "Unknown",
                    duration.toString(),
                    albumArt
                )
                audioFiles.add(audioFile)
            }

        }

        return audioFiles
    }


    private fun getAlbumArtFromMediaStore(uri: Uri, context: Context): Bitmap? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val albumArtByteArray = retriever.embeddedPicture
            if (albumArtByteArray != null) {
                // Convert the byte array to a Bitmap
                return BitmapFactory.decodeByteArray(albumArtByteArray, 0, albumArtByteArray.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            retriever.release()
        }
        return null
    }


    private fun getDurationFromMediaStore(uri: Uri): Long {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(this, uri)
        mediaPlayer.prepare()
        val duration = mediaPlayer.duration.toLong()
        mediaPlayer.release()
        return duration
    }


    companion object {
        private const val ARG_FOLDER_NAME = "folderName"

    }
}

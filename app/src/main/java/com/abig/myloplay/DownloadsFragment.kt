package com.abig.myloplay

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.FragmentDownloadsBinding
import java.io.File

class DownloadsFragment : Fragment() {

    private lateinit var binding: FragmentDownloadsBinding
    private lateinit var adapter: DownloadedFilesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDownloadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DownloadedFilesAdapter()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Fetch audio files from the "myloplay" folder and populate the adapter
        fetchAudioFilesFromMyloplayFolder(requireContext())
    }

    private fun fetchAudioFilesFromMyloplayFolder(context: Context) {
        val folderName = "myloplay"
        val folder = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), folderName)
        val audioFiles = getSongsInFolder(folder, context, folderName)
        adapter.addFile(audioFiles.map { FileString(it.title!!) })
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
}

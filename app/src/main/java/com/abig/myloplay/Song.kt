package com.abig.myloplay

import android.content.Context
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val artist: String = "",
    val album: String = "",
    val duration: String = "",
    val uri: String = ""
) :
    Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(artist)
        parcel.writeString(album)
        parcel.writeString(duration)
        parcel.writeString(uri)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Song> {
            override fun createFromParcel(parcel: Parcel): Song {
                return Song(parcel)
            }

            override fun newArray(size: Int): Array<Song?> {
                return arrayOfNulls(size)
            }
        }

        fun fromUri(context: Context, uri: Uri): Song? {
            val musicResolver = context.contentResolver
            val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val musicCursor = musicResolver.query(
                musicUri,
                null,
                MediaStore.Audio.Media._ID + " =?",
                arrayOf(uri.lastPathSegment),
                null
            )
            var song: Song? = null
            if (musicCursor != null && musicCursor.moveToFirst()) {
                val titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                val idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val duration = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                val thisTitle = musicCursor.getString(titleColumn)
                val thisArtist = musicCursor.getString(artistColumn)
                val thisAlbum = musicCursor.getString(albumColumn)
                val thisId = musicCursor.getString(idColumn)
                val thisDuration = musicCursor.getString(duration)
                song = Song(thisId, thisTitle, thisArtist, thisAlbum, thisDuration, uri.toString())
            }
            musicCursor?.close()
            return song
        }
    }


}

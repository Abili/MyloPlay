package com.abig.myloplay

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class AudioFile(
    val id: String? = "",
    val downloadUrl: String? = "",
    val title: String? = "",
    val artist: String? = "",
    val duration: String? = "",
    val albumArt: Bitmap? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Bitmap::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(downloadUrl)
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeString(duration)
        parcel.writeParcelable(albumArt, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AudioFile> {
        override fun createFromParcel(parcel: Parcel): AudioFile {
            return AudioFile(parcel)
        }

        override fun newArray(size: Int): Array<AudioFile?> {
            return arrayOfNulls(size)
        }
    }
}

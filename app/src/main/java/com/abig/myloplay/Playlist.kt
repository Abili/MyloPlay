package com.abig.myloplay

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

@TypeConverters(StringListConverter::class)
data class Playlist(
    var id: String?,
    var name: String?,
    val userId: String?,
    var songIds: List<String>?,
    val userName: String?,// Add this field
    //val time: Timestamp = Timestamp.now()
    var time: Long = System.currentTimeMillis()
) : Serializable
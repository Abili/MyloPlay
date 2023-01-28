package com.abig.myloplay

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = "playlists")
@TypeConverters(StringListConverter::class)
data class Playlist(
    @PrimaryKey
    var id: String,
    var name: String = "",
    val userId: String = "",
    var songIds: List<String>,
    val userName: String? = "",// Add this field
    //val time: Timestamp = Timestamp.now()
    var time: Long = System.currentTimeMillis()
) : Serializable {


    constructor() : this(id = "", name = "", userId = "", songIds = emptyList(), userName = null)
}
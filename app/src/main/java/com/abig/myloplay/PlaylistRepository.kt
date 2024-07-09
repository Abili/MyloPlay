package com.abig.myloplay

import android.content.Context
import com.abig.myloplay.Util.formatPhoneNumber
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PlaylistRepository(private val context: Context) {
    val database = FirebaseDatabase.getInstance().reference
    val auth = FirebaseAuth.getInstance()

    //val context: Context? = Activity().applicationContext
    fun retrieveCurrentUserPlaylists(onMyPlaylistLoaded: (List<Playlist>) -> Unit) {
        // Retrieve playlists for the current user
        val playlists = mutableListOf<Playlist>()
        // ...
        val userId = auth.currentUser!!.uid
        database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (usersnap in snapshot.children) {
                    val username = usersnap.child("username").getValue(String::class.java)
                    val uid = usersnap.child("id").getValue(String::class.java)
                    val plsnapshot =
                        snapshot.child(userId).child("playlists").child("single").child("playlists")
                    for (playlistShot in plsnapshot.children) {
                        val playlistId = playlistShot.key
                        val playlistname =
                            playlistShot.child("playlistName").getValue(String::class.java)
                        val playlist = Playlist(
                            playlistId!!,
                            playlistname!!,
                            userId,
                            null,
                            username,
                            System.currentTimeMillis(),
                            null
                        )
                        playlists.clear()
                        playlists.add(playlist)
                    }
                    onMyPlaylistLoaded(playlists)
                }
            }


            override fun onCancelled(error: DatabaseError) {
                //error
            }
        })

    }

    fun retrieveOtherUsersPlaylists(onOthersPlaylistLoaded: (List<Playlist>) -> Unit) {
        val currentUserContacts = mutableListOf<String>()

        // Fetch the current user's contacts from the phone
        val contacts = ContactsRetriever(context).retrieveContacts()

        // Extract phone numbers from the retrieved contacts and format them
        currentUserContacts.addAll(contacts.map { formatPhoneNumber(it.phone) })

        // Retrieve playlists for other users
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        database.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playlists = mutableListOf<Playlist>()

                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key
                    val username = userSnapshot.child("username").getValue(String::class.java)
                    val phone = userSnapshot.child("phone").getValue(String::class.java)

                    if (userId != currentUserId) {
                        val isCurrentUserRestrictedByUser =
                            userSnapshot.child("restricted").child(currentUserId)
                                .getValue(Boolean::class.java) ?: false

                        val isCurrentUserInContactList =
                            currentUserContacts.contains(
                                formatPhoneNumber(
                                    phone!!
                                )
                            )

                        if (!isCurrentUserRestrictedByUser && isCurrentUserInContactList) {
                            val playlistsSnapshot =
                                userSnapshot.child("playlists").child("single").child("playlists")
                            for (playlistShot in playlistsSnapshot.children) {
                                val playlistId = playlistShot.key
                                val playListName = playlistShot.child("playlistName")
                                    .getValue(String::class.java)

                                val playlist = Playlist(
                                    playlistId!!,
                                    playListName!!,
                                    userId!!,
                                    null,
                                    username,
                                    System.currentTimeMillis(),
                                    null
                                )
                                playlists.clear()
                                playlists.add(playlist)
                                // allPlaylists.add(playlist)
                            }
                        }
                    }
                    onOthersPlaylistLoaded(playlists)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }

    fun retrieveGroupPlaylists(onGroupPlaylistLoaded: (List<Playlist>) -> Unit) {
        val playlists = mutableListOf<Playlist>()
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key
                    val username = userSnapshot.child("username").getValue(String::class.java)

                    val playlistsSnapshot =
                        userSnapshot.child("playlists").child("group").child("playlists")
                    if (playlistsSnapshot.exists()) {
                        for (playlistShot in playlistsSnapshot.children) {
                            val playlistId = playlistShot.key
                            val playlistName =
                                playlistShot.child("playlistName").getValue(String::class.java)

                            // Check if the current user's ID is either the creator or in the "userIds" array
                            val userIdsSnapshot = playlistShot.child("userIds")
                            if (playlistShot.child("userId").value == currentUserId || userIdsSnapshot.children.any { it.value == currentUserId }) {
                                // Log playlist information for debugging

                                val playlist = Playlist(
                                    playlistId!!,
                                    playlistName!!,
                                    userId!!,
                                    null,
                                    username,
                                    System.currentTimeMillis(),
                                    null
                                )
                                playlists.clear()
                                playlists.add(playlist)
                                //allPlaylists.add(playlist)
                            }
                        }
                    }
                    onGroupPlaylistLoaded(playlists)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }


//
//    suspend fun deletePlaylist(playlist: Playlist) {
//        playlistDao.deletePlaylist(playlist)
//    }
//
//    fun getSongs(songIds: List<String>): List<Song> {
//        return playlistDao.getSongs(songIds)
//    }
//
//     fun updatePlaylist(playlist: Playlist) {
//        playlistDao.updatePlaylist(playlist)
//    }
}


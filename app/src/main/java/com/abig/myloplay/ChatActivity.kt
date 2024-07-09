package com.abig.myloplay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.abig.myloplay.databinding.ActivityChatBinding
import com.android.volley.Request.Method.POST
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var currentUserChatAdapter: CurrentUserChatAdapter

    // lateinit var friendsChatAdapter: FriendsChatAdapter
    lateinit var auth: FirebaseAuth
    val msgs = mutableListOf<Message>()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChatBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        val playlistType = intent.getStringExtra("playlistType")
        val playlistId = intent.getStringExtra("playlistId")
        val reciepientId = intent.getStringExtra("userId")
        //val username = intent.getStringExtra("username")
        val senderId = auth.currentUser!!.uid

        currentUserChatAdapter = CurrentUserChatAdapter(senderId)

        binding.chatrecycler.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = this@ChatActivity.currentUserChatAdapter
        }



        binding.sendmsg.setOnClickListener {
            val msg = binding.chatText.text.toString().trim()
            sendMessage(msg, senderId, playlistType, playlistId, reciepientId)
        }
        loadMessages(senderId, reciepientId!!, playlistType, playlistId)
    }


    private fun sendMessage(
        msg: String,
        senderId: String,
        playlistType: String?,
        playlistId: String?,
        reciepientId: String?
    ) {
        val chatRef = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(reciepientId!!)
            .child("playlists")
            .child(playlistType!!)
            .child("playlists")
            .child(playlistId!!)
            .child("chats")

        val chatId = chatRef.push().key
        val message =
            Message(senderId, reciepientId, "Username", System.currentTimeMillis().toString(), msg)
        chatRef.child(chatId!!).setValue(message)

        // Send notification to the recipient
        sendNotification("New Message", msg, senderId)
    }

    private fun loadMessages(
        senderId: String,
        reciepientId: String,
        playlistType: String?,
        playlistId: String?
    ) {
        val chatRef = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(reciepientId)
            .child("playlists")
            .child(playlistType!!)
            .child("playlists")
            .child(playlistId!!)
            .child("chats")

        val messageListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                for (chatSnapshot in snapshot.children) {
                    val rid = chatSnapshot.child("reciepientId").getValue(String::class.java)
                    val username = chatSnapshot.child("username").getValue(String::class.java)
                    val chatTime = chatSnapshot.child("chatTime").getValue(String::class.java)
                    val msg = chatSnapshot.child("msg").getValue(String::class.java)
                    messages.add(Message(senderId, rid, username, chatTime, msg))
                }
                currentUserChatAdapter.setMessages(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }

        chatRef.addValueEventListener(messageListener)
    }

    private fun sendNotification(title: String, message: String, recipientUserId: String) {
        // Construct the notification message
//        val notification = Notification(
//            title,
//            message
//        )

        // Send the notification to the recipient user using their FCM token
        val recipientRef = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(recipientUserId)
        recipientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fcmToken = snapshot.child("fcmToken").getValue(String::class.java)
                if (!fcmToken.isNullOrEmpty()) {
                    // Send the notification to the recipient user's FCM token
                    val request = JsonObjectRequest(
                        POST,
                        "https://fcm.googleapis.com/fcm/send",
                        JSONObject().apply {
                            put("to", fcmToken)
                            put("data", JSONObject().apply {
                                put("title", title)
                                put("message", message)
                            })
                        },
                        { _ -> /* Notification sent successfully */ },
                        { error -> /* Handle error */ }
                    )

                    // Add your server key as the authorization header
                    request.headers["Authorization"] =
                        "key=AAAA5Vdprfw:APA91bHpYcEi_Q2d8yHZ8j_h1Btn5IC9U0dtsKaSI5WsTZ4qAvyI_hoazGbRUFCcJW-V2NBgZqCvWoaDXKHbF_betDTnQzVYbu8pDUwXREz9hW5R3z1bC6vZEuKtDEVe6kp7-k-GXS1Q"

                    // Add the request to the request queue
                    Volley.newRequestQueue(this@ChatActivity).add(request)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


}
package com.abig.myloplay

class Message(
    val senderId: String? = "",
    val reciepientId: String? = "",
    val username: String? = "",
    val chatTime: String? = "",
    val msg: String? = "",
    var seen: Boolean = false
)

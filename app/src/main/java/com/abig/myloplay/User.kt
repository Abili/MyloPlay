package com.abig.myloplay

class User(
    var id: String = "",
    var imageUrl: String? = null,
    var username: String = "",
    var phone: String = "",
    var email: String? = "",
    var isSelected: Boolean? = false
) {


    constructor() : this(
        id = "",
        imageUrl = "",
        username = "",
        phone = "",
        email = "",
        isSelected = false
    )
}

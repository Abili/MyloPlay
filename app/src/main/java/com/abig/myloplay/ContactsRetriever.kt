package com.abig.myloplay

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract

class ContactsRetriever(private val context: Context) {

    // Function to retrieve user contacts
    fun retrieveContacts(): List<User> {
        val contactsList = mutableListOf<User>()

        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val phoneNumberColumnIndex =
                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val userNameColumnIndex =
                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val userPhotoColumnIndex =
                it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
            while (it.moveToNext()) {
                val phoneNumber = it.getString(phoneNumberColumnIndex)
                val username = it.getString(userNameColumnIndex)
                val imageUriString = it.getString(userPhotoColumnIndex)

                val imageUri = imageUriString?.let { uri ->
                    Uri.parse(uri)
                }

                val user = User("", imageUri.toString(), username, phoneNumber, "")
                contactsList.add(user)
            }
        }

        // Sort the contacts list alphabetically based on usernames
        contactsList.sortBy { it.username }

        return contactsList
    }
}

package com.abig.myloplay

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object Util {
    fun formatDuration(duration: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    fun formatTime(timestamp: Long): String {
        val date = Date(timestamp)
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return timeFormat.format(date)
    }
    fun formatPhoneNumber(phone: String): String {
        return if (phone.startsWith("0")) {
            // Convert phone number starting with zero to +256 format
            "+256${phone.substring(1).replace(" ", "")}"
        } else {
            // Keep the existing format if it already starts with +256
            phone.replace(" ", "")
        }
    }


}
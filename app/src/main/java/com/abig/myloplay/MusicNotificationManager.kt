package com.abig.myloplay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle

class MusicNotificationManager(private val service: MusicService) {

    private val notificationManager: NotificationManager =
        service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(title: String, artist: String) {
        val playPauseAction = if (service.isPlaying()) {
            NotificationCompat.Action(
                R.drawable.pause, // Make sure this drawable resource exists
                "Pause",
                playbackAction(MusicService.ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.ic_play, // Make sure this drawable resource exists
                "Play",
                playbackAction(MusicService.ACTION_PLAY)
            )
        }

        val notification = NotificationCompat.Builder(service, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(artist)
            .setSmallIcon(R.drawable.music_note) // Make sure this drawable resource exists
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .addAction(
                NotificationCompat.Action(
                    R.drawable.previous, // Make sure this drawable resource exists
                    "Previous",
                    playbackAction(MusicService.ACTION_PREVIOUS)
                )
            )
            .addAction(playPauseAction)
            .addAction(
                NotificationCompat.Action(
                    R.drawable.next, // Make sure this drawable resource exists
                    "Next",
                    playbackAction(MusicService.ACTION_NEXT)
                )
            )
            .setStyle(
                MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setContentIntent(contentIntent())
            .setOngoing(service.isPlaying())
            .build()

        service.startForeground(NOTIFICATION_ID, notification)
    }

    fun updateNotification(isPlaying: Boolean) {
        showNotification(service.songTitles[service.currentPosition], service.artists[service.currentPosition])
    }

    private fun contentIntent(): PendingIntent {
        val openPlayerIntent = Intent(service, MiniPlayerActivity::class.java)
        return PendingIntent.getActivity(service, 0, openPlayerIntent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun playbackAction(action: String): PendingIntent {
        val playbackAction = Intent(service, MusicService::class.java).apply { this.action = action }
        return PendingIntent.getService(service, 0, playbackAction, PendingIntent.FLAG_IMMUTABLE)
    }

    companion object {
        private const val CHANNEL_ID = "music_player_channel"
        private const val NOTIFICATION_ID = 1
    }
}

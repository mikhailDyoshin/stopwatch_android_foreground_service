package com.example.stopwatchproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.stopwatchproject.stopwatch.StopwatchService

class StopwatchNotificationManager(
    private val context: Context,
    private val notificationManager: NotificationManager
) {

    private val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)

    fun setUpNotification() {
        createChannel()
        createNotification()
    }

    fun updateNotification(timeValue: Long) {
        notificationBuilder.setContentTitle("Elapsed time: $timeValue s.")
        displayNotification()
    }

    fun cancel() {
        notificationManager.cancel(NOTIFICATION_ID)
    }


    private fun createActionIntent(action: StopwatchNotificationAction): PendingIntent {
        return PendingIntent.getService(
            context,
            0,
            Intent(context, StopwatchService::class.java).setAction(action.actionString),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "PlayerNotificationChannel",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    private fun displayNotification() {
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotification(timeValue: Long = 0L) {
        val stopIntent =
            createActionIntent(action = StopwatchNotificationAction.ACTION_STOP)

        notificationBuilder
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle("Elapsed time: $timeValue s.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(R.drawable.ic_launcher_foreground, "Stop", stopIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        displayNotification()
    }

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "PlaybackServiceChannel"
    }


    enum class StopwatchNotificationAction(val actionString: String) {
        ACTION_STOP("com.example.stopwatchproject.ACTION_STOP"),
    }
}
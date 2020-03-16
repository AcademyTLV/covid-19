package com.android_academy.covid_19.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android_academy.covid_19.R
import com.android_academy.covid_19.providers.UserLocationModel

interface CodeOrangeNotificationManager {
    fun showCollisionFound(list: List<UserLocationModel>)
}

private const val GENERAL_NOTIFICATION_CHANNEL_ID = "General_Notification_Channel_Id"
private const val FOUND_CORRELATION_NOTIFICATION_ID = 101

class CodeOrangeNotificationManagerImpl(
    private val context: Context
) : CodeOrangeNotificationManager {

    override fun showCollisionFound(list: List<UserLocationModel>) {
        createNotificationChannel()
        val builder = NotificationCompat.Builder(context, GENERAL_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.ahtung))
            .setContentText(context.getString(R.string.open_app))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(FOUND_CORRELATION_NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(GENERAL_NOTIFICATION_CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

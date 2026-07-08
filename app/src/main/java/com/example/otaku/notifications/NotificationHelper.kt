package com.example.otaku.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.otaku.MainActivity
import com.example.otaku.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        companion object {
            const val CHANNEL_ID = "general_notifications"
            private const val SCHEME = "com.example.otaku"
        }

        init {
            createNotificationChannel()
        }

        private fun createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.notification_channel_name)
                val descriptionText = context.getString(R.string.notification_channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel =
                    NotificationChannel(CHANNEL_ID, name, importance).apply {
                        description = descriptionText
                    }
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun showNotification(
            id: Int,
            title: String,
            message: String,
            deepLinkUri: String? = null,
        ) {
            val intent =
                if (deepLinkUri != null) {
                    Intent(Intent.ACTION_VIEW, deepLinkUri.toUri(), context, MainActivity::class.java)
                } else {
                    Intent(context, MainActivity::class.java)
                }

            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(
                    context,
                    id,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                )

            val builder =
                NotificationCompat
                    .Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.otaku)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                try {
                    notify(id, builder.build())
                } catch (e: SecurityException) {
                }
            }
        }

        fun getMediaDetailDeepLink(
            mediaId: Int,
            mediaType: String,
        ): String = "$SCHEME://media_detail?id=$mediaId&mediaType=$mediaType"

        fun getNotificationsDeepLink(): String = "$SCHEME://notifications"
    }

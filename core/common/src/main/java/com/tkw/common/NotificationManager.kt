package com.tkw.common

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat


object NotificationManager {
    const val NOTI_CH = "NOTI_CH"
    const val MUTE_CH = "MUTE_CH"
    private const val NOTIFICATION_GROUP_NAME = "GROUP_NAME"
    private lateinit var homeIntent: PendingIntent
    const val TIMEOUT: Long = 1000 * 30

    fun setPendingIntent(intent: PendingIntent) {
        homeIntent = intent
    }

    fun createNotificationChannel(context: Context) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val name = context.getString(R.string.channel_name)
        val description = context.getString(R.string.channel_desc)
        val channel = NotificationChannel(NOTI_CH, name, importance).apply {
            this.description = description
        }
        val muteChannel = NotificationChannel(MUTE_CH, name, importance).apply {
            this.description = description
            setSound(null, null)
            enableVibration(false)
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        notificationManager.createNotificationChannel(muteChannel)
    }

    fun buildNotification(
        context: Context,
        drawable: Int,
        title: String,
        text: String,
        channelId: String
    ): NotificationCompat.Builder {
        val contentView = RemoteViews(context.packageName, R.layout.custom_notification)
        contentView.setTextViewText(R.id.tv_title, title)
        contentView.setTextViewText(R.id.tv_content, text)

        val builder = NotificationCompat.Builder(context, channelId)
        builder.setSmallIcon(drawable)
            .setContentTitle(title)
            .setContentText(text)
            .setCategory(Notification.CATEGORY_ALARM)
            .setContentIntent(homeIntent)
            .setFullScreenIntent(getFullScreenIntent(context), true)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(contentView)
            .setCustomHeadsUpContentView(contentView)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setTimeoutAfter(TIMEOUT)

        return builder
    }

    fun buildSummaryNotification(
        context: Context,
        drawable: Int,
        channelId: String
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, channelId)
        builder.setSmallIcon(drawable)
            .setGroup(NOTIFICATION_GROUP_NAME)
            .setGroupSummary(true)
        return builder
    }

    fun notify(
        context: Context,
        builder: NotificationCompat.Builder
    ) {
        val notificationId = "${System.currentTimeMillis()}".hashCode()
        val notificationManager: NotificationManager =
            context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    private fun getFullScreenIntent(context: Context): PendingIntent {
        val intent = Intent(context, AlarmActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(
            context,
            0x01,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}
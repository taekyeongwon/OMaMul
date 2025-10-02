package com.tkw.alarmnoti

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tkw.domain.model.RingTone
import com.tkw.domain.model.RingToneMode

object NotificationManager {
    private const val NOTI_CH = "NOTI_CH"
    private const val MUTE_CH = "MUTE_CH"
    private const val DEFAULT_CH = "DEFAULT_CH"
    private const val NOTIFICATION_GROUP_NAME = "GROUP_NAME"
    private lateinit var homeIntent: PendingIntent
    const val TIMEOUT: Long = 1000 * 30 // 30초 후 자동 종료

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
        val defaultChannel = NotificationChannel(DEFAULT_CH, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
            this.description = description
            setSound(null, null)
            enableVibration(false)
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        notificationManager.createNotificationChannel(muteChannel)
        notificationManager.createNotificationChannel(defaultChannel)
    }

    fun setContentClickPendingIntent(intent: PendingIntent) {
        homeIntent = intent
    }

    fun notify(context: Context, ringtoneMode: RingToneMode) {
        val notificationId = "${System.currentTimeMillis()}".hashCode()
        val builder = buildNotification(
            context,
            R.drawable.noti_foreground,
            context.getString(R.string.notification_title),
            context.getString(R.string.notification_text),
            getChannel(ringtoneMode.getCurrentMode().name) //핸드폰 설정대로면 NOTI_CH, 그 외 MUTE_CH
        )
        //휴대폰 설정과 동일이라면 그대로 빌드.
        //알림 표시 안하는 경우 builder.setSilent(true) 적용
        if(ringtoneMode.isSilence) {
            builder.setSilent(true)
        }
        if (canUseFullScreenIntent(context)) {
            notifyIfFullScreen(context, notificationId)
            builder.fullScreenBuilder(
                context,
                context.getString(R.string.notification_title),
                context.getString(R.string.notification_text)
            )
        }
//        builder.setDismissListener(context, notificationId)

        val notificationManager: NotificationManager =
            context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
        play(ringtoneMode.getCurrentMode().name, context)
    }

    /**
     * ringtone mode
     * 핸드폰 설정과 동일한 경우 NOTI_CH 리턴
     * 이외 MUTE_CH 리턴
     */
    private fun getChannel(ringtoneMode: String): String {
        return when(ringtoneMode) {
            RingTone.DEVICE.name -> NOTI_CH
            else -> MUTE_CH
        }
    }

    private fun canUseFullScreenIntent(context: Context): Boolean {
        return if(Build.VERSION.SDK_INT >= 34) {
            val notificationManager: NotificationManager =
                context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.canUseFullScreenIntent()
        } else {
            true
        }
    }

    /**
     * 헤드업 알람은 swipe하면 상태창에서 알람이 사라짐. 상태창에 알람 유지가 필요한 경우 호출
     */
    fun notifyIfFullScreen(context: Context, notificationId: Int) {
        val builder = buildNotification(
            context,
            R.drawable.noti_foreground,
            context.getString(R.string.notification_title),
            context.getString(R.string.notification_text),
            DEFAULT_CH
        )
        val notificationManager: NotificationManager =
            context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        if(notificationId != -1) {
            //tag를 통해 헤드업 알람이 setTimeoutAfter로 제거된 이후에도 따로 그룹으로 쌓일 수 있음.
            notificationManager.notify("tag", notificationId, builder.build())
        }
    }

    fun notifyService(context: Context, foregroundId: Int): Notification {
        val builder = buildNotification(
            context,
            R.drawable.noti_foreground,
            context.getString(R.string.notification_title),
            "백업 실행 중 입니다.",
            DEFAULT_CH
        )
        builder.setOngoing(true)
        val notificationManager: NotificationManager =
            context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        val notification = builder.build()

        //BackupForeground에서 startForeground할 때 넘겨준 id값과 동일한 id로 notify
        notificationManager.notify(foregroundId, notification)
        return notification
    }

    private fun buildNotification(
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
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(contentView)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        return builder
    }

    /**
     * 그룹으로 묶어서 알람 처리하는 경우 호출
     */
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

    private fun NotificationCompat.Builder.fullScreenBuilder(
        context: Context,
        title: String,
        text: String
    ) {
        val contentView = RemoteViews(context.packageName, R.layout.custom_notification)
        contentView.setTextViewText(R.id.tv_title, title)
        contentView.setTextViewText(R.id.tv_content, text)

//        setCustomHeadsUpContentView(contentView)
        setFullScreenIntent(getFullScreenIntent(context), true)
        setTimeoutAfter(TIMEOUT)
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

    private fun play(ringtoneMode: String, context: Context) {
        when(ringtoneMode) {
            RingTone.BELL.name -> {
                playRingtone(context)
            }
            RingTone.VIBE.name -> {
                playVibrate(context)
            }
            RingTone.ALL.name -> {
                playRingtone(context)
                playVibrate(context)
            }
        }
    }

    /**
     * 아래 두 메서드는 notify 시 호출
     */
    private fun playRingtone(context: Context) {
        val uriRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(context, uriRingtone)
        val audioAttributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
        ringtone.audioAttributes = audioAttributes
        ringtone.play()
    }

    private fun playVibrate(context: Context) {
        val vibrator =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API Level 31에서 VibratorManager로 변경됨
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun cancelNotify(context: Context, notificationId: Int) {
        val notificationManager: NotificationManager =
            context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    fun isNotificationEnabled(context: Context): Boolean {
        return NotificationManagerCompat
            .from(context)
            .areNotificationsEnabled()
    }

    /**
     * 알림을 직접 swipe해서 제거할 때 호출할 리시버 등록용 메서드
     */
    private fun NotificationCompat.Builder.setDismissListener(context: Context, notificationId: Int) {
        val deleteIntent = Intent(context, NotificationDismissedReceiver::class.java)
        deleteIntent.putExtra("notification_id", notificationId)
        setDeleteIntent(
            PendingIntent.getBroadcast(
                context,
                notificationId,
                deleteIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }
}
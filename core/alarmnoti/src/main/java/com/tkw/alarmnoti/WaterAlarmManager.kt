package com.tkw.alarmnoti

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.tkw.domain.IAlarmManager
import com.tkw.domain.model.Alarm
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WaterAlarmManager @Inject constructor(
    @ApplicationContext private val context: Context
): IAlarmManager {

    override fun setAlarm(alarm: Alarm) {
        with(alarm) {
            if(canScheduleExactAlarms()) {
                setAlarmManager(alarmId, startTime, interval)
                cancelWorkManager(alarmId)
            } else {
                setWorkManager(alarmId, startTime)
                cancelAlarmManager(alarmId)
            }
        }
    }

    override fun cancelAlarm(alarmId: Int) {
        cancelAlarmManager(alarmId)
        cancelWorkManager(alarmId)
    }

    override fun canScheduleExactAlarms(): Boolean {
        return if(Build.VERSION.SDK_INT >= 31) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else true
    }

    private fun setAlarmManager(
        alarmId: Int,
        startTime: Long,
        interval: Long
    ) {
        val intent = Intent(context, WaterAlarmReceiver::class.java)
        intent.putExtra("ALARM_ID", alarmId)
        intent.putExtra("ALARM_TIME", startTime)
        intent.putExtra("ALARM_INTERVAL", interval)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmClock = AlarmManager.AlarmClockInfo(
            startTime,
            pendingIntent
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAlarmClock(
            alarmClock, pendingIntent
        )
    }

    private fun setWorkManager(alarmId: Int, startTime: Long) {
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<ScheduledWorkManager>()
            .setInitialDelay(ScheduledWorkManager.getCertainTime(), TimeUnit.MILLISECONDS)
//            .setInputData(
//                Data.Builder()
//                    .putString()
//            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            ScheduledWorkManager.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            oneTimeWorkRequest
        )
    }

    private fun cancelAlarmManager(alarmId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WaterAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun cancelWorkManager(alarmId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork(ScheduledWorkManager.WORK_NAME)
    }
}
package com.tkw.alarmnoti

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.tkw.domain.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class WaterAlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var alarmRepository: AlarmRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        //todo extra로 받아서 buildNotification에 ringtone mode 넘겨줌
        if (context != null && intent != null) {
            val ringtoneMode = intent.getStringExtra("RINGTONE") ?: ""  //extra에서 꺼내기
            NotificationManager.notify(context, ringtoneMode)
            //주기 모드면 interval만큼 startTime에 더해서 실행
            //그 외에는 시간마다 각 알람 설정하고, 24시간만큼 startTime에 더해서 실행
            val alarmId = intent.getIntExtra("ALARM_ID", -1)
            val alarmTime = intent.getLongExtra("ALARM_TIME", -1)
            val alarmInterval = intent.getLongExtra("ALARM_INTERVAL", -1)
            val startTime = alarmTime + alarmInterval

            CoroutineScope(Dispatchers.Main).launch {
                alarmRepository.setAlarm(
                    alarmId,
                    startTime,
                    alarmInterval
                )
            }
        }

        Log.d("test", "alarmnoti received")

    }
}
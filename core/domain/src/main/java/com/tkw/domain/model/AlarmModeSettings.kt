package com.tkw.domain.model

import java.io.Serializable
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class AlarmModeSetting(
    val selectedDate: List<DayOfWeek> = listOf(),
    val interval: Int = DEFAULT_PERIOD_INTERVAL
) {
    companion object {
        const val DEFAULT_PERIOD_INTERVAL: Int = 1000 * 60 * 2 //한 시간으로 변경 필요. period 수정할 때 설정한 알람 간격 값임.
        const val DEFAULT_CUSTOM_INTERVAL: Int = 1000 * 60 * 5 //임시 5분 처리. 추후 24시간으로 변경
    }
}

data class Alarm(
    val alarmId: Int,   //HHmm
    val startTime: Long,
//    val interval: Long = -1,
    val weekList: List<DayOfWeek>,
    val enabled: Boolean = false
): Serializable {
    var isChecked: Boolean = false

    fun getAlarmTime(): String {
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        val instant = Instant.ofEpochMilli(startTime)
        val localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime()

        return localTime.format(formatter)
    }

    fun getIntervalByNextDayOfWeek(): Int {
        val dateFromStartTime =
            Instant
                .ofEpochMilli(startTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

        val currentDOW = DayOfWeek.from(dateFromStartTime)
        var daysDifference = runCatching {
            weekList.minOf {
                if(it.value == currentDOW.value) {
                    if(startTime > System.currentTimeMillis()) 0
                    else 7
                } else {
                    (it.value - currentDOW.value + 7) % 7
                }
            }
        }.getOrNull()

        if(daysDifference == 0 && startTime < System.currentTimeMillis()) {
            daysDifference = 7
        }
        return if(daysDifference == null) -1
        else 1000 * 60 * 60 * 24 * daysDifference
    }
}

data class AlarmList(
    val alarmList: List<Alarm> = listOf()
)
package com.tkw.setting

import android.content.Context
import androidx.lifecycle.asLiveData
import com.tkw.base.BaseViewModel
import com.tkw.common.util.DateTimeUtils
import com.tkw.domain.AlarmRepository
import com.tkw.domain.PrefDataRepository
import com.tkw.domain.SettingRepository
import com.tkw.domain.WaterRepository
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.DayOfWaterList
import com.tkw.domain.model.RingTone
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SettingViewModel
@Inject constructor(
    @ApplicationContext private val context: Context,
//    settingRepository: SettingRepository,
    waterRepository: WaterRepository,
    alarmRepository: AlarmRepository,
    prefDataRepository: PrefDataRepository
): BaseViewModel() {

    //todo settingRepo에서 구글 계정 가져오기

    private val getAllDay = waterRepository.getAllDay().mapLatest { list ->
        DayOfWaterList(list)
    }

    val totalIntake = getAllDay.flatMapLatest {
        flow {
            emit("${it.getTotalIntake()}ml")
        }
    }.asLiveData()

    val totalAchieve = getAllDay.flatMapLatest {
        flow {
            emit("${it.getTotalAchieve(getIntakeAmount.first())}일")
        }
    }.asLiveData()

    private val getIntakeAmount = prefDataRepository.fetchIntakeAmount()
    val goalOfIntake = getIntakeAmount.mapLatest {
        "${it}ml"
    }.asLiveData()

    val currentLang = prefDataRepository.fetchLanguage().mapLatest {
        when(it) {
            Locale.KOREAN.language -> getCustomString(com.tkw.ui.R.string.lang_ko)
            Locale.ENGLISH.language -> getCustomString(com.tkw.ui.R.string.lang_en)
            Locale.JAPANESE.language -> getCustomString(com.tkw.ui.R.string.lang_jp)
            Locale.CHINESE.language -> getCustomString(com.tkw.ui.R.string.lang_cn)
            else -> getCustomString(com.tkw.ui.R.string.lang_ko)
        }
    }.asLiveData()

    val unit = prefDataRepository.fetchUnit().mapLatest {
        when(it) {
            0 -> "ml, L"
            1 -> "fl.oz"
            else -> "ml, L"
        }
    }.asLiveData()

    private val alarmSetting = alarmRepository.getAlarmSetting()
    private val alarmModeSetting = alarmRepository.getAlarmModeSetting()

    val alarmMode = alarmSetting.flatMapLatest {
        flow {
            val mode = it.alarmMode
            when(mode) {
                AlarmMode.PERIOD -> emit(getCustomString(com.tkw.ui.R.string.alarm_mode_period))
                AlarmMode.CUSTOM -> emit(getCustomString(com.tkw.ui.R.string.alarm_mode_custom))
            }
        }
    }.asLiveData()

    val alarmRingtone = alarmSetting.flatMapLatest {
        flow {
            val ringtone = it.ringToneMode.getCurrentMode()
            val soundTitle = when(ringtone) {
                RingTone.DEVICE -> getCustomString(com.tkw.ui.R.string.alarm_sound_device)
                RingTone.BELL -> getCustomString(com.tkw.ui.R.string.alarm_sound_ringtone)
                RingTone.VIBE -> getCustomString(com.tkw.ui.R.string.alarm_sound_vibe)
                RingTone.ALL -> getCustomString(com.tkw.ui.R.string.alarm_sound_all)
                RingTone.IGNORE -> getCustomString(com.tkw.ui.R.string.alarm_sound_silence)
            }
            emit(soundTitle)
        }
    }.asLiveData()

    val alarmSchedule = alarmModeSetting.flatMapLatest {
        flow {
            emit(
                it.selectedDate.map {
                    it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                }.joinToString(", ")
            )
        }
    }.asLiveData()

    val alarmTime = alarmModeSetting.flatMapLatest {
        flow {
            val startTime = DateTimeUtils.getFormattedTime(it.startTime.hour, it.startTime.minute)
            val endTime = DateTimeUtils.getFormattedTime(it.endTime.hour, it.endTime.minute)
            emit("$startTime - $endTime")
        }
    }.asLiveData()

    private fun getCustomString(id: Int): String {
        return context.getString(id)
    }
}
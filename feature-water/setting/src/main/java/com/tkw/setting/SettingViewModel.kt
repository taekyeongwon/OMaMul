package com.tkw.setting

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.tkw.base.BaseViewModel
import com.tkw.base.launch
import com.tkw.common.SingleLiveEvent
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
    waterRepository: WaterRepository,
    alarmRepository: AlarmRepository,
    private val settingRepository: SettingRepository,
    private val prefDataRepository: PrefDataRepository
): BaseViewModel() {

    private val _nextEvent = SingleLiveEvent<Unit>()
    val nextEvent: LiveData<Unit> = _nextEvent

    private val getAllDay = waterRepository.getAllDay().mapLatest { list ->
        DayOfWaterList(list)
    }

    private val settings = settingRepository.getSetting()

    val totalIntake = getAllDay.flatMapLatest { dayList ->
        settings.mapLatest { setting ->
            setting.formatAmount(dayList.getTotalIntake())
        }
    }.asLiveData()

    val totalAchieve = getAllDay.flatMapLatest { dayList ->
        settings.mapLatest { setting ->
            "${dayList.getTotalAchieve(setting.intake)}"
        }
    }.asLiveData()

    val goalOfIntake = settings.mapLatest {
        it.formatAmount(it.intake)
    }.asLiveData()

    val currentLangFlow = prefDataRepository.fetchLanguage()
    val currentLang = currentLangFlow.mapLatest {
        when(it) {
            Locale.KOREAN.language -> com.tkw.ui.R.string.lang_ko
            Locale.ENGLISH.language -> com.tkw.ui.R.string.lang_en
            Locale.JAPANESE.language -> com.tkw.ui.R.string.lang_jp
            Locale.CHINESE.language -> com.tkw.ui.R.string.lang_cn
            else -> com.tkw.ui.R.string.lang_ko
        }
    }.asLiveData()

    val unitFlow = settings.mapLatest {
        it.unit
    }
    val unit = settings.mapLatest {
        when(it.unit) {
            0 -> "ml, L"
            1 -> "fl oz"
            2 -> "컵"
            else -> "ml, L"
        }
    }.asLiveData()

    val lastSync = prefDataRepository.fetchLastSync().asLiveData()

    private val alarmSetting = alarmRepository.getAlarmSetting()
    private val alarmModeSetting = alarmRepository.getAlarmModeSetting()

    val alarmMode = alarmSetting.flatMapLatest {
        flow {
            val mode = it.alarmMode
            when(mode) {
                AlarmMode.PERIOD -> emit(com.tkw.ui.R.string.alarm_mode_period)
                AlarmMode.CUSTOM -> emit(com.tkw.ui.R.string.alarm_mode_custom)
            }
        }
    }.asLiveData()

    val alarmRingtone = alarmSetting.flatMapLatest {
        flow {
            val ringtone = it.ringToneMode.getCurrentMode()
            val soundTitle = when(ringtone) {
                RingTone.DEVICE -> com.tkw.ui.R.string.alarm_sound_device
                RingTone.BELL -> com.tkw.ui.R.string.alarm_sound_ringtone
                RingTone.VIBE -> com.tkw.ui.R.string.alarm_sound_vibe
                RingTone.ALL -> com.tkw.ui.R.string.alarm_sound_all
                RingTone.IGNORE -> com.tkw.ui.R.string.alarm_sound_silence
            }
            emit(soundTitle)
        }
    }.asLiveData()

    val alarmSchedule = alarmModeSetting.flatMapLatest {
        flow {
            if(it.selectedDate.isEmpty()) {
                emit("-")
            } else {
                emit(
                    it.selectedDate.joinToString(", ") {
                        it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    }
                )
            }
        }
    }.asLiveData()

    val alarmTime = alarmModeSetting.flatMapLatest {
        flow {
            emit(it.run {
                getTimeRange(
                    DateTimeUtils.Time.getFormat(startTime),
                    DateTimeUtils.Time.getFormat(endTime))
            })
        }
    }.asLiveData()

    fun saveUnit(unit: Int) {
        launch {
            settingRepository.saveUnit(unit)
            _nextEvent.call()
        }
    }

    fun saveLanguage(lang: String) {
        launch {
            prefDataRepository.saveLanguage(lang)
            _nextEvent.call()
        }
    }
}
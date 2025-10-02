package com.tkw.setting

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.tkw.base.BaseViewModel
import com.tkw.base.launch
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
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

    private val _nextEvent = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
    val nextEvent: SharedFlow<Unit> = _nextEvent.asSharedFlow()

    private val getAllDay = waterRepository.getAllDay().mapLatest { list ->
        DayOfWaterList(list)
    }

    private val settings = settingRepository.getSetting()

    val totalIntakeFlow: StateFlow<String> = getAllDay.flatMapLatest { dayList ->
        settings.mapLatest { setting ->
            setting.formatAmount(dayList.getTotalIntake())
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    val totalAchieveFlow: StateFlow<String> = getAllDay.flatMapLatest { dayList ->
        settings.mapLatest { setting ->
            "${dayList.getTotalAchieve(setting.intake)}"
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    val goalOfIntakeFlow: StateFlow<String> = settings.mapLatest {
        it.formatAmount(it.intake)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    val currentLangFlow = prefDataRepository.fetchLanguage()
    val currentLangResFlow: StateFlow<Int> = currentLangFlow.mapLatest {
        when(it) {
            Locale.KOREAN.language -> com.tkw.ui.R.string.lang_ko
            Locale.ENGLISH.language -> com.tkw.ui.R.string.lang_en
            Locale.JAPANESE.language -> com.tkw.ui.R.string.lang_jp
            Locale.CHINESE.language -> com.tkw.ui.R.string.lang_cn
            else -> com.tkw.ui.R.string.lang_ko
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        com.tkw.ui.R.string.lang_ko
    )

    val unitFlow = settings.mapLatest {
        it.unit
    }
    val unitTextFlow: StateFlow<String> = settings.mapLatest {
        when(it.unit) {
            0 -> "ml, L"
            1 -> "fl oz"
            2 -> "컵"
            else -> "ml, L"
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "ml, L"
    )

    val lastSyncFlow: StateFlow<Long> = prefDataRepository.fetchLastSync().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0L
    )

    private val alarmSetting = alarmRepository.getAlarmSetting()
    private val alarmModeSetting = alarmRepository.getAlarmModeSetting()

    val alarmModeFlow: StateFlow<Int> = alarmSetting.flatMapLatest {
        flow {
            val mode = it.alarmMode
            when(mode) {
                AlarmMode.PERIOD -> emit(com.tkw.ui.R.string.alarm_mode_period)
                AlarmMode.CUSTOM -> emit(com.tkw.ui.R.string.alarm_mode_custom)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        com.tkw.ui.R.string.alarm_mode_period
    )

    val alarmRingtoneFlow: StateFlow<Int> = alarmSetting.flatMapLatest {
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
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        com.tkw.ui.R.string.alarm_sound_device
    )

    val alarmScheduleFlow: StateFlow<String> = alarmModeSetting.flatMapLatest {
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
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "-"
    )

    val alarmTimeFlow: StateFlow<String> = alarmModeSetting.flatMapLatest {
        flow {
            emit(it.run {
                getTimeRange(
                    DateTimeUtils.Time.getFormat(startTime),
                    DateTimeUtils.Time.getFormat(endTime))
            })
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "-"
    )

    fun saveUnit(unit: Int) {
        launch {
            settingRepository.saveUnit(unit)
            _nextEvent.tryEmit(Unit)
        }
    }

    fun saveLanguage(lang: String) {
        launch {
            prefDataRepository.saveLanguage(lang)
            _nextEvent.tryEmit(Unit)
        }
    }
}
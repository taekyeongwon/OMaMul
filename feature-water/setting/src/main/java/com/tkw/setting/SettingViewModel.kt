package com.tkw.setting

import android.content.Context
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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
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

    // SharedFlow로 변경된 이벤트
    private val _nextEvent = MutableSharedFlow<Unit>()
    val nextEvent: SharedFlow<Unit> = _nextEvent.asSharedFlow()

    // Dialog 상태 관리
    private val _showLanguageDialog = MutableStateFlow(false)
    val showLanguageDialog = _showLanguageDialog.asStateFlow()

    private val _showUnitDialog = MutableStateFlow(false)
    val showUnitDialog = _showUnitDialog.asStateFlow()

    fun showLanguageDialog() {
        _showLanguageDialog.value = true
    }

    fun hideLanguageDialog() {
        _showLanguageDialog.value = false
    }

    fun showUnitDialog() {
        _showUnitDialog.value = true
    }

    fun hideUnitDialog() {
        _showUnitDialog.value = false
    }

    private val getAllDay = waterRepository.getAllDay().mapLatest { list ->
        DayOfWaterList(list)
    }


    // Compose용 StateFlow
    val totalIntakeStateFlow: StateFlow<String> = getAllDay.flatMapLatest {
        flow {
            emit("${it.getTotalIntake()}ml")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "0ml"
    )


    // Compose용 StateFlow
    val totalAchieveStateFlow: StateFlow<String> = getAllDay.flatMapLatest {
        flow {
            emit("${it.getTotalAchieve(settings.first().intake)}")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "0"
    )

    private val settings = settingRepository.getSetting()

    // Compose용 StateFlow
    val goalOfIntakeStateFlow: StateFlow<String> = settings.mapLatest {
        "${it.intake}ml"
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "0ml"
    )

    val currentLangFlow = prefDataRepository.fetchLanguage()

    // Compose용 StateFlow
    val currentLangStateFlow: StateFlow<Int> = currentLangFlow.mapLatest {
        when(it) {
            Locale.KOREAN.language -> com.tkw.ui.R.string.lang_ko
            Locale.ENGLISH.language -> com.tkw.ui.R.string.lang_en
            Locale.JAPANESE.language -> com.tkw.ui.R.string.lang_jp
            Locale.CHINESE.language -> com.tkw.ui.R.string.lang_cn
            else -> com.tkw.ui.R.string.lang_ko
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = com.tkw.ui.R.string.lang_ko
    )

    val unitFlow = settings.mapLatest {
        it.unit
    }

    // Compose용 StateFlow
    val unitStateFlow: StateFlow<String> = settings.mapLatest {
        when(it.unit) {
            0 -> "ml, L"
            1 -> "fl.oz"
            else -> "ml, L"
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "ml, L"
    )


    // Compose용 StateFlow
    val lastSyncStateFlow: StateFlow<Long> = prefDataRepository.fetchLastSync()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = -1L
        )

    private val alarmSetting = alarmRepository.getAlarmSetting()
    private val alarmModeSetting = alarmRepository.getAlarmModeSetting()


    // Compose용 StateFlow
    val alarmModeStateFlow: StateFlow<Int> = alarmSetting.flatMapLatest {
        flow {
            val mode = it.alarmMode
            when(mode) {
                AlarmMode.PERIOD -> emit(com.tkw.ui.R.string.alarm_mode_period)
                AlarmMode.CUSTOM -> emit(com.tkw.ui.R.string.alarm_mode_custom)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = com.tkw.ui.R.string.alarm_mode_period
    )


    // Compose용 StateFlow
    val alarmRingtoneStateFlow: StateFlow<Int> = alarmSetting.flatMapLatest {
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
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = com.tkw.ui.R.string.alarm_sound_device
    )


    // Compose용 StateFlow
    val alarmScheduleStateFlow: StateFlow<String> = alarmModeSetting.flatMapLatest {
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
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "-"
    )


    // Compose용 StateFlow
    val alarmTimeStateFlow: StateFlow<String> = alarmModeSetting.flatMapLatest {
        flow {
            emit(it.run {
                getTimeRange(
                    DateTimeUtils.Time.getFormat(startTime),
                    DateTimeUtils.Time.getFormat(endTime))
            })
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "-"
    )

    fun saveUnit(unit: Int) {
        launch {
            settingRepository.saveUnit(unit)
            _nextEvent.emit(Unit)
        }
    }

    fun saveLanguage(lang: String) {
        launch {
            prefDataRepository.saveLanguage(lang)
            _nextEvent.emit(Unit)
        }
    }
}
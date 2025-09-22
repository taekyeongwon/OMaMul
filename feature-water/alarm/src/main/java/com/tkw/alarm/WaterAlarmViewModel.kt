package com.tkw.alarm

import com.tkw.base.BaseViewModel
import com.tkw.base.launch
import com.tkw.common.util.DateTimeUtils
import com.tkw.domain.AlarmRepository
import com.tkw.domain.PrefDataRepository
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmEtcSettings
import com.tkw.domain.model.AlarmList
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmModeSetting
import com.tkw.domain.model.AlarmSettings
import com.tkw.domain.model.RingToneMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

@HiltViewModel
class WaterAlarmViewModel @Inject constructor(
    private val prefDataRepository: PrefDataRepository,
    private val alarmRepository: AlarmRepository
): BaseViewModel() {
    private val _nextEvent = MutableSharedFlow<Unit>()
    val nextEvent: SharedFlow<Unit> = _nextEvent.asSharedFlow()

    //알람 권한 허용 여부
    private val isAlarmEnabled = prefDataRepository.fetchAlarmEnableFlag()
    suspend fun getNotificationEnabled() = isAlarmEnabled.first()
    suspend fun setAlarmEnabled(flag: Boolean) {
        prefDataRepository.saveAlarmEnableFlag(flag)
    }

    //설정에서 알람 허용 여부
    private val isNotificationEnabled = MutableStateFlow(false)
    fun setNotificationEnabled(flag: Boolean) {
        isNotificationEnabled.value = flag
    }

    //알람 및 설정에서 알람 허용했는지 여부
    @OptIn(ExperimentalCoroutinesApi::class)
    fun isNotificationAlarmEnabled() = isAlarmEnabled
        .combine(isNotificationEnabled) { isAlarm, isNoti ->
            isAlarm && isNoti
        }

    private val alarmSettingsFlow: Flow<AlarmSettings> = alarmRepository.getAlarmSetting()


    // Compose용 StateFlow 버전
    val alarmSettingsStateFlow: StateFlow<AlarmSettings?> =
        alarmSettingsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val isAlarmEnabledStateFlow: StateFlow<Boolean> =
        isAlarmEnabled.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val isNotificationEnabledStateFlow: StateFlow<Boolean> =
        isNotificationEnabled.asStateFlow()

    val alarmModeStateFlow: StateFlow<AlarmMode?> =
        alarmSettingsFlow.mapLatest { it.alarmMode }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Compose용 추가 StateFlow들
    val periodModeSettingsStateFlow: StateFlow<AlarmModeSetting> =
        alarmRepository.getAlarmModeSetting().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AlarmModeSetting()
        )

    val customAlarmListStateFlow: StateFlow<AlarmList?> =
        alarmRepository.getAlarmList(AlarmMode.CUSTOM).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )



    @OptIn(ExperimentalCoroutinesApi::class)
    val alarmRingToneStateFlow: StateFlow<RingToneMode?> =
        alarmSettingsFlow.mapLatest {
            it.ringToneMode
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val alarmEtcSettingStateFlow: StateFlow<AlarmEtcSettings?> =
        alarmSettingsFlow.mapLatest {
            it.etcSetting
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isStopWhenReachedGoal: Flow<Boolean> =
        alarmSettingsFlow.flatMapLatest {
            flow {
                emit(it.etcSetting.stopReachedGoal)
            }
        }

    val isReachedGoalStateFlow: StateFlow<Boolean> = prefDataRepository.fetchReachedGoal()
        .combine(isStopWhenReachedGoal) { isReachedGoal, stopReachedFlag ->
            isReachedGoal && stopReachedFlag
        }.distinctUntilChanged().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    //period 모드 화면 변경사항 체크용
    private val _tmpPeriodModeStateFlow = MutableStateFlow<AlarmModeSetting?>(null)
    val tmpPeriodModeStateFlow: StateFlow<AlarmModeSetting?> = _tmpPeriodModeStateFlow.asStateFlow()

    //알람 변경에 따라 remainTime 재요청
    @OptIn(ExperimentalCoroutinesApi::class)
    val timeTickerLiveData = isNotificationAlarmEnabled()
        .flatMapLatest { alarmRepository.getRemainAlarmTime() }


    private val _remainTimeStateFlow = MutableStateFlow("")
    val remainTimeStateFlow: StateFlow<String> = _remainTimeStateFlow.asStateFlow()

    fun wakeAllAlarm() {
        launch {
            alarmRepository.wakeAllAlarm()
        }
    }

    suspend fun delayAllAlarm(isDelayed: Boolean, isNotificationEnabled: Boolean = true) {
        launch {
            alarmRepository.delayAllAlarm(isDelayed, isNotificationEnabled)
        }
    }

    fun sleepAllAlarm() {
        launch {
            alarmRepository.sleepAllAlarm(alarmSettingsFlow.first().alarmMode)
        }
    }

    suspend fun clearAlarm(mode: AlarmMode) {
        launch {
            alarmRepository.deleteAllAlarm(mode)
        }
    }

    fun updateRingToneMode(mode: RingToneMode) {
        launch {
            val currentSetting = alarmSettingsFlow.first()
            val newSetting = AlarmSettings(
                mode,
                currentSetting.alarmMode,
                currentSetting.etcSetting
            )
            alarmRepository.updateAlarmSetting(newSetting)
        }
    }

    fun updateAlarmMode(mode: AlarmMode) {
        launch {
            val currentSetting = alarmSettingsFlow.first()
            val newSetting = AlarmSettings(
                currentSetting.ringToneMode,
                mode,
                currentSetting.etcSetting
            )
            alarmRepository.sleepAllAlarm(currentSetting.alarmMode) //모드 변경 시 이전 모드의 알람 전부 알람매니저에서 해제
            alarmRepository.updateAlarmSetting(newSetting)
        }
    }

    fun updateEtcSetting(etcSettings: AlarmEtcSettings) {
        launch {
            val currentSetting = alarmSettingsFlow.first()
            val newSetting = AlarmSettings(
                currentSetting.ringToneMode,
                currentSetting.alarmMode,
                etcSettings
            )
            alarmRepository.updateAlarmSetting(newSetting)
        }
    }

    // Compose용 함수들 (기존 함수와 구분하기 위해 다른 이름 사용)
    fun setAlarmEnabledCompose(enabled: Boolean) {
        launch {
            prefDataRepository.saveAlarmEnableFlag(enabled)
        }
    }

    fun delayAllAlarmCompose(isDelayed: Boolean, isNotificationEnabled: Boolean = true) {
        launch {
            alarmRepository.delayAllAlarm(isDelayed, isNotificationEnabled)
        }
    }

    fun updateAlarmModeSetting(setting: AlarmModeSetting) {
        launch {
            alarmRepository.updateAlarmModeSetting(setting)
        }
    }

    fun setPeriodAlarm(period: AlarmModeSetting) {
        launch {
            if(period.selectedDate.isNotEmpty()) {
                var start = period.startTime
                val end = period.endTime
                val interval = period.interval * 1000
                val alarmList = ArrayList<Alarm>()

                while(start < end) {
                    alarmList.add(
                        Alarm(
                            DateTimeUtils.DateTime.getFormatTrim(start),
                            start,
                            period.selectedDate,
                            true
                        )
                    )
                    start += interval
                }
                setAlarmList(alarmList)
            } else {
                setAlarmList(listOf())
            }
        }
    }

    fun setCustomAlarm(alarm: Alarm) {
        launch {
            alarmRepository.setAlarm(alarm, isNotificationAlarmEnabled().first(), isReachedGoalStateFlow.value)
        }
    }

    private suspend fun setAlarmList(list: List<Alarm>) {
        alarmRepository.setAlarmList(list, isNotificationAlarmEnabled().first(), isReachedGoalStateFlow.value)
    }

    fun deleteAlarm(list: List<Alarm>) {
        launch {
            alarmRepository.deleteAlarm(list, AlarmMode.CUSTOM)
            _nextEvent.emit(Unit)
        }
    }

    fun updateList(list: List<Alarm>) {
        launch {
            alarmRepository.updateList(list, AlarmMode.CUSTOM)
        }
    }

    fun setTmpPeriodMode(period: AlarmModeSetting) {
        _tmpPeriodModeStateFlow.value = period
    }

    fun saveReachedGoal(isReached: Boolean) {
        launch {
            prefDataRepository.saveReachedGoal(isReached)
        }
    }

    fun setRemainTimeContent(content: String) {
        _remainTimeStateFlow.value = content
    }

    companion object {
        const val TIME_UNIT_SECONDS: Long = 1000
    }
}
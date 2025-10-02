package com.tkw.alarm

import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WaterAlarmViewModel @Inject constructor(
    private val prefDataRepository: PrefDataRepository,
    private val alarmRepository: AlarmRepository
): BaseViewModel() {
    private val _nextEvent = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
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

    val periodModeSettingsFlow: StateFlow<AlarmModeSetting> =
        alarmRepository.getAlarmModeSetting().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AlarmModeSetting()
        )

    val alarmSettingsStateFlow: StateFlow<AlarmSettings> =
        alarmSettingsFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AlarmSettings()
        )

    val customAlarmListFlow: StateFlow<AlarmList> =
        alarmRepository.getAlarmList(AlarmMode.CUSTOM).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AlarmList(listOf())
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val alarmRingToneFlow: StateFlow<RingToneMode> =
        alarmSettingsFlow.mapLatest {
            it.ringToneMode
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            RingToneMode()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val alarmModeFlow: StateFlow<AlarmMode> =
        alarmSettingsFlow.mapLatest {
            it.alarmMode
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AlarmMode.PERIOD
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val alarmEtcSettingFlow: StateFlow<AlarmEtcSettings> =
        alarmSettingsFlow.mapLatest {
            it.etcSetting
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AlarmEtcSettings()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isStopWhenReachedGoal: Flow<Boolean> =
        alarmSettingsFlow.flatMapLatest {
            flow {
                emit(it.etcSetting.stopReachedGoal)
            }
        }

    val isReachedGoalFlow: StateFlow<Boolean> = prefDataRepository.fetchReachedGoal()
        .combine(isStopWhenReachedGoal) { isReachedGoal, stopReachedFlag ->
            isReachedGoal && stopReachedFlag
        }.distinctUntilChanged().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    //period 모드 화면 변경사항 체크용
    private val _tmpPeriodModeFlow = MutableStateFlow(AlarmModeSetting())
    val tmpPeriodModeFlow: StateFlow<AlarmModeSetting> = _tmpPeriodModeFlow

    //알람 변경에 따라 remainTime 재요청
    @OptIn(ExperimentalCoroutinesApi::class)
    val timeTickerFlow: Flow<Long> = isNotificationAlarmEnabled()
        .flatMapLatest { alarmRepository.getRemainAlarmTime() }

    private val _remainTimeFlow = MutableStateFlow("")
    val remainTimeFlow: StateFlow<String> = _remainTimeFlow

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

    suspend fun updateEtcSetting(etcSettings: AlarmEtcSettings) {
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

    suspend fun updateAlarmModeSetting(setting: AlarmModeSetting) {
        alarmRepository.updateAlarmModeSetting(setting)
    }

    suspend fun setPeriodAlarm(period: AlarmModeSetting) {
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

    suspend fun setCustomAlarm(alarm: Alarm) {
        alarmRepository.setAlarm(alarm, isNotificationAlarmEnabled().first(), isReachedGoalFlow.value)
    }

    private suspend fun setAlarmList(list: List<Alarm>) {
        alarmRepository.setAlarmList(list, isNotificationAlarmEnabled().first(), isReachedGoalFlow.value)
    }

    fun deleteAlarm(list: List<Alarm>) {
        launch {
            alarmRepository.deleteAlarm(list, AlarmMode.CUSTOM)
            _nextEvent.tryEmit(Unit)
        }
    }

    fun updateList(list: List<Alarm>) {
        launch {
            alarmRepository.updateList(list, AlarmMode.CUSTOM)
        }
    }

    fun setTmpPeriodMode(period: AlarmModeSetting) {
        _tmpPeriodModeFlow.value = period
    }

    fun saveReachedGoal(isReached: Boolean) {
        launch {
            prefDataRepository.saveReachedGoal(isReached)
        }
    }

    fun setRemainTimeContent(content: String) {
        _remainTimeFlow.value = content
    }

    companion object {
        const val TIME_UNIT_SECONDS: Long = 1000
    }
}
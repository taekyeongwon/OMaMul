package com.tkw.alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.tkw.base.BaseViewModel
import com.tkw.base.launch
import com.tkw.common.SingleLiveEvent
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _nextEvent = SingleLiveEvent<Unit>()
    val nextEvent: LiveData<Unit> = _nextEvent

    //알람 권한 허용 여부
    private val isAlarmEnabledFlow = prefDataRepository.fetchAlarmEnableFlag()
    suspend fun getNotificationEnabled() = isAlarmEnabledFlow.first()
    fun setAlarmEnabled(flag: Boolean) {
        launch {
            prefDataRepository.saveAlarmEnableFlag(flag)
        }
    }

    //설정에서 알람 허용 여부
    private val isNotificationEnabled = MutableStateFlow(false)
    fun setNotificationEnabled(flag: Boolean) {
        isNotificationEnabled.value = flag
    }

    //알람 및 설정에서 알람 허용했는지 여부
    @OptIn(ExperimentalCoroutinesApi::class)
    fun isNotificationAlarmEnabled() = isAlarmEnabledFlow
        .combine(isNotificationEnabled) { isAlarm, isNoti ->
            isAlarm && isNoti
        }

    private val alarmSettingsFlow: Flow<AlarmSettings> = alarmRepository.getAlarmSetting()

    val periodModeSettingsLiveData: LiveData<AlarmModeSetting> =
        alarmRepository.getAlarmModeSetting().asLiveData()

    val alarmSettingsLiveData: LiveData<AlarmSettings> =
        alarmSettingsFlow.asLiveData()
    
    // StateFlow versions for Compose
    val alarmSettings: StateFlow<AlarmSettings?> = 
        alarmSettingsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    val isAlarmEnabled: StateFlow<Boolean> = 
        isAlarmEnabledFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
        
    fun loadAlarmSettings() {
        // StateFlow는 자동으로 수집되므로 별도 로딩 로직 불필요
    }

    val customAlarmList: LiveData<AlarmList> =
        alarmRepository.getAlarmList(AlarmMode.CUSTOM).asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    val alarmRingTone: LiveData<RingToneMode> =
        alarmSettingsFlow.mapLatest {
            it.ringToneMode
        }.asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    val alarmMode: LiveData<AlarmMode> =
        alarmSettingsFlow.mapLatest {
            it.alarmMode
        }.asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    val alarmEtcSetting: LiveData<AlarmEtcSettings> =
        alarmSettingsFlow.mapLatest {
            it.etcSetting
        }.asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isStopWhenReachedGoal: Flow<Boolean> =
        alarmSettingsFlow.flatMapLatest {
            flow {
                emit(it.etcSetting.stopReachedGoal)
            }
        }

    val isReachedGoal: LiveData<Boolean> = prefDataRepository.fetchReachedGoal()
        .combine(isStopWhenReachedGoal) { isReachedGoal, stopReachedFlag ->
            isReachedGoal && stopReachedFlag
        }.distinctUntilChanged().asLiveData()

    //period 모드 화면 변경사항 체크용
    private val _tmpPeriodMode: MutableLiveData<AlarmModeSetting> = MutableLiveData()
    val tmpPeriodMode: LiveData<AlarmModeSetting> = _tmpPeriodMode

    //알람 변경에 따라 remainTime 재요청
    @OptIn(ExperimentalCoroutinesApi::class)
    val timeTickerLiveData = isNotificationAlarmEnabled()
        .flatMapLatest { alarmRepository.getRemainAlarmTime() }

    private val _remainTimeLiveData = MutableLiveData<String>()
    val remainTimeLiveData: LiveData<String> = _remainTimeLiveData

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
        alarmRepository.setAlarm(alarm, isNotificationAlarmEnabled().first(), isReachedGoal.value ?: false)
    }

    private suspend fun setAlarmList(list: List<Alarm>) {
        alarmRepository.setAlarmList(list, isNotificationAlarmEnabled().first(), isReachedGoal.value ?: false)
    }

    fun deleteAlarm(list: List<Alarm>) {
        launch {
            alarmRepository.deleteAlarm(list, AlarmMode.CUSTOM)
            _nextEvent.call()
        }
    }

    fun updateList(list: List<Alarm>) {
        launch {
            alarmRepository.updateList(list, AlarmMode.CUSTOM)
        }
    }

    fun setTmpPeriodMode(period: AlarmModeSetting) {
        _tmpPeriodMode.value = period
    }

    fun saveReachedGoal(isReached: Boolean) {
        launch {
            prefDataRepository.saveReachedGoal(isReached)
        }
    }

    fun setRemainTimeContent(content: String) {
        launch {
            _remainTimeLiveData.value = content
        }
    }

    companion object {
        const val TIME_UNIT_SECONDS: Long = 1000
    }
}
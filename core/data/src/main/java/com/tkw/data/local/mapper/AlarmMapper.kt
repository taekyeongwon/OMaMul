package com.tkw.data.local.mapper

import com.tkw.database.model.AlarmEntity
import com.tkw.database.model.AlarmEtcSettingsEntity
import com.tkw.database.model.AlarmListEntity
import com.tkw.database.model.AlarmModeSettingEntity
import com.tkw.database.model.AlarmModeEntity
import com.tkw.database.model.AlarmSettingsEntity
import com.tkw.database.model.RingToneModeEntity
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmEtcSettings
import com.tkw.domain.model.AlarmList
import com.tkw.domain.model.AlarmModeSetting
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmSettings
import com.tkw.domain.model.RingToneMode
import io.realm.kotlin.ext.toRealmList
import java.time.DayOfWeek

object AlarmMapper {

    fun alarmSettingToEntity(
        alarmSettings: AlarmSettings
    ): AlarmSettingsEntity {
        return AlarmSettingsEntity().apply {
            this.ringToneMode = ringToneToEntity(alarmSettings.ringToneMode)
            this.alarmModeEnum = AlarmModeEntity.valueOf(alarmSettings.alarmMode.name)
            this.etcSetting = alarmEtcToEntity(alarmSettings.etcSetting)
        }
    }

    fun alarmSettingToModel(
        alarmSettingsEntity: AlarmSettingsEntity
    ): AlarmSettings {
        return AlarmSettings(
            ringToneToModel(alarmSettingsEntity.ringToneMode ?: RingToneModeEntity()),
            AlarmMode.valueOf(alarmSettingsEntity.alarmModeEnum.name),
            alarmEtcToModel(alarmSettingsEntity.etcSetting ?: AlarmEtcSettingsEntity())
        )
    }

    fun alarmModeToEntity(alarmMode: AlarmModeSetting): AlarmModeSettingEntity {
        return AlarmModeSettingEntity().apply {
            this.startTime = alarmMode.startTime
            this.endTime = alarmMode.endTime
            this.selectedDate = alarmMode.selectedDate.map { it.value }.toRealmList()
            this.interval = alarmMode.interval
        }
    }

    fun alarmModeToModel(alarmModeEntity: AlarmModeSettingEntity): AlarmModeSetting {
        return AlarmModeSetting(
            alarmModeEntity.startTime,
            alarmModeEntity.endTime,
            alarmModeEntity.selectedDate.map { DayOfWeek.of(it) },
            alarmModeEntity.interval
        )
    }

    fun alarmToEntity(alarm: Alarm): AlarmEntity {
        return AlarmEntity().apply {
            this.alarmId = alarm.alarmId
            this.startTime = alarm.startTime
            this.selectedDates = alarm.weekList.map { it.value }.toRealmList()
            this.enabled = alarm.enabled
        }
    }

    fun alarmToModel(alarm: AlarmEntity): Alarm {
        return Alarm(
            alarm.alarmId,
            alarm.startTime,
            alarm.selectedDates.map { DayOfWeek.of(it) },
            alarm.enabled
        )
    }

    fun alarmModeToEntity(mode: AlarmMode): AlarmModeEntity {
        return AlarmModeEntity.valueOf(mode.name)
    }

    fun alarmListToModel(alarmListEntity: AlarmListEntity): AlarmList {
        val modelList = alarmListEntity.alarmList.map(::alarmToModel)
        return AlarmList(alarmList = modelList)
    }

    fun alarmListToEntity(alarmList: List<Alarm>): List<AlarmEntity> {
        return alarmList.map {
            alarmToEntity(it)
        }
    }

    private fun ringToneToEntity(ringToneMode: RingToneMode): RingToneModeEntity {
        return RingToneModeEntity().apply {
            this.isBell = ringToneMode.isBell
            this.isVibe = ringToneMode.isVibe
            this.isDevice = ringToneMode.isDevice
            this.isSilence = ringToneMode.isSilence
        }
    }

    private fun ringToneToModel(ringToneEntity: RingToneModeEntity): RingToneMode {
        return RingToneMode(
            isBell = ringToneEntity.isBell,
            isVibe = ringToneEntity.isVibe,
            isDevice = ringToneEntity.isDevice,
            isSilence = ringToneEntity.isSilence
        )
    }

    private fun alarmEtcToEntity(alarmEtcSettings: AlarmEtcSettings): AlarmEtcSettingsEntity {
        return AlarmEtcSettingsEntity().apply {
            this.stopReachedGoal = alarmEtcSettings.stopReachedGoal
            this.delayTomorrow = alarmEtcSettings.delayTomorrow
        }
    }

    private fun alarmEtcToModel(alarmEtcSettingsEntity: AlarmEtcSettingsEntity): AlarmEtcSettings {
        return AlarmEtcSettings(
            alarmEtcSettingsEntity.stopReachedGoal,
            alarmEtcSettingsEntity.delayTomorrow
        )
    }
}
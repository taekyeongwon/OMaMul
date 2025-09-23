package com.tkw.data.local.mapper

import com.tkw.database.model.SettingEntity
import com.tkw.domain.model.Settings

object SettingMapper {
    fun settingToEntity(setting: Settings): SettingEntity {
        return SettingEntity().apply {
            this.intake = setting.intake
            this.unit = setting.unit
            this.unitString = setting.unitString
        }
    }

    fun settingToModel(entity: SettingEntity): Settings {
        return Settings(
            intake = entity.intake,
            unit = entity.unit,
            unitString = entity.unitString.takeIf { it.isNotEmpty() } ?: "ml"
        )
    }
}
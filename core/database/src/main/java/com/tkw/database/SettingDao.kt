package com.tkw.database

import com.tkw.database.model.SettingEntity
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

interface SettingDao: RealmDao {
    suspend fun createSetting()

    suspend fun saveIntake(amount: Int)

    suspend fun saveUnit(unit: Int)

    suspend fun saveUnitString(unit: String)

    fun getSetting(): Flow<ResultsChange<SettingEntity>>
}
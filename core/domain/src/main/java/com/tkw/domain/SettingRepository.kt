package com.tkw.domain

import com.tkw.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import java.io.File

interface SettingRepository {
    suspend fun merge(sourceFile: File, destFile: File)

    suspend fun saveIntake(amount: Int)

    suspend fun saveUnit(unit: Int)

    suspend fun saveUnitString(unit: String)

    fun getSetting(): Flow<Settings>
}
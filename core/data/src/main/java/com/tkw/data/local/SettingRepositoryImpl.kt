package com.tkw.data.local

import com.tkw.data.local.mapper.SettingMapper
import com.tkw.database.FileMerger
import com.tkw.database.SettingDao
import com.tkw.domain.SettingRepository
import com.tkw.domain.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import java.io.File
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val merger: FileMerger,
    private val settingDao: SettingDao
): SettingRepository {
    override suspend fun merge(sourceFile: File, destFile: File) {
        merger.onMerge(sourceFile, destFile)
    }

    override suspend fun saveIntake(amount: Int) {
        settingDao.saveIntake(amount)
    }

    override suspend fun saveUnit(unit: Int) {
        settingDao.saveUnit(unit)
    }

    override suspend fun saveUnitString(unit: String) {
        settingDao.saveUnitString(unit)
    }

    override fun getSetting(): Flow<Settings> {
        val settingFlow = settingDao.getSetting()
        return flow {
            settingFlow.collect {
                val setting = it.list.firstOrNull()
                if(setting == null) {
                    createSetting()
                } else {
                    this.emit(SettingMapper.settingToModel(setting))
                }
            }
        }
    }

    private suspend fun createSetting() {
        settingDao.createSetting()
    }
}
package com.tkw.domain

import kotlinx.coroutines.flow.Flow

interface PrefDataRepository {
    suspend fun saveLanguage(lang: String)
    fun fetchLanguage(): Flow<String>

    suspend fun saveReachedGoal(isReached: Boolean)
    fun fetchReachedGoal(): Flow<Boolean>

    suspend fun saveInitialFlag(flag: Boolean)
    fun fetchInitialFlag(): Flow<Boolean>

    suspend fun saveAlarmEnableFlag(flag: Boolean)
    fun fetchAlarmEnableFlag(): Flow<Boolean>

    suspend fun saveLastSync(time: Long)
    fun fetchLastSync(): Flow<Long>

    suspend fun saveCurrentSelectedCupId(cupId: String?)
    fun fetchCurrentSelectedCupId(): Flow<String?>
}
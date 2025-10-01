package com.tkw.database

import com.tkw.database.model.CupEntity
import com.tkw.database.model.CupListEntity
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

interface CupDao: RealmDao {
    fun getCup(id: String): CupEntity?

    fun getCupList(): CupListEntity?

    fun getCupListFlow(): Flow<ResultsChange<CupListEntity>>

    suspend fun createList()

    suspend fun insertCup(obj: CupEntity)

    suspend fun updateCup(target: CupEntity)

    suspend fun updateAll(list: List<CupEntity>)

    suspend fun deleteCup(cupId: String)
}
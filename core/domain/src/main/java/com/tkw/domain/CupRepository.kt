package com.tkw.domain

import com.tkw.domain.model.Cup
import com.tkw.domain.model.CupList
import com.tkw.domain.model.UnitType
import kotlinx.coroutines.flow.Flow

interface CupRepository {
    fun getCupById(id: String): Flow<Cup?>

    fun getCupList(): Flow<CupList>

    suspend fun createList()

    suspend fun insertCup(cupName: String, cupAmount: Int, cupUnit: UnitType = UnitType.ML)

    suspend fun updateCup(cupId: String, cupName: String, cupAmount: Int, cupUnit: UnitType = UnitType.ML)

    suspend fun updateAll(list: List<Cup>)

    suspend fun deleteCup(cupId: String)

    // 현재 선택된 컵 관리
    fun getCurrentSelectedCupId(): Flow<String?>

    suspend fun setCurrentSelectedCupId(cupId: String?)
}
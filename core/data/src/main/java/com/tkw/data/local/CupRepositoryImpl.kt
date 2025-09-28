package com.tkw.data.local

import com.tkw.data.local.mapper.CupMapper
import com.tkw.database.CupDao
import com.tkw.database.model.CupEntity
import com.tkw.domain.CupRepository
import com.tkw.domain.PrefDataRepository
import com.tkw.domain.model.Cup
import com.tkw.domain.model.CupList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CupRepositoryImpl @Inject constructor(
    private val cupDao: CupDao,
    private val prefDataRepository: PrefDataRepository
): CupRepository {
    override fun getCupById(id: String): Flow<Cup?> = flow {
        val cup = cupDao.getCup(id)?.let {
            CupMapper.cupToModel(it)
        }
        emit(cup)
    }

    override fun getCupList(): Flow<CupList> {
        val cupFlow = cupDao.getCupListFlow()
        return flow {
            cupFlow.collect {
                val cupList = it.list.firstOrNull()
                if(cupList == null) {
                    createList()
                } else {
                    this.emit(CupMapper.cupListToModel(cupList))
                }
            }
        }
    }

    override suspend fun createList() = cupDao.createList()

    override suspend fun insertCup(cupName: String, cupAmount: Int) {
        val cup = CupEntity().apply {
            this.cupName = cupName
            this.cupAmount = cupAmount
        }
        cupDao.insertCup(cup)
    }

    override suspend fun updateCup(cupId: String, cupName: String, cupAmount: Int) {
        val target = CupEntity().apply {
            this.cupId = cupId
            this.cupName = cupName
            this.cupAmount = cupAmount
        }
        cupDao.updateCup(target)
    }

    override suspend fun updateAll(list: List<Cup>) {
        val mappedList = list.map {
            CupMapper.cupToEntity(it)
        }
        cupDao.updateAll(mappedList)
    }

    override suspend fun deleteCup(cupId: String) = cupDao.deleteCup(cupId)

    override fun getCurrentSelectedCupId(): Flow<String?> =
        prefDataRepository.fetchCurrentSelectedCupId().map { cupId ->
            if (cupId.isNullOrEmpty()) null else cupId
        }

    override suspend fun setCurrentSelectedCupId(cupId: String?) {
        prefDataRepository.saveCurrentSelectedCupId(cupId)
    }
}
package com.tkw.database.local

import com.tkw.database.CupDao
import com.tkw.database.model.CupEntity
import com.tkw.database.model.CupListEntity
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.reflect.KClass

class CupDaoImpl @Inject constructor(): CupDao {
    override val realm: Realm = Realm.open(getRealmConfiguration())

    private val getCupList: MutableRealm.() -> CupListEntity? = {
        this.query(CupListEntity::class, "cupId == $0", CupEntity.DEFAULT_CUP_LIST_ID).first().find()
    }

    override fun getCup(id: String): CupEntity? {
        return this.findFirst(CupListEntity::class)?.cupList?.find { it.cupId == id }
    }

    override fun getCupList(): CupListEntity? {
        return this.findByOne(CupListEntity::class, "cupId == $0", CupEntity.DEFAULT_CUP_LIST_ID)
    }

    override fun getCupListFlow(): Flow<ResultsChange<CupListEntity>> {
        return this.stream(this.find(CupListEntity::class, "cupId == $0", CupEntity.DEFAULT_CUP_LIST_ID))
    }

    override suspend fun createList() {
        this.upsert(CupListEntity())
    }

    override suspend fun insertCup(obj: CupEntity) {
        this.write {
            // 트랜잭션 내부에서 람다 호출
            val cupList = this@write.getCupList()
            if (cupList != null) {
                // EmbeddedRealmObject는 트랜잭션 내에서 새로 생성해야 함
                val newCup = CupEntity().apply {
                    cupId = obj.cupId
                    cupName = obj.cupName
                    cupAmount = obj.cupAmount
                    cupUnit = obj.cupUnit
                }
                cupList.cupList.add(newCup)
            }
        }
    }

    override suspend fun updateCup(target: CupEntity) {
        this.write {
            val origin = getCup(target.cupId)
            findLatest(origin!!)?.apply {
                cupName = target.cupName
                cupAmount = target.cupAmount
                cupUnit = target.cupUnit
            }
        }
    }

    override suspend fun updateAll(list: List<CupEntity>) {
        this.write {
            // 트랜잭션 내부에서 람다 호출
            val cupList = this@write.getCupList()
            if (cupList != null) {
                cupList.cupList.clear()
                list.forEach { cup ->
                    // EmbeddedRealmObject는 트랜잭션 내에서 새로 생성해야 함
                    val newCup = CupEntity().apply {
                        cupId = cup.cupId
                        cupName = cup.cupName
                        cupAmount = cup.cupAmount
                        cupUnit = cup.cupUnit
                    }
                    cupList.cupList.add(newCup)
                }
            }
        }
    }

    override suspend fun deleteCup(cupId: String) {
        this.write {
            // 트랜잭션 내부에서 람다 호출
            val cupList = this@write.getCupList()
            if (cupList != null) {
                // 트랜잭션 내에서 직접 검색하여 삭제
                val cupToRemove = cupList.cupList.find { it.cupId == cupId }
                if (cupToRemove != null) {
                    cupList.cupList.remove(cupToRemove)
                }
            }
        }
    }
}
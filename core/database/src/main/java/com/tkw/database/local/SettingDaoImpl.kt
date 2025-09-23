package com.tkw.database.local

import com.tkw.database.SettingDao
import com.tkw.database.model.CupEntity
import com.tkw.database.model.SettingEntity
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.reflect.KClass

class SettingDaoImpl @Inject constructor() : SettingDao {
    override val realm: Realm = Realm.open(getRealmConfiguration())

    private val settings: MutableRealm.() -> SettingEntity? = {
        this.query(SettingEntity::class, "id == $0", 0).first().find()
    }

    override suspend fun createSetting() {
        this.upsert(SettingEntity())
    }

    override suspend fun saveIntake(amount: Int) {
        this.write {
            settings()?.intake = amount
        }
    }

    override suspend fun saveUnit(unit: Int) {
        this.write {
            settings()?.unit = unit
        }
    }

    override suspend fun saveUnitString(unit: String) {
        this.write {
            settings()?.unitString = unit
        }
    }

    override fun getSetting(): Flow<ResultsChange<SettingEntity>> {
        return this.stream(find(SettingEntity::class, "id == $0", 0))
    }
}
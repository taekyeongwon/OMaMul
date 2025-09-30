package com.tkw.data.local.mapper

import com.tkw.database.model.CupEntity
import com.tkw.database.model.CupListEntity
import com.tkw.domain.model.Cup
import com.tkw.domain.model.CupList
import com.tkw.domain.model.UnitType

object CupMapper {
    fun cupToEntity(cup: Cup): CupEntity {
        return CupEntity().apply {
            this.cupId = cup.cupId
            this.cupName = cup.cupName
            this.cupAmount = cup.cupAmount
            this.cupUnit = cup.cupUnit.name
        }
    }

    fun cupToModel(entity: CupEntity): Cup {
        return Cup(
            cupId = entity.cupId,
            cupName = entity.cupName,
            cupAmount = entity.cupAmount,
            cupUnit = UnitType.fromString(entity.cupUnit)
        )
    }

    fun cupListToModel(entity: CupListEntity): CupList {
        return CupList(
            cupId = entity.cupId,
            cupList = entity.cupList.map {
                cupToModel(it)
            }
        )
    }
}
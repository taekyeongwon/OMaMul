package com.tkw.domain.model

import java.io.Serializable

data class Cup(
    var cupId: String = "",
    var cupName: String = "",
    var cupAmount: Int = DEFAULT_CUP_AMOUNT,
    var cupUnit: UnitType = UnitType.ML,
    var isChecked: Boolean = false
): Serializable {

    var createMode: Boolean = true

    fun copy(): Cup = Cup(cupId, cupName, cupAmount, cupUnit, isChecked).apply {
        createMode = this@Cup.createMode
    }

    /**
     * 현재 컵의 용량을 ml 단위로 반환
     */
    fun getAmountInMl(): Int {
        return cupUnit.toMl(cupAmount.toDouble())
    }

    /**
     * ml 단위의 용량을 현재 컵의 단위로 변환하여 설정
     */
    fun setAmountFromMl(mlAmount: Int) {
        cupAmount = cupUnit.fromMl(mlAmount).toInt()
    }

    companion object {
        const val DEFAULT_CUP_AMOUNT = 1000
        const val DEFAULT_CUP_ID = "default_cup_id"
    }
}

data class CupList(
    val cupId: String = Cup.DEFAULT_CUP_ID,
    val cupList: List<Cup> = listOf()
): Serializable
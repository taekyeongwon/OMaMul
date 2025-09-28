package com.tkw.domain.model

import java.io.Serializable

data class Cup(
    var cupId: String = "",
    var cupName: String = "",
    var cupAmount: Int = DEFAULT_CUP_AMOUNT,
    var isChecked: Boolean = false
): Serializable {

    var createMode: Boolean = true

    fun copy(): Cup = Cup(cupId, cupName, cupAmount, isChecked).apply {
        createMode = this@Cup.createMode
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
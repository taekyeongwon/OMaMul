package com.tkw.domain.model

/**
 * 물 용량 단위 타입
 * ml: 밀리리터 (기본 단위)
 * L: 리터 (1L = 1000ml)
 * CUP: 컵 (1컵 = 200ml)
 * FL_OZ: 액량 온스 (1 fl oz ≈ 29.5735ml)
 */
enum class UnitType(val displayName: String, val mlPerUnit: Double) {
    ML("ml", 1.0),
    L("L", 1000.0),
    CUP("cup", 200.0),
    FL_OZ("fl oz", 29.5735);

    companion object {
        fun fromString(value: String): UnitType {
            return values().find { it.name == value } ?: ML
        }

        fun fromDisplayName(displayName: String): UnitType {
            return values().find { it.displayName == displayName } ?: ML
        }
    }

    /**
     * 현재 단위의 값을 ml로 변환
     */
    fun toMl(amount: Double): Int {
        return (amount * mlPerUnit).toInt()
    }

    /**
     * ml 값을 현재 단위로 변환
     */
    fun fromMl(mlAmount: Int): Double {
        return mlAmount / mlPerUnit
    }
}
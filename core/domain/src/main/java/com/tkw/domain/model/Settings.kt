package com.tkw.domain.model

data class Settings(
    val intake: Int = 2000,
    val unit: Int = 0,   // 0: ml, L / 1: fl oz / 2: 컵
) {
    /**
     * 현재 설정된 단위 타입을 반환
     */
    fun getUnitType(): UnitType {
        return when(unit) {
            1 -> UnitType.FL_OZ
            2 -> UnitType.CUP
            else -> UnitType.ML
        }
    }

    /**
     * ml 값을 현재 설정된 단위로 변환하여 포맷팅
     */
    fun formatAmount(mlAmount: Int): String {
        val unitType = getUnitType()
        return when(unit) {
            0 -> {
                // ml, L 단위: 1000ml 이상이면 L로 표시
                if (mlAmount >= 1000) {
                    val liters = mlAmount / 1000.0
                    String.format("%.1fL", liters)
                } else {
                    "${mlAmount}ml"
                }
            }
            1 -> {
                // fl oz 단위
                val flOz = unitType.fromMl(mlAmount)
                String.format("%.1f fl oz", flOz)
            }
            2 -> {
                // 컵 단위
                val cups = unitType.fromMl(mlAmount)
                String.format("%.1f컵", cups)
            }
            else -> "${mlAmount}ml"
        }
    }

    /**
     * ml 값을 현재 설정된 단위로 변환 (숫자만)
     */
    fun convertAmount(mlAmount: Int): Double {
        val unitType = getUnitType()
        return when(unit) {
            0 -> {
                // ml, L 단위: 1000ml 이상이면 L로 변환
                if (mlAmount >= 1000) {
                    mlAmount / 1000.0
                } else {
                    mlAmount.toDouble()
                }
            }
            else -> unitType.fromMl(mlAmount)
        }
    }

    /**
     * ml 값을 현재 설정된 단위로 변환하여 단위 문자열 반환
     */
    fun getUnitString(mlAmount: Int): String {
        return when(unit) {
            0 -> if (mlAmount >= 1000) "L" else "ml"
            1 -> "fl oz"
            2 -> "컵"
            else -> "ml"
        }
    }
}
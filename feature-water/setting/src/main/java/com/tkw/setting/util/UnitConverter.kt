package com.tkw.setting.util

/**
 * 물 섭취량 단위 변환 유틸리티
 */
object UnitConverter {
    // 변환 상수
    private const val ML_TO_FLOZ_RATIO = 29.5735  // 1 fl.oz = 29.5735 ml
    private const val ML_TO_CUP_RATIO = 240.0     // 1 cup = 240 ml

    // 지원하는 단위
    object Units {
        const val ML = "ml"
        const val FL_OZ = "fl oz"
        const val CUP = "cup"
    }

    /**
     * ml 값을 지정된 단위로 변환
     * @param mlValue ml 단위의 값
     * @param targetUnit 목표 단위 ("ml", "fl oz", "cup")
     * @return 변환된 값
     */
    fun convertFromMl(mlValue: Int, targetUnit: String): Double {
        return when(targetUnit) {
            Units.ML -> mlValue.toDouble()
            Units.FL_OZ -> mlValue / ML_TO_FLOZ_RATIO
            Units.CUP -> mlValue / ML_TO_CUP_RATIO
            else -> mlValue.toDouble()
        }
    }

    /**
     * 지정된 단위의 값을 ml로 변환
     * @param value 변환할 값
     * @param sourceUnit 원본 단위 ("ml", "fl oz", "cup")
     * @return ml 단위의 값
     */
    fun convertToMl(value: Double, sourceUnit: String): Int {
        return when(sourceUnit) {
            Units.ML -> value.toInt()
            Units.FL_OZ -> (value * ML_TO_FLOZ_RATIO).toInt()
            Units.CUP -> (value * ML_TO_CUP_RATIO).toInt()
            else -> value.toInt()
        }
    }

    /**
     * 섭취량을 사용자 단위로 포맷팅
     * @param mlValue ml 단위의 값
     * @param userUnit 사용자 설정 단위
     * @return 포맷된 문자열 (예: "1500ml", "50.7fl oz", "12.3cup")
     */
    fun formatIntake(mlValue: Int, userUnit: String): String {
        val convertedValue = convertFromMl(mlValue, userUnit)

        return when(userUnit) {
            Units.ML -> {
                if (mlValue >= 1000) {
                    val liters = mlValue / 1000.0
                    "${"%.1f".format(liters).trimEnd('0').trimEnd('.')}L"
                } else {
                    "${convertedValue.toInt()}ml"
                }
            }
            Units.FL_OZ -> "${"%.1f".format(convertedValue).trimEnd('0').trimEnd('.')}fl oz"
            Units.CUP -> "${"%.1f".format(convertedValue).trimEnd('0').trimEnd('.')}컵"
            else -> "${convertedValue.toInt()}ml"
        }
    }

    /**
     * 입력값의 유효성 검사
     * @param value 입력값
     * @param unit 단위 ("ml", "fl oz", "cup")
     * @return 유효한지 여부
     */
    fun isValidIntake(value: Double, unit: String): Boolean {
        val mlValue = convertToMl(value, unit)
        return mlValue in 100..10000 // ml 기준 100ml ~ 10L
    }

    /**
     * 권장 섭취량을 사용자 단위로 변환
     */
    fun getRecommendedIntake(gender: String, unit: String): String {
        val mlValue = when(gender) {
            "male" -> 2500
            "female" -> 2000
            else -> 2000
        }
        return formatIntake(mlValue, unit)
    }

    /**
     * 단위 변환 시 기존 값 변환
     * @param currentValue 현재 값
     * @param fromUnit 기존 단위
     * @param toUnit 새 단위
     * @return 변환된 값
     */
    fun convertBetweenUnits(currentValue: Int, fromUnit: String, toUnit: String): Int {
        val mlValue = convertToMl(currentValue.toDouble(), fromUnit)
        return mlValue
    }

    /**
     * 단위별 최소/최대 입력값 범위 반환
     */
    fun getInputRange(unit: String): Pair<Double, Double> {
        return when(unit) {
            Units.ML -> Pair(100.0, 10000.0)
            Units.FL_OZ -> Pair(3.4, 338.0)    // 100ml ~ 10L를 fl oz로 변환
            Units.CUP -> Pair(0.4, 41.7)       // 100ml ~ 10L를 cup으로 변환
            else -> Pair(100.0, 10000.0)
        }
    }

    /**
     * 단위 표시명 반환
     */
    fun getUnitDisplayName(unit: String): String {
        return when(unit) {
            Units.ML -> "밀리리터 (ml)"
            Units.FL_OZ -> "플루이드 온스 (fl oz)"
            Units.CUP -> "컵 (cup)"
            else -> "밀리리터 (ml)"
        }
    }

    /**
     * 단위별 설명 반환
     */
    fun getUnitDescription(unit: String): String {
        return when(unit) {
            Units.ML -> "1ml = 1ml"
            Units.FL_OZ -> "1fl oz ≈ 29.6ml"
            Units.CUP -> "1cup ≈ 240ml"
            else -> "1ml = 1ml"
        }
    }

    /**
     * 단위 인덱스를 단위 문자열로 변환
     * @param unitIndex 단위 인덱스 (0: ml, 1: fl oz)
     * @return 단위 문자열
     */
    fun getUnitString(unitIndex: Int): String {
        return when(unitIndex) {
            0 -> Units.ML
            1 -> Units.FL_OZ
            2 -> Units.CUP
            else -> Units.ML
        }
    }

    /**
     * 단위 문자열을 단위 인덱스로 변환
     * @param unit 단위 문자열
     * @return 단위 인덱스
     */
    fun getUnitIndex(unit: String): Int {
        return when(unit) {
            Units.ML -> 0
            Units.FL_OZ -> 1
            Units.CUP -> 2
            else -> 0
        }
    }
}
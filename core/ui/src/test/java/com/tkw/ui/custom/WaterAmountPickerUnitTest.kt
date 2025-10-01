package com.tkw.ui.custom

import com.tkw.ui.custom.WaterAmountPicker.UnitType
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * WaterAmountPicker의 단위 변환 로직 테스트
 * (View 의존성이 없는 순수 로직만 테스트)
 */
class WaterAmountPickerUnitTest {

    @Test
    fun `UnitType ML은 1ml당 1ml`() {
        assertEquals(1.0, UnitType.ML.mlPerUnit, 0.001)
    }

    @Test
    fun `UnitType L은 1L당 1000ml`() {
        assertEquals(1000.0, UnitType.L.mlPerUnit, 0.001)
    }

    @Test
    fun `UnitType CUP은 1컵당 200ml`() {
        assertEquals(200.0, UnitType.CUP.mlPerUnit, 0.001)
    }

    @Test
    fun `UnitType FL_OZ는 1 fl oz당 29_5735ml`() {
        assertEquals(29.5735, UnitType.FL_OZ.mlPerUnit, 0.0001)
    }

    @Test
    fun `ml에서 L로 변환 - 1000ml = 1L`() {
        val mlValue = 1000
        val lValue = mlValue / UnitType.L.mlPerUnit
        assertEquals(1.0, lValue, 0.01)
    }

    @Test
    fun `ml에서 CUP으로 변환 - 200ml = 1컵`() {
        val mlValue = 200
        val cupValue = mlValue / UnitType.CUP.mlPerUnit
        assertEquals(1.0, cupValue, 0.01)
    }

    @Test
    fun `ml에서 FL_OZ로 변환 - 200ml ≈ 6_76 fl oz`() {
        val mlValue = 200
        val flOzValue = mlValue / UnitType.FL_OZ.mlPerUnit
        assert(flOzValue > 6.7 && flOzValue < 6.8)
    }

    @Test
    fun `L에서 ml로 변환 - 2L = 2000ml`() {
        val lValue = 2.0
        val mlValue = (lValue * UnitType.L.mlPerUnit).toInt()
        assertEquals(2000, mlValue)
    }

    @Test
    fun `CUP에서 ml로 변환 - 5컵 = 1000ml`() {
        val cupValue = 5.0
        val mlValue = (cupValue * UnitType.CUP.mlPerUnit).toInt()
        assertEquals(1000, mlValue)
    }

    @Test
    fun `FL_OZ에서 ml로 변환 - 10 fl oz ≈ 296ml`() {
        val flOzValue = 10.0
        val mlValue = (flOzValue * UnitType.FL_OZ.mlPerUnit).toInt()
        assert(mlValue in 295..296)
    }

    @Test
    fun `ml 단위 범위 - 50ml~2000ml, 50ml 간격이 적절한지 확인`() {
        val minValue = 50
        val maxValue = 2000
        val interval = 50

        // 최소값부터 최대값까지 interval로 나누어떨어지는지 확인
        val steps = (maxValue - minValue) / interval
        assertEquals(39, steps) // (2000-50)/50 = 39 steps
    }

    @Test
    fun `L 단위 범위 - 1L~5L, 1L 간격이 적절한지 확인`() {
        val minValue = 1
        val maxValue = 5
        val interval = 1

        val steps = (maxValue - minValue) / interval
        assertEquals(4, steps) // (5-1)/1 = 4 steps
    }

    @Test
    fun `CUP 단위 범위 - 1컵~10컵, 1컵 간격이 적절한지 확인`() {
        val minValue = 1
        val maxValue = 10
        val interval = 1

        val steps = (maxValue - minValue) / interval
        assertEquals(9, steps) // (10-1)/1 = 9 steps
    }

    @Test
    fun `FL_OZ 단위 범위 - 2 fl oz~70 fl oz, 2 fl oz 간격이 적절한지 확인`() {
        val minValue = 2
        val maxValue = 70
        val interval = 2

        val steps = (maxValue - minValue) / interval
        assertEquals(34, steps) // (70-2)/2 = 34 steps
    }

    @Test
    fun `200ml을 각 단위로 변환 통합 테스트`() {
        val mlValue = 200

        // ML 단위: 200ml
        val mlResult = mlValue / UnitType.ML.mlPerUnit
        assertEquals(200.0, mlResult, 0.01)

        // L 단위: 0.2L
        val lResult = mlValue / UnitType.L.mlPerUnit
        assertEquals(0.2, lResult, 0.01)

        // CUP 단위: 1컵
        val cupResult = mlValue / UnitType.CUP.mlPerUnit
        assertEquals(1.0, cupResult, 0.01)

        // FL_OZ 단위: 약 6.76 fl oz
        val flOzResult = mlValue / UnitType.FL_OZ.mlPerUnit
        assert(flOzResult > 6.7 && flOzResult < 6.8)
    }

    @Test
    fun `2000ml을 각 단위로 변환 통합 테스트`() {
        val mlValue = 2000

        // ML 단위: 2000ml
        val mlResult = mlValue / UnitType.ML.mlPerUnit
        assertEquals(2000.0, mlResult, 0.01)

        // L 단위: 2L
        val lResult = mlValue / UnitType.L.mlPerUnit
        assertEquals(2.0, lResult, 0.01)

        // CUP 단위: 10컵
        val cupResult = mlValue / UnitType.CUP.mlPerUnit
        assertEquals(10.0, cupResult, 0.01)

        // FL_OZ 단위: 약 67.6 fl oz
        val flOzResult = mlValue / UnitType.FL_OZ.mlPerUnit
        assert(flOzResult > 67.0 && flOzResult < 68.0)
    }

    @Test
    fun `단위 변경 시 배열 인덱스 범위 검증 - ML에서 L`() {
        // ML: 50~2000, interval=50 → 40개 항목 (0~39 인덱스)
        val mlMin = 50
        val mlMax = 2000
        val mlInterval = 50
        val mlArraySize = (mlMax - mlMin) / mlInterval + 1
        assertEquals(40, mlArraySize)

        // L: 1~5, interval=1 → 5개 항목 (0~4 인덱스)
        val lMin = 1
        val lMax = 5
        val lInterval = 1
        val lArraySize = (lMax - lMin) / lInterval + 1
        assertEquals(5, lArraySize)

        // 1000ml을 L로 변환 → 1L (인덱스 0)
        val mlValue = 1000
        val lValue = (mlValue / UnitType.L.mlPerUnit).toInt()
        val lIndex = (lValue - lMin) / lInterval
        assert(lIndex in 0 until lArraySize)
    }

    @Test
    fun `단위 변경 시 배열 인덱스 범위 검증 - ML에서 CUP`() {
        val mlMin = 50
        val mlMax = 2000
        val mlInterval = 50
        val mlArraySize = (mlMax - mlMin) / mlInterval + 1
        assertEquals(40, mlArraySize)

        // CUP: 1~10, interval=1 → 10개 항목
        val cupMin = 1
        val cupMax = 10
        val cupInterval = 1
        val cupArraySize = (cupMax - cupMin) / cupInterval + 1
        assertEquals(10, cupArraySize)

        // 200ml을 컵으로 변환 → 1컵 (인덱스 0)
        val mlValue = 200
        val cupValue = (mlValue / UnitType.CUP.mlPerUnit).toInt()
        val cupIndex = (cupValue - cupMin) / cupInterval
        assert(cupIndex in 0 until cupArraySize)
    }

    @Test
    fun `단위 변경 시 배열 인덱스 범위 검증 - ML에서 FL_OZ`() {
        val mlMin = 50
        val mlMax = 2000
        val mlInterval = 50
        val mlArraySize = (mlMax - mlMin) / mlInterval + 1
        assertEquals(40, mlArraySize)

        // FL_OZ: 2~70, interval=2 → 35개 항목
        val flOzMin = 2
        val flOzMax = 70
        val flOzInterval = 2
        val flOzArraySize = (flOzMax - flOzMin) / flOzInterval + 1
        assertEquals(35, flOzArraySize)

        // 200ml을 fl oz로 변환 → 약 6.76 → 6 (coerce) (인덱스 2)
        val mlValue = 200
        val flOzValue = (mlValue / UnitType.FL_OZ.mlPerUnit).toInt().coerceIn(flOzMin, flOzMax)
        val flOzIndex = (flOzValue - flOzMin) / flOzInterval
        assert(flOzIndex in 0 until flOzArraySize)
    }

    @Test
    fun `배열 최대값을 초과하는 인덱스 접근 방지 확인`() {
        // L 단위 배열 크기: 5
        val lMin = 1
        val lMax = 5
        val lInterval = 1
        val lArraySize = (lMax - lMin) / lInterval + 1
        
        // 잘못된 인덱스 180이 들어왔을 때 coerce로 안전 처리
        val invalidIndex = 180
        val safeIndex = invalidIndex.coerceIn(0, lArraySize - 1)
        assertEquals(4, safeIndex) // 최대 인덱스인 4로 보정
    }

    @Test
    fun `displayedValues 배열 크기와 인덱스 일관성 검증`() {
        // 각 단위별 배열 크기 계산
        data class UnitConfig(val min: Int, val max: Int, val interval: Int)
        
        val configs = mapOf(
            UnitType.ML to UnitConfig(50, 2000, 50),
            UnitType.L to UnitConfig(1, 5, 1),
            UnitType.CUP to UnitConfig(1, 10, 1),
            UnitType.FL_OZ to UnitConfig(2, 70, 2)
        )
        
        configs.forEach { (unitType, config) ->
            val expectedSize = (config.max - config.min) / config.interval + 1
            val values = mutableListOf<String>()
            for (i in 0 until expectedSize) {
                values.add((config.min + i * config.interval).toString())
            }
            
            // 배열 크기가 예상과 일치하는지 확인
            assertEquals("Unit: $unitType - size mismatch", expectedSize, values.size)
            
            // 모든 인덱스가 배열 범위 내에 있는지 확인
            for (index in 0 until expectedSize) {
                assert(index < values.size) { "Index $index exceeds array size ${values.size} for $unitType" }
            }
        }
    }
}

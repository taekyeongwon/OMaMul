package com.tkw.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * UnitType enum의 변환 기능 테스트
 */
class UnitTypeTest {

    @Test
    fun `ML 단위는 1ml당 1ml`() {
        assertEquals(1.0, UnitType.ML.mlPerUnit, 0.001)
    }

    @Test
    fun `L 단위는 1L당 1000ml`() {
        assertEquals(1000.0, UnitType.L.mlPerUnit, 0.001)
    }

    @Test
    fun `CUP 단위는 1컵당 200ml`() {
        assertEquals(200.0, UnitType.CUP.mlPerUnit, 0.001)
    }

    @Test
    fun `FL_OZ 단위는 1 fl oz당 29_5735ml`() {
        assertEquals(29.5735, UnitType.FL_OZ.mlPerUnit, 0.0001)
    }

    @Test
    fun `toMl - ML 단위에서 ml로 변환`() {
        assertEquals(500, UnitType.ML.toMl(500.0))
        assertEquals(1000, UnitType.ML.toMl(1000.0))
    }

    @Test
    fun `toMl - L 단위에서 ml로 변환`() {
        assertEquals(1000, UnitType.L.toMl(1.0))
        assertEquals(2000, UnitType.L.toMl(2.0))
        assertEquals(2500, UnitType.L.toMl(2.5))
    }

    @Test
    fun `toMl - CUP 단위에서 ml로 변환`() {
        assertEquals(200, UnitType.CUP.toMl(1.0))
        assertEquals(400, UnitType.CUP.toMl(2.0))
        assertEquals(1000, UnitType.CUP.toMl(5.0))
    }

    @Test
    fun `toMl - FL_OZ 단위에서 ml로 변환`() {
        // 10 fl oz ≈ 295.735ml
        val result = UnitType.FL_OZ.toMl(10.0)
        assert(result in 295..296) // 반올림으로 인한 오차 허용
    }

    @Test
    fun `fromMl - ML 단위로 ml에서 변환`() {
        assertEquals(500.0, UnitType.ML.fromMl(500), 0.01)
        assertEquals(1000.0, UnitType.ML.fromMl(1000), 0.01)
    }

    @Test
    fun `fromMl - L 단위로 ml에서 변환`() {
        assertEquals(1.0, UnitType.L.fromMl(1000), 0.01)
        assertEquals(2.0, UnitType.L.fromMl(2000), 0.01)
        assertEquals(2.5, UnitType.L.fromMl(2500), 0.01)
    }

    @Test
    fun `fromMl - CUP 단위로 ml에서 변환`() {
        assertEquals(1.0, UnitType.CUP.fromMl(200), 0.01)
        assertEquals(2.0, UnitType.CUP.fromMl(400), 0.01)
        assertEquals(5.0, UnitType.CUP.fromMl(1000), 0.01)
    }

    @Test
    fun `fromMl - FL_OZ 단위로 ml에서 변환`() {
        // 200ml ≈ 6.76 fl oz
        val result = UnitType.FL_OZ.fromMl(200)
        assert(result > 6.7 && result < 6.8)
    }

    @Test
    fun `fromString - 문자열에서 UnitType 변환`() {
        assertEquals(UnitType.ML, UnitType.fromString("ML"))
        assertEquals(UnitType.L, UnitType.fromString("L"))
        assertEquals(UnitType.CUP, UnitType.fromString("CUP"))
        assertEquals(UnitType.FL_OZ, UnitType.fromString("FL_OZ"))
    }

    @Test
    fun `fromString - 잘못된 문자열은 ML 반환`() {
        assertEquals(UnitType.ML, UnitType.fromString("INVALID"))
        assertEquals(UnitType.ML, UnitType.fromString(""))
    }

    @Test
    fun `fromDisplayName - 표시명에서 UnitType 변환`() {
        assertEquals(UnitType.ML, UnitType.fromDisplayName("ml"))
        assertEquals(UnitType.L, UnitType.fromDisplayName("L"))
        assertEquals(UnitType.CUP, UnitType.fromDisplayName("cup"))
        assertEquals(UnitType.FL_OZ, UnitType.fromDisplayName("fl oz"))
    }

    @Test
    fun `fromDisplayName - 잘못된 표시명은 ML 반환`() {
        assertEquals(UnitType.ML, UnitType.fromDisplayName("invalid"))
    }

    @Test
    fun `양방향 변환 테스트 - ML`() {
        val originalMl = 1500
        val converted = UnitType.ML.fromMl(originalMl)
        val backToMl = UnitType.ML.toMl(converted)
        assertEquals(originalMl, backToMl)
    }

    @Test
    fun `양방향 변환 테스트 - L`() {
        val originalMl = 2000
        val converted = UnitType.L.fromMl(originalMl)
        val backToMl = UnitType.L.toMl(converted)
        assertEquals(originalMl, backToMl)
    }

    @Test
    fun `양방향 변환 테스트 - CUP`() {
        val originalMl = 1000
        val converted = UnitType.CUP.fromMl(originalMl)
        val backToMl = UnitType.CUP.toMl(converted)
        assertEquals(originalMl, backToMl)
    }

    @Test
    fun `양방향 변환 테스트 - FL_OZ`() {
        val originalMl = 300
        val converted = UnitType.FL_OZ.fromMl(originalMl)
        val backToMl = UnitType.FL_OZ.toMl(converted)
        // 반올림 오차 허용 (±1ml)
        assert(kotlin.math.abs(originalMl - backToMl) <= 1)
    }
}

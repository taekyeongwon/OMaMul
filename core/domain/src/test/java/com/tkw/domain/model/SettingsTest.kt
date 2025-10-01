package com.tkw.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Settings 모델의 단위 변환 기능 테스트
 */
class SettingsTest {

    @Test
    fun `getUnitType - unit 0이면 ML 반환`() {
        val settings = Settings(intake = 2000, unit = 0)
        assertEquals(UnitType.ML, settings.getUnitType())
    }

    @Test
    fun `getUnitType - unit 1이면 FL_OZ 반환`() {
        val settings = Settings(intake = 2000, unit = 1)
        assertEquals(UnitType.FL_OZ, settings.getUnitType())
    }

    @Test
    fun `getUnitType - unit 2이면 CUP 반환`() {
        val settings = Settings(intake = 2000, unit = 2)
        assertEquals(UnitType.CUP, settings.getUnitType())
    }

    @Test
    fun `formatAmount - ml,L 모드에서 1000ml 미만은 ml로 표시`() {
        val settings = Settings(intake = 2000, unit = 0)
        assertEquals("500ml", settings.formatAmount(500))
        assertEquals("999ml", settings.formatAmount(999))
    }

    @Test
    fun `formatAmount - ml,L 모드에서 1000ml 이상은 L로 표시`() {
        val settings = Settings(intake = 2000, unit = 0)
        assertEquals("1.0L", settings.formatAmount(1000))
        assertEquals("2.0L", settings.formatAmount(2000))
        assertEquals("2.5L", settings.formatAmount(2500))
    }

    @Test
    fun `formatAmount - fl oz 모드에서 올바른 변환`() {
        val settings = Settings(intake = 2000, unit = 1)
        // 200ml ≈ 6.76 fl oz
        val result = settings.formatAmount(200)
        assert(result.contains("6.") && result.contains("fl oz"))
    }

    @Test
    fun `formatAmount - 컵 모드에서 올바른 변환`() {
        val settings = Settings(intake = 2000, unit = 2)
        // 200ml = 1컵
        assertEquals("1.0컵", settings.formatAmount(200))
        // 400ml = 2컵
        assertEquals("2.0컵", settings.formatAmount(400))
        // 1000ml = 5컵
        assertEquals("5.0컵", settings.formatAmount(1000))
    }

    @Test
    fun `convertAmount - ml,L 모드에서 1000ml 미만은 ml 숫자 반환`() {
        val settings = Settings(intake = 2000, unit = 0)
        assertEquals(500.0, settings.convertAmount(500), 0.01)
        assertEquals(999.0, settings.convertAmount(999), 0.01)
    }

    @Test
    fun `convertAmount - ml,L 모드에서 1000ml 이상은 L 숫자 반환`() {
        val settings = Settings(intake = 2000, unit = 0)
        assertEquals(1.0, settings.convertAmount(1000), 0.01)
        assertEquals(2.0, settings.convertAmount(2000), 0.01)
        assertEquals(2.5, settings.convertAmount(2500), 0.01)
    }

    @Test
    fun `convertAmount - fl oz 모드에서 올바른 변환`() {
        val settings = Settings(intake = 2000, unit = 1)
        // 200ml ≈ 6.76 fl oz
        val result = settings.convertAmount(200)
        assert(result > 6.7 && result < 6.8)
    }

    @Test
    fun `convertAmount - 컵 모드에서 올바른 변환`() {
        val settings = Settings(intake = 2000, unit = 2)
        // 200ml = 1컵
        assertEquals(1.0, settings.convertAmount(200), 0.01)
        // 1000ml = 5컵
        assertEquals(5.0, settings.convertAmount(1000), 0.01)
    }

    @Test
    fun `getUnitString - ml,L 모드에서 올바른 단위 문자열 반환`() {
        val settings = Settings(intake = 2000, unit = 0)
        assertEquals("ml", settings.getUnitString(500))
        assertEquals("L", settings.getUnitString(1000))
        assertEquals("L", settings.getUnitString(2000))
    }

    @Test
    fun `getUnitString - fl oz 모드에서 fl oz 반환`() {
        val settings = Settings(intake = 2000, unit = 1)
        assertEquals("fl oz", settings.getUnitString(200))
        assertEquals("fl oz", settings.getUnitString(2000))
    }

    @Test
    fun `getUnitString - 컵 모드에서 컵 반환`() {
        val settings = Settings(intake = 2000, unit = 2)
        assertEquals("컵", settings.getUnitString(200))
        assertEquals("컵", settings.getUnitString(1000))
    }

    @Test
    fun `목표량 2000ml을 각 단위로 변환 통합 테스트`() {
        val intakeGoal = 2000

        // ml, L 모드: 2.0L로 표시
        val settingsMlL = Settings(intake = intakeGoal, unit = 0)
        assertEquals("2.0L", settingsMlL.formatAmount(intakeGoal))

        // fl oz 모드: 약 67.6 fl oz로 표시
        val settingsFlOz = Settings(intake = intakeGoal, unit = 1)
        val flOzResult = settingsFlOz.formatAmount(intakeGoal)
        assert(flOzResult.contains("67.") && flOzResult.contains("fl oz"))

        // 컵 모드: 10.0컵으로 표시
        val settingsCup = Settings(intake = intakeGoal, unit = 2)
        assertEquals("10.0컵", settingsCup.formatAmount(intakeGoal))
    }
}

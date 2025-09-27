package com.tkw.ui.view

import android.graphics.Outline
import android.graphics.Path
import android.view.View
import android.view.ViewOutlineProvider

/**
 * 원형으로 클립 처리하는 OutlineProvider
 */
class CupClipOutlineProvider : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        val width = view.width
        val height = view.height

        if (width <= 0 || height <= 0) return

        // 정사각형 영역을 기준으로 원형 클립 생성
        val size = minOf(width, height)
        val centerX = width / 2
        val centerY = height / 2
        val radius = size / 2

        // 원형 클립 영역 설정
        outline.setOval(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
    }
}
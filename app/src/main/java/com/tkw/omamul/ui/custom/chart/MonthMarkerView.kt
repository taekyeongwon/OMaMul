package com.tkw.omamul.ui.custom.chart

import android.content.Context
import android.graphics.Canvas
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.tkw.omamul.R

class MonthMarkerView(context: Context?, layoutResource: Int)
    : MarkerView(context, layoutResource) {

    private val tvDay: TextView = findViewById(R.id.tv_day)
    private val tvAmount: TextView = findViewById(R.id.tv_amount)

    // draw override를 사용해 marker의 위치 조정 (bar의 상단 중앙)
    override fun draw(canvas: Canvas) {
        canvas.translate(-(width / 2).toFloat(), -height.toFloat() - 16f)
        super.draw(canvas)
    }

    // entry를 content의 텍스트에 지정
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val x = e?.x
        val y = e?.y ?: 0f
        val milliLiterUnit = context.getString(R.string.unit_ml_no_bracket)
        tvDay.text = String.format("%.0f", x).plus("일")
        tvAmount.text = String.format("%.0f", y * 1000).plus(milliLiterUnit)  //L -> ml 변환해서 표시

        super.refreshContent(e, highlight)
    }

}
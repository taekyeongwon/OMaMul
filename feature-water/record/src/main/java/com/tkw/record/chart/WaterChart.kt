package com.tkw.record.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

data class ChartData(
    val x: Float,
    val y: Float,
    val label: String = ""
)

@Composable
fun WaterBarChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    maxValue: Float = 2000f,
    targetValue: Float = 1500f,
    barColor: Color = Color(0xFF4A90E2),
    targetColor: Color = Color(0xFFFF9800),
    backgroundColor: Color = Color(0xFFE3F2FD)
) {
    var chartData by remember { mutableStateOf(data) }

    LaunchedEffect(data) {
        chartData = data
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // 차트 제목
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "일일 물 섭취량",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 목표선 표시
                Box(
                    modifier = Modifier
                        .size(12.dp, 2.dp)
                        .background(targetColor)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "목표 ${targetValue.toInt()}ml",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = Color(0xFF757575)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 차트 영역
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (chartData.isNotEmpty()) {
                drawBarChart(
                    data = chartData,
                    maxValue = maxValue,
                    targetValue = targetValue,
                    barColor = barColor,
                    targetColor = targetColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // X축 라벨 (시간)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("0", "6", "12", "18", "24").forEach { hour ->
                Text(
                    text = "${hour}시",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}

private fun DrawScope.drawBarChart(
    data: List<ChartData>,
    maxValue: Float,
    targetValue: Float,
    barColor: Color,
    targetColor: Color
) {
    val chartWidth = size.width
    val chartHeight = size.height - 20.dp.toPx() // Y축 라벨 공간 확보
    val barWidth = chartWidth / 24f // 24시간으로 나누기
    val actualMaxValue = max(maxValue, data.maxOfOrNull { it.y } ?: 0f)

    // 목표선 그리기
    val targetY = chartHeight - (targetValue / actualMaxValue) * chartHeight
    drawLine(
        color = targetColor,
        start = Offset(0f, targetY),
        end = Offset(chartWidth, targetY),
        strokeWidth = 2.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f))
    )

    // 시간별 누적 데이터를 위한 맵
    val hourlyData = mutableMapOf<Int, Float>()

    // 데이터를 시간별로 그룹화하고 누적
    data.forEach { point ->
        val hour = point.x.toInt()
        hourlyData[hour] = (hourlyData[hour] ?: 0f) + point.y
    }

    // 바 차트 그리기
    hourlyData.forEach { (hour, value) ->
        val barHeight = (value / actualMaxValue) * chartHeight
        val barLeft = hour * barWidth
        val barTop = chartHeight - barHeight

        // 그라데이션 바 그리기
        val gradient = Brush.verticalGradient(
            colors = listOf(
                barColor,
                barColor.copy(alpha = 0.7f)
            ),
            startY = barTop,
            endY = chartHeight
        )

        drawRect(
            brush = gradient,
            topLeft = Offset(barLeft + barWidth * 0.1f, barTop),
            size = androidx.compose.ui.geometry.Size(
                barWidth * 0.8f,
                barHeight
            )
        )

        // 바 테두리
        drawRect(
            color = barColor,
            topLeft = Offset(barLeft + barWidth * 0.1f, barTop),
            size = androidx.compose.ui.geometry.Size(
                barWidth * 0.8f,
                barHeight
            ),
            style = Stroke(width = 1.dp.toPx())
        )
    }

    // Y축 그리드 라인
    val gridLines = 5
    repeat(gridLines) { i ->
        val y = (chartHeight / gridLines) * i
        drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(0f, y),
            end = Offset(chartWidth, y),
            strokeWidth = 1.dp.toPx()
        )
    }
}

@Composable
fun WaterLineChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier,
    maxValue: Float = 2000f,
    lineColor: Color = Color(0xFF4A90E2),
    fillColor: Color = Color(0xFF4A90E2).copy(alpha = 0.3f),
    backgroundColor: Color = Color(0xFFE3F2FD)
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        if (data.isNotEmpty()) {
            drawLineChart(
                data = data,
                maxValue = maxValue,
                lineColor = lineColor,
                fillColor = fillColor
            )
        }
    }
}

private fun DrawScope.drawLineChart(
    data: List<ChartData>,
    maxValue: Float,
    lineColor: Color,
    fillColor: Color
) {
    val chartWidth = size.width
    val chartHeight = size.height
    val actualMaxValue = max(maxValue, data.maxOfOrNull { it.y } ?: 0f)

    if (data.size < 2) return

    val path = Path()
    val fillPath = Path()

    data.forEachIndexed { index, point ->
        val x = (point.x / data.last().x) * chartWidth
        val y = chartHeight - (point.y / actualMaxValue) * chartHeight

        if (index == 0) {
            path.moveTo(x, y)
            fillPath.moveTo(x, chartHeight)
            fillPath.lineTo(x, y)
        } else {
            path.lineTo(x, y)
            fillPath.lineTo(x, y)
        }
    }

    // 채우기 영역 완성
    fillPath.lineTo(chartWidth, chartHeight)
    fillPath.close()

    // 채우기 그리기
    drawPath(
        path = fillPath,
        color = fillColor
    )

    // 라인 그리기
    drawPath(
        path = path,
        color = lineColor,
        style = Stroke(
            width = 3.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // 데이터 포인트 그리기
    data.forEach { point ->
        val x = (point.x / data.last().x) * chartWidth
        val y = chartHeight - (point.y / actualMaxValue) * chartHeight

        drawCircle(
            color = lineColor,
            radius = 4.dp.toPx(),
            center = Offset(x, y)
        )

        drawCircle(
            color = Color.White,
            radius = 2.dp.toPx(),
            center = Offset(x, y)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WaterBarChartPreview() {
    val sampleData = listOf(
        ChartData(0f, 200f, "0시"),
        ChartData(6f, 150f, "6시"),
        ChartData(8f, 300f, "8시"),
        ChartData(12f, 450f, "12시"),
        ChartData(14f, 200f, "14시"),
        ChartData(18f, 350f, "18시"),
        ChartData(20f, 250f, "20시"),
        ChartData(22f, 150f, "22시")
    )

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        WaterBarChart(
            data = sampleData,
            maxValue = 2000f,
            targetValue = 1500f
        )

        Spacer(modifier = Modifier.height(16.dp))

        WaterLineChart(
            data = sampleData,
            maxValue = 2000f
        )
    }
}
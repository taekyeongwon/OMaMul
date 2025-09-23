package com.tkw.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tkw.domain.model.DayOfWater
import com.tkw.domain.model.DayOfWaterList
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.tkw.ui.chart.CustomBarChart
import com.tkw.ui.chart.CustomLineChart
import com.tkw.ui.chart.marker.MarkerType
import com.tkw.ui.R
import kotlinx.coroutines.launch

@Composable
fun WaterLogScreen(
    onNavigateBack: () -> Unit,
    viewModel: LogViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(null)

    var showEditDialog by remember { mutableStateOf(false) }
    var editWater by remember { mutableStateOf<com.tkw.domain.model.Water?>(null) }

    // SideEffect 처리
    LaunchedEffect(sideEffect) {
        val effect = sideEffect
        if (effect is LogContract.SideEffect.ShowEditDialog) {
            editWater = effect.water
            showEditDialog = true
        }
    }

    WaterLogScreenContent(
        state = state,
        onDayEvent = { move -> viewModel.setEvent(LogContract.Event.DayAmountEvent(move)) },
        onWeekEvent = { move -> viewModel.setEvent(LogContract.Event.WeekAmountEvent(move)) },
        onMonthEvent = { move -> viewModel.setEvent(LogContract.Event.MonthAmountEvent(move)) },
        onDayEventByDate = { date -> viewModel.setEvent(LogContract.Event.DayAmountEventByDate(date)) },
        onShowAddDialog = { viewModel.setEvent(LogContract.Event.ShowAddDialog) },
        onShowEditDialog = { water -> viewModel.setEvent(LogContract.Event.ShowEditDialog(water)) },
        onRemoveDayAmount = { water -> viewModel.setEvent(LogContract.Event.RemoveDayAmount(water)) },
        onAddAmount = { amount, date -> viewModel.addAmount(amount, date) },
        onUpdateAmount = { origin, amount, date -> viewModel.updateAmount(origin, amount, date) }
    )

    // 편집 다이얼로그
    if (showEditDialog) {
        WaterEditDialog(
            water = editWater,
            onDismiss = {
                showEditDialog = false
                editWater = null
            },
            onConfirm = { amount, date ->
                if (editWater != null) {
                    viewModel.updateAmount(editWater!!, amount, date)
                } else {
                    viewModel.addAmount(amount, date)
                }
                showEditDialog = false
                editWater = null
            }
        )
    }
}

@Composable
private fun WaterLogScreenContent(
    state: LogContract.State = LogContract.State.Loading(false),
    onDayEvent: (LogContract.Move) -> Unit = {},
    onWeekEvent: (LogContract.Move) -> Unit = {},
    onMonthEvent: (LogContract.Move) -> Unit = {},
    onDayEventByDate: (String) -> Unit = {},
    onShowAddDialog: () -> Unit = {},
    onShowEditDialog: (com.tkw.domain.model.Water) -> Unit = {},
    onRemoveDayAmount: (com.tkw.domain.model.Water) -> Unit = {},
    onAddAmount: (Int, String) -> Unit = { _, _ -> },
    onUpdateAmount: (com.tkw.domain.model.Water, Int, String) -> Unit = { _, _, _ -> }
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val tabTitles = listOf(
        stringResource(R.string.log_day_title),
        stringResource(R.string.log_week_title),
        stringResource(R.string.log_month_title)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE3F2FD),
                        Color(0xFFBBDEFB)
                    )
                )
            )
    ) {
        // 상단 헤더
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "물 섭취 기록",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                    )

                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = Color(0xFF4A90E2),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 탭 레이아웃
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF4A90E2),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = Color(0xFF4A90E2)
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }

        // 페이저 콘텐츠
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> DayLogContent(
                    state = state,
                    isCurrentPage = pagerState.currentPage == 0,
                    onEvent = onDayEvent,
                    onEventByDate = onDayEventByDate,
                    onShowAddDialog = onShowAddDialog,
                    onShowEditDialog = onShowEditDialog,
                    onRemoveDayAmount = onRemoveDayAmount
                )
                1 -> WeekLogContent(
                    state = state,
                    isCurrentPage = pagerState.currentPage == 1,
                    onEvent = onWeekEvent
                )
                2 -> MonthLogContent(
                    state = state,
                    isCurrentPage = pagerState.currentPage == 2,
                    onEvent = onMonthEvent
                )
            }
        }
    }
}

@Composable
private fun DayLogContent(
    state: LogContract.State,
    isCurrentPage: Boolean,
    onEvent: (LogContract.Move) -> Unit,
    onEventByDate: (String) -> Unit,
    onShowAddDialog: () -> Unit,
    onShowEditDialog: (com.tkw.domain.model.Water) -> Unit,
    onRemoveDayAmount: (com.tkw.domain.model.Water) -> Unit
) {
    LaunchedEffect(isCurrentPage) {
        if (isCurrentPage) {
            onEvent(LogContract.Move.INIT)
        }
    }

    when (state) {
        is LogContract.State.Complete -> {
            if (state.unit == LogContract.DateUnit.DAY && state.data.list.isNotEmpty()) {
                val dayOfWater = state.data.list[0]
                DayLogView(
                    dayOfWater = dayOfWater,
                    onEvent = onEvent,
                    onEventByDate = onEventByDate,
                    onShowAddDialog = onShowAddDialog,
                    onShowEditDialog = onShowEditDialog,
                    onRemoveDayAmount = onRemoveDayAmount
                )
            } else {
                EmptyLogView()
            }
        }
        is LogContract.State.Loading -> {
            LoadingView()
        }
        LogContract.State.Error -> {
            ErrorView()
        }
    }
}

@Composable
private fun WeekLogContent(
    state: LogContract.State,
    isCurrentPage: Boolean,
    onEvent: (LogContract.Move) -> Unit
) {
    LaunchedEffect(isCurrentPage) {
        if (isCurrentPage) {
            onEvent(LogContract.Move.INIT)
        }
    }

    when (state) {
        is LogContract.State.Complete -> {
            if (state.unit == LogContract.DateUnit.WEEK) {
                WeekLogView(
                    data = state.data,
                    onEvent = onEvent
                )
            }
        }
        is LogContract.State.Loading -> {
            LoadingView()
        }
        LogContract.State.Error -> {
            ErrorView()
        }
    }
}

@Composable
private fun MonthLogContent(
    state: LogContract.State,
    isCurrentPage: Boolean,
    onEvent: (LogContract.Move) -> Unit
) {
    LaunchedEffect(isCurrentPage) {
        if (isCurrentPage) {
            onEvent(LogContract.Move.INIT)
        }
    }

    when (state) {
        is LogContract.State.Complete -> {
            if (state.unit == LogContract.DateUnit.MONTH) {
                MonthLogView(
                    data = state.data,
                    onEvent = onEvent
                )
            }
        }
        is LogContract.State.Loading -> {
            LoadingView()
        }
        LogContract.State.Error -> {
            ErrorView()
        }
    }
}

@Composable
private fun DayLogView(
    dayOfWater: DayOfWater,
    onEvent: (LogContract.Move) -> Unit,
    onEventByDate: (String) -> Unit,
    onShowAddDialog: () -> Unit,
    onShowEditDialog: (com.tkw.domain.model.Water) -> Unit,
    onRemoveDayAmount: (com.tkw.domain.model.Water) -> Unit
) {
    // 일간 로그 화면 구현
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 날짜 네비게이션
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onEvent(LogContract.Move.LEFT) }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "이전 날")
                    }

                    Text(
                        text = dayOfWater.date,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    IconButton(onClick = { onEvent(LogContract.Move.RIGHT) }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "다음 날")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 일일 누적 차트 (MPAndroidChart)
                AndroidView(
                    factory = { context ->
                        CustomBarChart(context).apply {
                            setXMinMax(0f, 24f)
                            setLimit(2000f)
                            setYUnit("ml")
                            setMarker(MarkerType.DAY)
                            setXValueFormat(arrayOf("0", "6", "12", "18", "24"))
                        }
                    },
                    update = { chart ->
                        val chartData = dayOfWater.getAccumulatedAmount().map { (hour, amount) ->
                            BarEntry(hour.toFloat(), amount.toFloat())
                        }
                        chart.setChartData(chartData)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 총 섭취량 표시
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "총 섭취량",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Text(
                        text = "${dayOfWater.getTotalIntakeByDate()}ml",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A90E2)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 추가 버튼
                Button(
                    onClick = onShowAddDialog,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("물 추가")
                }
            }
        }
    }
}

@Composable
private fun WeekLogView(
    data: DayOfWaterList,
    onEvent: (LogContract.Move) -> Unit
) {
    // 주간 로그 화면 구현
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onEvent(LogContract.Move.LEFT) }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "이전 주")
                    }

                    Text(
                        text = "주간 기록",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    IconButton(onClick = { onEvent(LogContract.Move.RIGHT) }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "다음 주")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 주간 차트 (7일간의 일일 총량, MPAndroidChart)
                AndroidView(
                    factory = { context ->
                        CustomLineChart(context).apply {
                            setXMinMax(1f, 7f)
                            setLimit(2000f)
                            setYUnit("ml")
                            setMarker(MarkerType.WEEK)
                        }
                    },
                    update = { chart ->
                        val chartData = data.list.mapIndexed { index, dayOfWater ->
                            Entry(index.toFloat(), dayOfWater.getTotalIntakeByDate().toFloat())
                        }
                        chart.setChartData(chartData)
                        val dateLabels = data.list.map { it.date.split("-").last() }.toTypedArray()
                        chart.setXValueFormat(dateLabels)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 주간 평균 표시
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "주간 평균",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )

                    val weeklyAverage = if (data.list.isNotEmpty()) {
                        data.list.sumOf { it.getTotalIntakeByDate() } / data.list.size
                    } else 0

                    Text(
                        text = "${weeklyAverage}ml",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A90E2)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthLogView(
    data: DayOfWaterList,
    onEvent: (LogContract.Move) -> Unit
) {
    // 월간 로그 화면 구현
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onEvent(LogContract.Move.LEFT) }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "이전 월")
                    }

                    Text(
                        text = "월간 기록",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    IconButton(onClick = { onEvent(LogContract.Move.RIGHT) }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "다음 월")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 월간 차트 (일별 총량, MPAndroidChart)
                AndroidView(
                    factory = { context ->
                        CustomLineChart(context).apply {
                            setXMinMax(1f, 7f)
                            setLimit(2000f)
                            setYUnit("ml")
                            setMarker(MarkerType.MONTH)
                        }
                    },
                    update = { chart ->
                        val chartData = data.list.mapIndexed { index, dayOfWater ->
                            Entry(index.toFloat(), dayOfWater.getTotalIntakeByDate().toFloat())
                        }
                        chart.setChartData(chartData)
                        val dateLabels = data.list.map { it.date.split("-").last() }.toTypedArray()
                        chart.setXValueFormat(dateLabels)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 월간 통계
                val monthlyTotal = data.list.sumOf { it.getTotalIntakeByDate() }
                val monthlyAverage = if (data.list.isNotEmpty()) monthlyTotal / data.list.size else 0
                val daysWithGoal = data.list.count { it.getTotalIntakeByDate() >= 2000 }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "월간 총량",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${monthlyTotal}ml",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF66BB6A)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "월간 평균",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${monthlyAverage}ml",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF66BB6A)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "목표 달성 일수",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${daysWithGoal}일",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF66BB6A)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyLogView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF90CAF9)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "기록이 없습니다",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF757575)
            )
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFF4A90E2)
        )
    }
}

@Composable
private fun ErrorView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "오류가 발생했습니다",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFE57373)
        )
    }
}

@Composable
private fun WaterEditDialog(
    water: com.tkw.domain.model.Water?,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var amount by remember { mutableStateOf(water?.amount?.toString() ?: "") }
    var time by remember { mutableStateOf(water?.dateTime ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (water != null) "물 섭취 수정" else "물 섭취 추가",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("양 (ml)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("시간") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountInt = amount.toIntOrNull() ?: 0
                    onConfirm(amountInt, time)
                }
            ) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun WaterLogScreenPreview() {
    WaterLogScreenContent()
}
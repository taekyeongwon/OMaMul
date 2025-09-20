package com.tkw.home

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tkw.alarm.WaterAlarmViewModel
import com.tkw.domain.model.Cup
import com.tkw.home.dialog.WaterIntakeDialog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCupManagement: () -> Unit = {},
    waterViewModel: WaterViewModel = hiltViewModel(),
    alarmViewModel: WaterAlarmViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val dayOfWater by waterViewModel.amountLiveData.collectAsState()
    val cupList by waterViewModel.cupListLiveData.collectAsState(initial = emptyList())
    var intakeGoal by remember { mutableStateOf(0) }

    // Dialog 상태
    val showWaterIntakeDialog: Boolean by waterViewModel.showWaterIntakeDialog.collectAsStateWithLifecycle()
    var selectedCupAmount by remember { mutableIntStateOf(250) }

    LaunchedEffect(Unit) {
        intakeGoal = waterViewModel.getIntakeAmount()
        waterViewModel.setToday()
    }

    var prevAmount by remember { mutableStateOf(0) }

    LaunchedEffect(dayOfWater) {
        dayOfWater?.let {
            val currentIntake = it.getTotalIntakeByDate()
            if (currentIntake >= intakeGoal && prevAmount < intakeGoal) {
                Toast.makeText(
                    context,
                    context.getString(com.tkw.ui.R.string.intake_complete),
                    Toast.LENGTH_SHORT
                ).show()
                alarmViewModel.saveReachedGoal(true)
            } else if (currentIntake < intakeGoal && prevAmount >= intakeGoal) {
                alarmViewModel.saveReachedGoal(false)
            }
            prevAmount = currentIntake
        }
    }

    LaunchedEffect(alarmViewModel.isReachedGoal.value) {
        coroutineScope.launch {
            val isNotificationEnabled = alarmViewModel.isNotificationAlarmEnabled().first()
            alarmViewModel.delayAllAlarm(alarmViewModel.isReachedGoal.value ?: false, isNotificationEnabled)
        }
    }

    // 배경 그라데이션
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE3F2FD),
            Color(0xFFBBDEFB),
            Color(0xFF90CAF9)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // 물 웨이브 애니메이션 배경
        WaterWaveBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                GlassmorphismTopBar(
                    title = stringResource(id = com.tkw.ui.R.string.title_main),
                    onShareClick = { /*TODO: 공유 기능*/ },
                    onMoreClick = { /*TODO: 더보기 메뉴*/ }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    WaterProgressWithWave(
                        currentIntake = dayOfWater?.getTotalIntakeByDate() ?: 0,
                        goal = intakeGoal
                    )
                }

                item {
                    GlassmorphismCard {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocalDrink,
                                    contentDescription = null,
                                    tint = Color(0xFF4A90E2)
                                )
                                Text(
                                    text = "물 선택",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1976D2)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            CupListWithAnimation(cupList, onAddClick = onNavigateToCupManagement) { cup ->
                                selectedCupAmount = cup.cupAmount
                                waterViewModel.showWaterIntakeDialog()
                            }
                        }
                    }
                }

                item {
                    AlarmInfoCard()
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }

    // Water Intake Dialog
    WaterIntakeDialog(
        isVisible = showWaterIntakeDialog,
        onDismiss = { waterViewModel.hideWaterIntakeDialog() },
        onConfirm = { amount ->
            waterViewModel.addCount(amount, com.tkw.common.util.DateTimeUtils.DateTime.getToday())
            waterViewModel.hideWaterIntakeDialog()
        },
        initialAmount = selectedCupAmount
    )
}

// 물 웨이브 배경 애니메이션
@Composable
fun WaterWaveBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.3f)
    ) {
        val width = size.width
        val height = size.height
        val waveHeight = 40f
        val wavelength = width / 2

        // 첫 번째 웨이브
        val path1 = Path().apply {
            moveTo(0f, height * 0.7f)
            for (x in 0..width.toInt() step 5) {
                val y = height * 0.7f + waveHeight * sin((x / wavelength * 2 * PI + waveOffset).toDouble()).toFloat()
                lineTo(x.toFloat(), y)
            }
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        // 두 번째 웨이브 (조금 다른 주기)
        val path2 = Path().apply {
            moveTo(0f, height * 0.8f)
            for (x in 0..width.toInt() step 5) {
                val y = height * 0.8f + waveHeight * 0.7f * sin((x / wavelength * 2 * PI + waveOffset * 0.8).toDouble()).toFloat()
                lineTo(x.toFloat(), y)
            }
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(path1, Color(0xFF64B5F6))
        drawPath(path2, Color(0xFF42A5F5))
    }
}

// 글래스모피즘 효과 카드
@Composable
fun GlassmorphismCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            content()
        }
    }
}

// 글래스모피즘 탑바
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassmorphismTopBar(
    title: String,
    onShareClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        actions = {
            IconButton(onClick = onShareClick) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "공유",
                    tint = Color.White
                )
            }
            IconButton(onClick = onMoreClick) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "더보기",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        )
    )
}

// 물 웨이브가 있는 진행률 표시기
@Composable
fun WaterProgressWithWave(currentIntake: Int, goal: Int) {
    val progress = if (goal > 0) (currentIntake.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "progress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )

    GlassmorphismCard {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(250.dp)
                .padding(20.dp)
        ) {
            // 배경 원
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = center
                val radius = size.minDimension / 2

                // 외곽 테두리
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = radius,
                    style = Stroke(width = 3.dp.toPx())
                )

                // 물 채우기 영역
                val waterHeight = radius * 2 * animatedProgress
                val waterY = center.y + radius - waterHeight

                if (animatedProgress > 0) {
                    // 물 웨이브 패스 생성
                    val path = Path().apply {
                        moveTo(center.x - radius, waterY)
                        for (x in -radius.toInt()..radius.toInt() step 5) {
                            val waveY = waterY + 15f * sin((x / 50f * 2 * PI + waveOffset).toDouble()).toFloat()
                            lineTo(center.x + x, waveY)
                        }
                        lineTo(center.x + radius, center.y + radius)
                        lineTo(center.x - radius, center.y + radius)
                        close()
                    }

                    // 클리핑하여 원 안에만 그리기
                    clipPath(Path().apply {
                        addOval(Rect(
                            center.x - radius + 3.dp.toPx(),
                            center.y - radius + 3.dp.toPx(),
                            center.x + radius - 3.dp.toPx(),
                            center.y + radius - 3.dp.toPx()
                        ))
                    }) {
                        drawPath(
                            path = path,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF4FC3F7),
                                    Color(0xFF29B6F6),
                                    Color(0xFF03A9F4)
                                )
                            )
                        )
                    }
                }

                // 진행률 텍스트 배경 원
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = radius * 0.6f
                )
            }

            // 진행률 텍스트
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${currentIntake}ml",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
                Text(
                    text = "목표 ${goal}ml",
                    fontSize = 16.sp,
                    color = Color(0xFF424242)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${(progress * 100).toInt()}% 달성",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4A90E2)
                )
            }
        }
    }
}

// 애니메이션이 적용된 컵 리스트
@Composable
fun CupListWithAnimation(cups: List<Cup>, onAddClick: () -> Unit, onCupClick: (Cup) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(cups) { cup ->
            CupCard(
                cup = cup,
                onClick = { onCupClick(cup) }
            )
        }
        item {
            AddCupCard(onClick = onAddClick)
        }
    }
}

// 개별 컵 카드
@Composable
fun CupCard(
    cup: Cup,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "cup_scale"
    )

    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .size(80.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.8f)
        ),
        border = BorderStroke(1.dp, Color(0xFF4A90E2).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 컵 아이콘 (간단한 도형으로 표현)
            Canvas(modifier = Modifier.size(24.dp)) {
                val cupPath = Path().apply {
                    moveTo(size.width * 0.2f, size.height * 0.3f)
                    lineTo(size.width * 0.8f, size.height * 0.3f)
                    lineTo(size.width * 0.7f, size.height * 0.9f)
                    lineTo(size.width * 0.3f, size.height * 0.9f)
                    close()
                }
                drawPath(
                    path = cupPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF64B5F6),
                            Color(0xFF1976D2)
                        )
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${cup.cupAmount}ml",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1976D2)
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

// 컵 추가 카드
@Composable
fun AddCupCard(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "add_cup_scale"
    )

    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .size(80.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4A90E2).copy(alpha = 0.1f)
        ),
        border = BorderStroke(2.dp, Color(0xFF4A90E2))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "컵 추가",
                tint = Color(0xFF4A90E2),
                modifier = Modifier.size(32.dp)
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

// 개선된 알람 정보 카드
@Composable
fun AlarmInfoCard() {
    GlassmorphismCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "다음 알람",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = "14:00",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                }
            }

            Button(
                onClick = { /*TODO: 알람 설정 화면으로 이동*/ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2).copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "설정",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Preview 함수들
@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreenContent(
            currentIntake = 1200,
            goal = 2000,
            cups = listOf(
                Cup("1", "작은 컵", 200),
                Cup("2", "중간 컵", 300),
                Cup("3", "큰 컵", 500)
            ),
            onCupClick = {},
            onAddCupClick = {},
            onShareClick = {},
            onMoreClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WaterProgressPreview() {
    MaterialTheme {
        WaterProgressWithWave(currentIntake = 1500, goal = 2000)
    }
}

@Preview(showBackground = true)
@Composable
private fun CupListPreview() {
    MaterialTheme {
        CupListWithAnimation(
            cups = listOf(
                Cup("1", "작은 컵", 200),
                Cup("2", "중간 컵", 300),
                Cup("3", "큰 컵", 500)
            ),
            onAddClick = {},
            onCupClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AlarmInfoCardPreview() {
    MaterialTheme {
        AlarmInfoCard()
    }
}

// Preview용 HomeScreen 컨텐츠 (ViewModel 없이)
@Composable
private fun HomeScreenContent(
    currentIntake: Int,
    goal: Int,
    cups: List<Cup>,
    onCupClick: (Cup) -> Unit,
    onAddCupClick: () -> Unit,
    onShareClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE3F2FD),
            Color(0xFFBBDEFB),
            Color(0xFF90CAF9)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        WaterWaveBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                GlassmorphismTopBar(
                    title = "오늘 마신 물",
                    onShareClick = onShareClick,
                    onMoreClick = onMoreClick
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    WaterProgressWithWave(
                        currentIntake = currentIntake,
                        goal = goal
                    )
                }

                item {
                    GlassmorphismCard {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocalDrink,
                                    contentDescription = null,
                                    tint = Color(0xFF4A90E2)
                                )
                                Text(
                                    text = "물 선택",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1976D2)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            CupListWithAnimation(cups, onAddCupClick, onCupClick)
                        }
                    }
                }

                item {
                    AlarmInfoCard()
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

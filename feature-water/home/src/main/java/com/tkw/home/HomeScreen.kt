package com.tkw.home

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import com.tkw.ui.icons.WaterIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tkw.alarm.WaterAlarmViewModel
import com.tkw.domain.model.Cup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    waterViewModel: WaterViewModel = hiltViewModel(),
    alarmViewModel: WaterAlarmViewModel = hiltViewModel(),
    onNavigateToRecord: () -> Unit = {},
    onNavigateToAlarm: () -> Unit = {},
    onNavigateToCup: () -> Unit = {},
    onNavigateToSetting: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val dayOfWater by waterViewModel.amountLiveData.collectAsState()
    val cupList by waterViewModel.cupListLiveData.collectAsState(initial = emptyList())
    var intakeGoal by remember { mutableStateOf(0) }

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

    val currentIntake = dayOfWater?.getTotalIntakeByDate() ?: 0
    val progress = if (intakeGoal > 0) (currentIntake.toFloat() / intakeGoal).coerceIn(0f, 1f) else 0f
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF87CEEB),
                        Color(0xFFE0F6FF)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with date and settings
            HeaderSection(onNavigateToSetting)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Main water progress circle
            WaterProgressCard(currentIntake, intakeGoal, progress)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quick actions
            QuickActionsSection(
                onRecordClick = onNavigateToRecord,
                onAlarmClick = onNavigateToAlarm
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Cup selection
            CupSelectionCard(
                cupList = cupList,
                onAddClick = onNavigateToCup,
                onCupClick = { cup ->
                    waterViewModel.addCount(cup.cupAmount, com.tkw.common.util.DateTimeUtils.DateTime.getToday())
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Today's summary
            TodaySummaryCard(dayOfWater)
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun HeaderSection(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "안녕하세요! 👋",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = SimpleDateFormat("MM월 dd일 EEEE", Locale.KOREAN).format(Date()),
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .background(
                    Color.White.copy(alpha = 0.2f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "설정",
                tint = Color.White
            )
        }
    }
}

@Composable
fun WaterProgressCard(currentIntake: Int, goal: Int, progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(220.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Background circle
                    drawArc(
                        color = Color(0xFFE3F7FF),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 20f, cap = StrokeCap.Round)
                    )
                    // Progress arc
                    drawArc(
                        color = Color(0xFF2196F3),
                        startAngle = -90f,
                        sweepAngle = 360 * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = 20f, cap = StrokeCap.Round)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        WaterIcons.LocalDrink,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color(0xFF2196F3)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${currentIntake}ml",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                    Text(
                        text = "/ ${goal}ml",
                        fontSize = 16.sp,
                        color = Color(0xFF757575)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(progress * 100).toInt()}% 달성",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (progress >= 1f) Color(0xFF4CAF50) else Color(0xFF2196F3)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    onRecordClick: () -> Unit,
    onAlarmClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onRecordClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    WaterIcons.Timeline,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "기록 보기",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
            }
        }
        
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onAlarmClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "알림 설정",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
            }
        }
    }
}

@Composable
fun CupSelectionCard(
    cupList: List<Cup>,
    onAddClick: () -> Unit,
    onCupClick: (Cup) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🥤 물 마시기",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cupList) { cup ->
                    Card(
                        modifier = Modifier
                            .size(100.dp)
                            .clickable { onCupClick(cup) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF3F9FF)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                WaterIcons.LocalDrink,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFF2196F3)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${cup.cupAmount}ml",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF212121)
                            )
                        }
                    }
                }
                
                item {
                    Card(
                        modifier = Modifier
                            .size(100.dp)
                            .clickable { onAddClick() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "컵 추가",
                                    modifier = Modifier.size(32.dp),
                                    tint = Color(0xFF757575)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "추가",
                                    fontSize = 12.sp,
                                    color = Color(0xFF757575)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodaySummaryCard(dayOfWater: com.tkw.domain.model.DayOfWater?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "📊 오늘의 요약",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${dayOfWater?.dayOfList?.size ?: 0}회",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                    Text(
                        text = "마신 횟수",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${dayOfWater?.getTotalIntakeByDate() ?: 0}ml",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        text = "총 섭취량",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val lastIntake = dayOfWater?.dayOfList?.lastOrNull()?.dateTime
                    val timeText = if (lastIntake != null && lastIntake.isNotBlank()) {
                        try {
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val date = inputFormat.parse(lastIntake)
                            SimpleDateFormat("HH:mm", Locale.KOREAN).format(date ?: Date())
                        } catch (e: Exception) {
                            "-"
                        }
                    } else {
                        "-"
                    }
                    Text(
                        text = timeText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )
                    Text(
                        text = "마지막 섭취",
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }
            }
        }
    }
}

// Preview Functions
@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun HomeScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF87CEEB),
                        Color(0xFFE0F6FF)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderSection(onSettingsClick = {})
            Spacer(modifier = Modifier.height(24.dp))
            WaterProgressCard(currentIntake = 1200, goal = 2000, progress = 0.6f)
            Spacer(modifier = Modifier.height(24.dp))
            QuickActionsSection(
                onRecordClick = {},
                onAlarmClick = {}
            )
            Spacer(modifier = Modifier.height(24.dp))
            CupSelectionCard(
                cupList = listOf(
                    Cup(cupId = "1", cupName = "물병", cupAmount = 500),
                    Cup(cupId = "2", cupName = "머그컵", cupAmount = 250),
                    Cup(cupId = "3", cupName = "텀블러", cupAmount = 350)
                ),
                onAddClick = {},
                onCupClick = {}
            )
            Spacer(modifier = Modifier.height(24.dp))
            TodaySummaryCard(null)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WaterProgressCardPreview() {
    WaterProgressCard(currentIntake = 1500, goal = 2000, progress = 0.75f)
}

@Preview(showBackground = true)
@Composable
fun HeaderSectionPreview() {
    HeaderSection(onSettingsClick = {})
}

@Preview(showBackground = true)
@Composable
fun QuickActionsSectionPreview() {
    QuickActionsSection(
        onRecordClick = {},
        onAlarmClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun CupSelectionCardPreview() {
    CupSelectionCard(
        cupList = listOf(
            Cup(cupId = "1", cupName = "물병", cupAmount = 500),
            Cup(cupId = "2", cupName = "머그컵", cupAmount = 250)
        ),
        onAddClick = {},
        onCupClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun TodaySummaryCardPreview() {
    TodaySummaryCard(null)
}
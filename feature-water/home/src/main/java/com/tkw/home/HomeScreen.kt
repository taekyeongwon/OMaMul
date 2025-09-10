package com.tkw.home

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tkw.alarm.WaterAlarmViewModel
import com.tkw.domain.model.Cup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    waterViewModel: WaterViewModel = hiltViewModel(),
    alarmViewModel: WaterAlarmViewModel = hiltViewModel()
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = com.tkw.ui.R.string.title_main)) },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE3F2FD))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            WaterProgress(dayOfWater?.getTotalIntakeByDate() ?: 0, intakeGoal)
            Spacer(modifier = Modifier.height(32.dp))
            CupList(cupList, onAddClick = { /*TODO*/ }) { cup ->
                waterViewModel.addCount(cup.cupAmount, com.tkw.common.util.DateTimeUtils.DateTime.getToday())
            }
            Spacer(modifier = Modifier.height(32.dp))
            AlarmInfo()
        }
    }
}

@Composable
fun WaterProgress(currentIntake: Int, goal: Int) {
    val progress = if (goal > 0) (currentIntake.toFloat() / goal).coerceIn(0f, 1f) else 0f

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color(0xFFE3F2FD),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 15f, cap = StrokeCap.Round),
                size = size
            )
            drawArc(
                color = Color(0xFF4A90E2),
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(width = 15f, cap = StrokeCap.Round),
                size = size
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${currentIntake}ml 달성",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            Text(
                text = "목표 ${goal}ml",
                fontSize = 16.sp,
                color = Color(0xFF757575)
            )
        }
    }
}

@Composable
fun CupList(cups: List<Cup>, onAddClick: () -> Unit, onCupClick: (Cup) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(cups) {
            Card(
                modifier = Modifier.size(100.dp),
                onClick = { onCupClick(it) }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(text = "${it.cupAmount}ml")
                }
            }
        }
        item {
            Card(
                modifier = Modifier.size(100.dp),
                onClick = onAddClick
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(Icons.Default.Add, contentDescription = "Add Cup")
                }
            }
        }
    }
}

@Composable
fun AlarmInfo() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "다음 알람: 14:00")
        IconButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Default.Notifications, contentDescription = "Alarm")
        }
    }
}

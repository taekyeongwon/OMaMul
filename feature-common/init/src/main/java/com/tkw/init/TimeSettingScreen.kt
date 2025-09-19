package com.tkw.init

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tkw.common.util.DateTimeUtils
import com.tkw.ui.R as CoreR
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSettingScreen(
    onNavigateNext: () -> Unit,
    viewModel: InitViewModel = hiltViewModel()
) {
    val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(null)

    var wakeupTime by remember { mutableStateOf(LocalTime.of(8, 0)) }
    var sleepTime by remember { mutableStateOf(LocalTime.of(23, 0)) }
    var showWakeupTimePicker by remember { mutableStateOf(false) }
    var showSleepTimePicker by remember { mutableStateOf(false) }

    // Side Effect 처리
    LaunchedEffect(sideEffect) {
        when (val effect = sideEffect) {
            is InitContract.SideEffect.OnMoveNext -> {
                onNavigateNext()
            }
            is InitContract.SideEffect.InitTimePicker -> {
                if (effect.flag) {
                    showWakeupTimePicker = true
                } else {
                    showSleepTimePicker = true
                }
            }
            else -> {}
        }
    }

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
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 진행 표시기
            ProgressIndicator(currentStep = 2)

            Spacer(modifier = Modifier.height(32.dp))

            // 제목
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(CoreR.string.init_time_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(CoreR.string.init_time_subtitle),
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 시간 설정 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // 기상 시간
                    TimeSettingItem(
                        title = stringResource(CoreR.string.wakeup_time),
                        time = wakeupTime,
                        onClick = {
                            viewModel.setEvent(InitContract.Event.ClickWakeUpTimePicker)
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 취침 시간
                    TimeSettingItem(
                        title = stringResource(CoreR.string.sleep_time),
                        time = sleepTime,
                        onClick = {
                            viewModel.setEvent(InitContract.Event.ClickSleepTimePicker)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 다음 버튼
            Button(
                onClick = {
                    val wakeTimeString = DateTimeUtils.Time.getFormat(wakeupTime.hour, wakeupTime.minute)
                    val sleepTimeString = DateTimeUtils.Time.getFormat(sleepTime.hour, sleepTime.minute)
                    viewModel.setEvent(InitContract.Event.SaveTime(wakeTimeString, sleepTimeString))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(CoreR.string.next),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Time Picker Dialogs
    if (showWakeupTimePicker) {
        TimePickerDialog(
            onTimeSelected = { hour, minute ->
                wakeupTime = LocalTime.of(hour, minute)
                showWakeupTimePicker = false
            },
            onDismiss = {
                showWakeupTimePicker = false
            },
            initialTime = wakeupTime
        )
    }

    if (showSleepTimePicker) {
        TimePickerDialog(
            onTimeSelected = { hour, minute ->
                sleepTime = LocalTime.of(hour, minute)
                showSleepTimePicker = false
            },
            onDismiss = {
                showSleepTimePicker = false
            },
            initialTime = sleepTime
        )
    }
}

@Composable
private fun ProgressIndicator(currentStep: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { step ->
            val isActive = step + 1 <= currentStep

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) Color.White else Color.Gray.copy(alpha = 0.5f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${step + 1}",
                    color = if (isActive) Color(0xFF2196F3) else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            if (step < 2) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(Color.Gray.copy(alpha = 0.5f))
                )
            }
        }
    }
}

@Composable
private fun TimeSettingItem(
    title: String,
    time: LocalTime,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = DateTimeUtils.Time.getFormat(time.hour, time.minute),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
    initialTime: LocalTime
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                }
            ) {
                Text("확인", color = Color(0xFF2196F3))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = Color(0xFF666666))
            }
        },
        text = {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialSelectedContentColor = Color.White,
                    clockDialUnselectedContentColor = Color(0xFF666666),
                    selectorColor = Color(0xFF2196F3)
                )
            )
        }
    )
}

// Preview Functions
@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun TimeSettingScreenPreview() {
    TimeSettingScreenContent(
        onNavigateNext = {}
    )
}

@Composable
private fun TimeSettingScreenContent(
    onNavigateNext: () -> Unit
) {
    var wakeupTime by remember { mutableStateOf(LocalTime.of(8, 0)) }
    var sleepTime by remember { mutableStateOf(LocalTime.of(23, 0)) }
    var showWakeupTimePicker by remember { mutableStateOf(false) }
    var showSleepTimePicker by remember { mutableStateOf(false) }

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
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 진행 표시기
            ProgressIndicator(currentStep = 2)

            Spacer(modifier = Modifier.height(32.dp))

            // 제목
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(CoreR.string.init_time_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(CoreR.string.init_time_subtitle),
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 시간 설정 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // 기상 시간
                    TimeSettingItem(
                        title = stringResource(CoreR.string.wakeup_time),
                        time = wakeupTime,
                        onClick = { showWakeupTimePicker = true }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 취침 시간
                    TimeSettingItem(
                        title = stringResource(CoreR.string.sleep_time),
                        time = sleepTime,
                        onClick = { showSleepTimePicker = true }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 다음 버튼
            Button(
                onClick = onNavigateNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(CoreR.string.next),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Time Picker Dialogs
    if (showWakeupTimePicker) {
        TimePickerDialog(
            onTimeSelected = { hour, minute ->
                wakeupTime = LocalTime.of(hour, minute)
                showWakeupTimePicker = false
            },
            onDismiss = {
                showWakeupTimePicker = false
            },
            initialTime = wakeupTime
        )
    }

    if (showSleepTimePicker) {
        TimePickerDialog(
            onTimeSelected = { hour, minute ->
                sleepTime = LocalTime.of(hour, minute)
                showSleepTimePicker = false
            },
            onDismiss = {
                showSleepTimePicker = false
            },
            initialTime = sleepTime
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimeSettingItemPreview() {
    TimeSettingItem(
        title = "기상 시간",
        time = LocalTime.of(8, 0),
        onClick = {}
    )
}
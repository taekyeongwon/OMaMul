package com.tkw.alarm.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmTimeBottomSheet(
    isVisible: Boolean,
    startTime: LocalTime = LocalTime.of(9, 0),
    endTime: LocalTime = LocalTime.of(21, 0),
    onDismiss: () -> Unit,
    onTimeSelect: (startTime: LocalTime, endTime: LocalTime) -> Unit
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            AlarmTimeBottomSheetContent(
                startTime = startTime,
                endTime = endTime,
                onDismiss = onDismiss,
                onTimeSelect = onTimeSelect
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlarmTimeBottomSheetContent(
    startTime: LocalTime = LocalTime.of(9, 0),
    endTime: LocalTime = LocalTime.of(21, 0),
    onDismiss: () -> Unit = {},
    onTimeSelect: (LocalTime, LocalTime) -> Unit = { _, _ -> }
) {
    var selectedStartTime by remember { mutableStateOf(startTime) }
    var selectedEndTime by remember { mutableStateOf(endTime) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE3F2FD),
                        Color.White
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 핸들
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 헤더
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = "시간 설정",
                tint = Color(0xFF4A90E2),
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "알람 시간 설정",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 시작 시간
        TimeSelectionCard(
            title = "시작 시간",
            subtitle = "알람 시작",
            time = selectedStartTime,
            onClick = { showStartTimePicker = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 종료 시간
        TimeSelectionCard(
            title = "종료 시간",
            subtitle = "알람 종료",
            time = selectedEndTime,
            onClick = { showEndTimePicker = true }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 버튼들
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF666666)
                )
            ) {
                Text("취소")
            }

            Button(
                onClick = {
                    onTimeSelect(selectedStartTime, selectedEndTime)
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                )
            ) {
                Text("확인")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // TimePicker for Start Time
    if (showStartTimePicker) {
        TimePickerDialog(
            onTimeSelected = { time ->
                selectedStartTime = time
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false },
            initialTime = selectedStartTime
        )
    }

    // TimePicker for End Time
    if (showEndTimePicker) {
        TimePickerDialog(
            onTimeSelected = { time ->
                selectedEndTime = time
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false },
            initialTime = selectedEndTime
        )
    }
}

@Composable
private fun TimeSelectionCard(
    title: String,
    subtitle: String,
    time: LocalTime,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.8f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4A90E2).copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1976D2)
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }

            Text(
                text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A90E2)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onTimeSelected: (LocalTime) -> Unit,
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
                    onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
                }
            ) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun AlarmTimeBottomSheetPreview() {
    AlarmTimeBottomSheetContent(
        startTime = LocalTime.of(9, 0),
        endTime = LocalTime.of(21, 0),
        onDismiss = {},
        onTimeSelect = { _, _ -> }
    )
}
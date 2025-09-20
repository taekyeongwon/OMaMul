package com.tkw.alarm.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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

data class CustomAlarmTime(
    val id: String = "",
    val time: LocalTime,
    val isEnabled: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAlarmBottomSheet(
    isVisible: Boolean,
    alarmTimes: List<CustomAlarmTime> = emptyList(),
    onDismiss: () -> Unit,
    onSaveAlarms: (List<CustomAlarmTime>) -> Unit
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            CustomAlarmBottomSheetContent(
                alarmTimes = alarmTimes,
                onDismiss = onDismiss,
                onSaveAlarms = onSaveAlarms
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomAlarmBottomSheetContent(
    alarmTimes: List<CustomAlarmTime> = emptyList(),
    onDismiss: () -> Unit = {},
    onSaveAlarms: (List<CustomAlarmTime>) -> Unit = {}
) {
    var localAlarmTimes by remember { mutableStateOf(alarmTimes) }
    var showTimePicker by remember { mutableStateOf(false) }
    var editingAlarmId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE3F2FD),
                        Color.White
                    )
                )
            )
            .padding(24.dp)
    ) {
        // 핸들
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 헤더
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = "커스텀 알람",
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "커스텀 알람 설정",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }

            IconButton(
                onClick = {
                    editingAlarmId = null
                    showTimePicker = true
                }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "알람 추가",
                    tint = Color(0xFF4A90E2)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 알람 목록
        if (localAlarmTimes.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F9FA)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFFBDBDBD),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "등록된 알람이 없습니다",
                        fontSize = 16.sp,
                        color = Color(0xFF757575)
                    )
                    Text(
                        text = "+ 버튼을 눌러 알람을 추가해보세요",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(localAlarmTimes) { alarmTime ->
                    CustomAlarmTimeCard(
                        alarmTime = alarmTime,
                        onToggle = { enabled ->
                            localAlarmTimes = localAlarmTimes.map { alarm ->
                                if (alarm.id == alarmTime.id) {
                                    alarm.copy(isEnabled = enabled)
                                } else alarm
                            }
                        },
                        onEdit = {
                            editingAlarmId = alarmTime.id
                            showTimePicker = true
                        },
                        onDelete = {
                            localAlarmTimes = localAlarmTimes.filter { it.id != alarmTime.id }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                    onSaveAlarms(localAlarmTimes)
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                )
            ) {
                Text("저장")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // TimePicker
    if (showTimePicker) {
        val currentTime = if (editingAlarmId != null) {
            localAlarmTimes.find { it.id == editingAlarmId }?.time ?: LocalTime.now()
        } else {
            LocalTime.now()
        }

        TimePickerDialog(
            onTimeSelected = { selectedTime ->
                if (editingAlarmId != null) {
                    // 기존 알람 수정
                    localAlarmTimes = localAlarmTimes.map { alarm ->
                        if (alarm.id == editingAlarmId) {
                            alarm.copy(time = selectedTime)
                        } else alarm
                    }
                } else {
                    // 새 알람 추가
                    val newAlarm = CustomAlarmTime(
                        id = System.currentTimeMillis().toString(),
                        time = selectedTime,
                        isEnabled = true
                    )
                    localAlarmTimes = localAlarmTimes + newAlarm
                }
                showTimePicker = false
                editingAlarmId = null
            },
            onDismiss = {
                showTimePicker = false
                editingAlarmId = null
            },
            initialTime = currentTime
        )
    }
}

@Composable
private fun CustomAlarmTimeCard(
    alarmTime: CustomAlarmTime,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (alarmTime.isEnabled) Color.White else Color(0xFFF5F5F5)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (alarmTime.isEnabled) Color(0xFF4A90E2).copy(alpha = 0.3f) else Color(0xFFE0E0E0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = alarmTime.time.format(DateTimeFormatter.ofPattern("HH:mm")),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (alarmTime.isEnabled) Color(0xFF1976D2) else Color(0xFF9E9E9E)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Switch(
                    checked = alarmTime.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4A90E2),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFBDBDBD)
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = "수정",
                        tint = Color(0xFF666666),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = Color(0xFFFF5722),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
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
fun CustomAlarmBottomSheetPreview() {
    CustomAlarmBottomSheetContent(
        alarmTimes = listOf(
            CustomAlarmTime("1", LocalTime.of(9, 0), true),
            CustomAlarmTime("2", LocalTime.of(12, 30), true),
            CustomAlarmTime("3", LocalTime.of(18, 0), false)
        ),
        onDismiss = {},
        onSaveAlarms = {}
    )
}
package com.tkw.alarm

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmModeSetting
import com.tkw.ui.R
import java.time.DayOfWeek

@Composable
fun AlarmModeScreen(
    onNavigateBack: () -> Unit,
    viewModel: WaterAlarmViewModel = hiltViewModel()
) {
    val alarmMode by viewModel.alarmModeStateFlow.collectAsStateWithLifecycle()
    val isAlarmEnabled by viewModel.isAlarmEnabledStateFlow.collectAsStateWithLifecycle()
    val isNotificationEnabled by viewModel.isNotificationEnabledStateFlow.collectAsStateWithLifecycle()
    val remainTimeContent by viewModel.remainTimeStateFlow.collectAsStateWithLifecycle()

    // 모드별 데이터
    val periodModeSetting by viewModel.periodModeSettingsStateFlow.collectAsStateWithLifecycle()
    val customAlarmList by viewModel.customAlarmListStateFlow.collectAsStateWithLifecycle()

    var showModeDialog by remember { mutableStateOf(false) }

    AlarmModeScreenContent(
        currentMode = alarmMode ?: AlarmMode.PERIOD,
        isAlarmEnabled = isAlarmEnabled && isNotificationEnabled,
        remainTimeContent = remainTimeContent,
        periodModeSetting = periodModeSetting,
        customAlarmList = customAlarmList?.alarmList ?: emptyList(),
        onModeChange = { mode ->
            viewModel.updateAlarmMode(mode)
        },
        onModeDialogShow = {
            showModeDialog = true
        },
        onPeriodSettingUpdate = { setting ->
            viewModel.setPeriodAlarm(setting)
            viewModel.updateAlarmModeSetting(setting)
        },
        onCustomAlarmAdd = { alarm ->
            viewModel.setCustomAlarm(alarm)
        },
        onCustomAlarmDelete = { alarmList ->
            viewModel.deleteAlarm(alarmList)
        },
        onCustomAlarmUpdate = { alarmList ->
            viewModel.updateList(alarmList)
        }
    )

    if (showModeDialog) {
        AlarmModeSelectionDialog(
            currentMode = alarmMode ?: AlarmMode.PERIOD,
            onModeSelected = { mode ->
                showModeDialog = false
                viewModel.updateAlarmMode(mode)
            },
            onDismiss = {
                showModeDialog = false
            }
        )
    }
}

@Composable
private fun AlarmModeScreenContent(
    currentMode: AlarmMode = AlarmMode.PERIOD,
    isAlarmEnabled: Boolean = false,
    remainTimeContent: String = "",
    periodModeSetting: AlarmModeSetting = AlarmModeSetting(),
    customAlarmList: List<Alarm> = emptyList(),
    onModeChange: (AlarmMode) -> Unit = {},
    onModeDialogShow: () -> Unit = {},
    onPeriodSettingUpdate: (AlarmModeSetting) -> Unit = {},
    onCustomAlarmAdd: (Alarm) -> Unit = {},
    onCustomAlarmDelete: (List<Alarm>) -> Unit = {},
    onCustomAlarmUpdate: (List<Alarm>) -> Unit = {}
) {
    var localMode by remember { mutableStateOf(currentMode) }
    var localRemainTime by remember { mutableStateOf(remainTimeContent) }

    // Preview용 로컬 상태 업데이트
    LaunchedEffect(currentMode) { localMode = currentMode }
    LaunchedEffect(remainTimeContent) { localRemainTime = remainTimeContent }

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
            .padding(16.dp)
    ) {
        // 상단 알람 상태 표시
        AlarmStatusCard(
            isEnabled = isAlarmEnabled,
            remainTimeContent = localRemainTime.ifEmpty {
                if (isAlarmEnabled) "알람이 설정되어 있습니다" else "알람이 비활성화되어 있습니다"
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 알람 모드 선택
        AlarmModeSelector(
            currentMode = localMode,
            onModeClick = {
                onModeDialogShow()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 모드별 내용
        when (localMode) {
            AlarmMode.PERIOD -> {
                PeriodAlarmSection(
                    setting = periodModeSetting,
                    onSettingUpdate = onPeriodSettingUpdate
                )
            }
            AlarmMode.CUSTOM -> {
                CustomAlarmSection(
                    alarmList = customAlarmList,
                    onAlarmAdd = onCustomAlarmAdd,
                    onAlarmDelete = onCustomAlarmDelete,
                    onAlarmUpdate = onCustomAlarmUpdate
                )
            }
        }
    }
}

@Composable
private fun AlarmStatusCard(
    isEnabled: Boolean,
    remainTimeContent: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) Color(0xFF4A90E2).copy(alpha = 0.1f)
                            else Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isEnabled) Icons.Default.Alarm else Icons.Default.AlarmOff,
                contentDescription = null,
                tint = if (isEnabled) Color(0xFF4A90E2) else Color(0xFF757575),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = stringResource(R.string.alarm_detail_mode_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isEnabled) Color(0xFF1565C0) else Color(0xFF757575)
                    )
                )
                Text(
                    text = remainTimeContent,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}

@Composable
private fun AlarmModeSelector(
    currentMode: AlarmMode,
    onModeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onModeClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.alarm_mode_title),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = when(currentMode) {
                            AlarmMode.PERIOD -> stringResource(R.string.alarm_mode_period)
                            AlarmMode.CUSTOM -> stringResource(R.string.alarm_mode_custom)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF757575)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF757575),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun PeriodAlarmSection(
    setting: AlarmModeSetting,
    onSettingUpdate: (AlarmModeSetting) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "주기 알람 설정",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 주간 선택
            Text(
                text = "알람 요일",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            WeekSelector(
                selectedDays = setting.selectedDate,
                onDaysSelected = { days ->
                    onSettingUpdate(setting.copy(selectedDate = days))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 알람 시간 범위
            PeriodTimeSetting(
                startTime = setting.startTime,
                endTime = setting.endTime,
                interval = setting.interval,
                onTimeRangeChange = { start, end ->
                    onSettingUpdate(setting.copy(startTime = start, endTime = end))
                },
                onIntervalChange = { interval ->
                    onSettingUpdate(setting.copy(interval = interval))
                }
            )
        }
    }
}

@Composable
private fun CustomAlarmSection(
    alarmList: List<Alarm>,
    onAlarmAdd: (Alarm) -> Unit,
    onAlarmDelete: (List<Alarm>) -> Unit,
    onAlarmUpdate: (List<Alarm>) -> Unit
) {
    var editMode by remember { mutableStateOf(false) }
    var checkedAlarms by remember { mutableStateOf<Set<String>>(emptySet()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "커스텀 알람 설정",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                )

                if (alarmList.isNotEmpty() && !editMode) {
                    IconButton(
                        onClick = { editMode = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = Color(0xFF4A90E2)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (alarmList.isEmpty()) {
                // 빈 상태
                EmptyAlarmState(
                    onAddAlarm = {
                        val newAlarm = Alarm(
                            alarmId = "alarm_${System.currentTimeMillis()}",
                            startTime = System.currentTimeMillis(),
                            weekList = emptyList(),
                            enabled = true
                        )
                        onAlarmAdd(newAlarm)
                    }
                )
            } else {
                // 알람 리스트
                LazyColumn(
                    modifier = Modifier.weight(1f, false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = alarmList,
                        key = { _, alarm -> alarm.alarmId }
                    ) { index, alarm ->
                        CustomAlarmItem(
                            alarm = alarm,
                            isEditMode = editMode,
                            isChecked = checkedAlarms.contains(alarm.alarmId),
                            onChecked = { checked ->
                                checkedAlarms = if (checked) {
                                    checkedAlarms + alarm.alarmId
                                } else {
                                    checkedAlarms - alarm.alarmId
                                }
                            },
                            onAlarmToggle = { enabled ->
                                val updatedAlarm = alarm.copy(enabled = enabled)
                                onAlarmUpdate(alarmList.map {
                                    if (it.alarmId == alarm.alarmId) updatedAlarm else it
                                })
                            },
                            onEdit = {
                                // 알람 편집 다이얼로그 표시
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 하단 버튼들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (editMode) Arrangement.SpaceBetween else Arrangement.Center
                ) {
                    if (editMode) {
                        // 삭제 버튼
                        if (checkedAlarms.isNotEmpty()) {
                            Button(
                                onClick = {
                                    val alarmsToDelete = alarmList.filter { checkedAlarms.contains(it.alarmId) }
                                    onAlarmDelete(alarmsToDelete)
                                    checkedAlarms = emptySet()
                                    editMode = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE57373)
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.delete))
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // 완료 버튼
                        Button(
                            onClick = {
                                editMode = false
                                checkedAlarms = emptySet()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.complete))
                        }
                    } else {
                        // 추가 버튼
                        Button(
                            onClick = {
                                val newAlarm = Alarm(
                                    alarmId = "alarm_${System.currentTimeMillis()}",
                                    startTime = System.currentTimeMillis(),
                                    weekList = emptyList(),
                                    enabled = true
                                )
                                onAlarmAdd(newAlarm)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4A90E2)
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.alarm_custom_add))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekSelector(
    selectedDays: List<DayOfWeek>,
    onDaysSelected: (List<DayOfWeek>) -> Unit
) {
    val weekDays = listOf("일", "월", "화", "수", "목", "금", "토")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDays.forEachIndexed { index, day ->
            val dayOfWeek = DayOfWeek.of(if (index == 0) 7 else index)
            val isSelected = selectedDays.contains(dayOfWeek)

            FilterChip(
                onClick = {
                    val newDays = if (isSelected) {
                        selectedDays - dayOfWeek
                    } else {
                        selectedDays + dayOfWeek
                    }
                    onDaysSelected(newDays.sortedBy { it.value })
                },
                label = { Text(day) },
                selected = isSelected,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4A90E2),
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun PeriodTimeSetting(
    startTime: Long,
    endTime: Long,
    interval: Int,
    onTimeRangeChange: (Long, Long) -> Unit,
    onIntervalChange: (Int) -> Unit
) {
    // Long을 시간 문자열로 변환하는 헬퍼 함수
    fun formatTime(timeMillis: Long): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timeMillis
        return String.format("%02d:%02d", calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE))
    }

    Column {
        Text(
            text = "시간 설정",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 시간 범위 설정
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 시작 시간
            TimePickerCard(
                label = "시작 시간",
                time = formatTime(startTime),
                onTimeSelect = { newTime ->
                    val timeParts = newTime.split(":").map { it.toInt() }
                    val calendar = java.util.Calendar.getInstance()
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, timeParts[0])
                    calendar.set(java.util.Calendar.MINUTE, timeParts[1])
                    calendar.set(java.util.Calendar.SECOND, 0)
                    calendar.set(java.util.Calendar.MILLISECOND, 0)
                    onTimeRangeChange(calendar.timeInMillis, endTime)
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 종료 시간
            TimePickerCard(
                label = "종료 시간",
                time = formatTime(endTime),
                onTimeSelect = { newTime ->
                    val timeParts = newTime.split(":").map { it.toInt() }
                    val calendar = java.util.Calendar.getInstance()
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, timeParts[0])
                    calendar.set(java.util.Calendar.MINUTE, timeParts[1])
                    calendar.set(java.util.Calendar.SECOND, 0)
                    calendar.set(java.util.Calendar.MILLISECOND, 0)
                    onTimeRangeChange(startTime, calendar.timeInMillis)
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 간격 설정
        Text(
            text = "알람 간격",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        IntervalSelector(
            selectedInterval = interval,
            onIntervalChange = onIntervalChange
        )
    }
}

@Composable
private fun TimePickerCard(
    label: String,
    time: String,
    onTimeSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .clickable { showTimePicker = true },
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF757575)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = time,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
            )
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialTime = time,
            onTimeSelected = { selectedTime ->
                onTimeSelect(selectedTime)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@Composable
private fun IntervalSelector(
    selectedInterval: Int,
    onIntervalChange: (Int) -> Unit
) {
    val intervals = listOf(900, 1800, 2700, 3600, 5400, 7200) // 초 단위: 15분, 30분, 45분, 1시간, 1.5시간, 2시간

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(intervals) { interval ->
            val displayText = when {
                interval < 3600 -> "${interval / 60}분"
                interval == 3600 -> "1시간"
                interval == 5400 -> "1.5시간"
                else -> "${interval / 3600}시간"
            }

            FilterChip(
                onClick = { onIntervalChange(interval) },
                label = {
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                selected = selectedInterval == interval,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4A90E2),
                    selectedLabelColor = Color.White,
                    containerColor = Color.White.copy(alpha = 0.7f),
                    labelColor = Color(0xFF1565C0)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val timeParts = initialTime.split(":").map { it.toIntOrNull() ?: 0 }
    val timePickerState = rememberTimePickerState(
        initialHour = timeParts.getOrNull(0) ?: 9,
        initialMinute = timeParts.getOrNull(1) ?: 0,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "시간 선택",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = Color(0xFFE3F2FD),
                    selectorColor = Color(0xFF4A90E2)
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedTime = String.format(
                        "%02d:%02d",
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    onTimeSelected(selectedTime)
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

@Composable
private fun EmptyAlarmState(
    onAddAlarm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AlarmAdd,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color(0xFF90CAF9)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.alarm_custom_empty_title),
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF757575)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddAlarm,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A90E2)
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.alarm_custom_add))
        }
    }
}

@Composable
private fun CustomAlarmItem(
    alarm: Alarm,
    isEditMode: Boolean,
    isChecked: Boolean,
    onChecked: (Boolean) -> Unit,
    onAlarmToggle: (Boolean) -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditMode) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = onChecked,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF4A90E2)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "알람 시간", // TODO: 실제 시간 포맷
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = "매일", // TODO: 실제 요일 표시
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF757575)
                )
            }

            if (!isEditMode) {
                Switch(
                    checked = alarm.enabled,
                    onCheckedChange = onAlarmToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF4A90E2),
                        checkedTrackColor = Color(0xFF4A90E2).copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

@Composable
private fun AlarmModeSelectionDialog(
    currentMode: AlarmMode,
    onModeSelected: (AlarmMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.alarm_mode_title))
        },
        text = {
            Column {
                AlarmMode.values().forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onModeSelected(mode) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentMode == mode,
                            onClick = { onModeSelected(mode) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when(mode) {
                                AlarmMode.PERIOD -> stringResource(R.string.alarm_mode_period)
                                AlarmMode.CUSTOM -> stringResource(R.string.alarm_mode_custom)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun AlarmModeScreenPreview() {
    AlarmModeScreenContent(
        currentMode = AlarmMode.PERIOD,
        isAlarmEnabled = true,
        remainTimeContent = "2시간 30분 후에 알람이 울립니다",
        periodModeSetting = AlarmModeSetting(
            selectedDate = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
            startTime = System.currentTimeMillis(),
            endTime = System.currentTimeMillis() + 8 * 60 * 60 * 1000,
            interval = 3600
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun AlarmModeScreenCustomPreview() {
    val sampleAlarms = listOf(
        Alarm("1", System.currentTimeMillis(), listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY), true),
        Alarm("2", System.currentTimeMillis(), listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), false)
    )

    AlarmModeScreenContent(
        currentMode = AlarmMode.CUSTOM,
        isAlarmEnabled = true,
        customAlarmList = sampleAlarms
    )
}
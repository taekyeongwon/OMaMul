package com.tkw.alarm

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tkw.domain.model.AlarmEtcSettings
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.RingTone
import com.tkw.ui.R

@Composable
fun AlarmSettingScreen(
    onNavigateToMode: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: WaterAlarmViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // StateFlow로 변환된 데이터들
    val alarmSettings by viewModel.alarmSettingsStateFlow.collectAsStateWithLifecycle()
    val isAlarmEnabled by viewModel.isAlarmEnabledStateFlow.collectAsStateWithLifecycle()
    val isNotificationEnabled by viewModel.isNotificationEnabledStateFlow.collectAsStateWithLifecycle()

    // 로컬 상태로 권한 관련 처리
    var showPermissionDialog by remember { mutableStateOf(false) }

    AlarmSettingScreenContent(
        isAlarmEnabled = isAlarmEnabled,
        isNotificationEnabled = isNotificationEnabled,
        ringtoneName = alarmSettings?.ringToneMode?.getCurrentMode()?.let { getRingtoneName(it) } ?: "",
        alarmModeName = alarmSettings?.alarmMode?.let { getAlarmModeName(it) } ?: "",
        etcSettings = alarmSettings?.etcSetting ?: AlarmEtcSettings(),
        onAlarmToggle = { enabled ->
            if (enabled) {
                if (!isNotificationEnabled) {
                    showPermissionDialog = true
                } else {
                    viewModel.setAlarmEnabledCompose(enabled)
                    viewModel.wakeAllAlarm()
                }
            } else {
                viewModel.setAlarmEnabledCompose(enabled)
                viewModel.sleepAllAlarm()
            }
        },
        onRingtoneClick = {
            // 벨소리 다이얼로그 표시 로직
        },
        onAlarmModeClick = onNavigateToMode,
        onStopReachedGoalToggle = { enabled ->
            alarmSettings?.etcSetting?.let { etcSetting ->
                viewModel.updateEtcSetting(etcSetting.copy(stopReachedGoal = enabled))
            }
        },
        onDelayAlarmClick = {
            viewModel.delayAllAlarmCompose(true, false)
        },
        onFullscreenSettingClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                    Uri.parse("package:${context.packageName}")
                )
                context.startActivity(intent)
            }
        },
        onExactAlarmSettingClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:${context.packageName}")
                )
                context.startActivity(intent)
            }
        }
    )

    if (showPermissionDialog) {
        PermissionDialog(
            onDismiss = {
                showPermissionDialog = false
                viewModel.setAlarmEnabledCompose(false)
            },
            onConfirm = {
                showPermissionDialog = false
                // 권한 설정으로 이동
                val intent = Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        )
    }
}

@Composable
private fun AlarmSettingScreenContent(
    isAlarmEnabled: Boolean = false,
    isNotificationEnabled: Boolean = false,
    ringtoneName: String = "",
    alarmModeName: String = "",
    etcSettings: AlarmEtcSettings = AlarmEtcSettings(),
    onAlarmToggle: (Boolean) -> Unit = {},
    onRingtoneClick: () -> Unit = {},
    onAlarmModeClick: () -> Unit = {},
    onStopReachedGoalToggle: (Boolean) -> Unit = {},
    onDelayAlarmClick: () -> Unit = {},
    onFullscreenSettingClick: () -> Unit = {},
    onExactAlarmSettingClick: () -> Unit = {}
) {
    var localAlarmEnabled by remember { mutableStateOf(isAlarmEnabled) }
    var localStopReachedGoal by remember { mutableStateOf(etcSettings.stopReachedGoal) }

    // Preview용 로컬 상태 업데이트
    LaunchedEffect(isAlarmEnabled) { localAlarmEnabled = isAlarmEnabled }
    LaunchedEffect(etcSettings.stopReachedGoal) { localStopReachedGoal = etcSettings.stopReachedGoal }

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
        // 상단 타이틀과 스위치
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.title_alarm_setting),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                    )
                    Text(
                        text = if (localAlarmEnabled) "알람이 활성화되어 있습니다" else "알람이 비활성화되어 있습니다",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF757575)
                    )
                }

                Switch(
                    checked = localAlarmEnabled,
                    onCheckedChange = { enabled ->
                        localAlarmEnabled = enabled
                        onAlarmToggle(enabled)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF4A90E2),
                        checkedTrackColor = Color(0xFF4A90E2).copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 알람 지연 버튼 (알람이 꺼져있을 때만 표시)
        if (!localAlarmEnabled && isNotificationEnabled) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDelayAlarmClick() },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEB3B).copy(alpha = 0.2f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Snooze,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.alarm_delay),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFF9800)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 설정 항목들
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
                // 벨소리 및 진동
                SettingItem(
                    icon = Icons.Default.VolumeUp,
                    title = stringResource(R.string.alarm_sound_title),
                    subtitle = ringtoneName.ifEmpty { stringResource(R.string.alarm_sound_device) },
                    onClick = onRingtoneClick
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // 알람 모드
                SettingItem(
                    icon = Icons.Default.Schedule,
                    title = stringResource(R.string.alarm_mode_title),
                    subtitle = alarmModeName.ifEmpty { stringResource(R.string.alarm_mode_period) },
                    onClick = onAlarmModeClick
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // 목표 도달 시 멈추기
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = Color(0xFF4A90E2),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.alarm_etc_stop_reached_goal),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Switch(
                        checked = localStopReachedGoal,
                        onCheckedChange = { enabled ->
                            localStopReachedGoal = enabled
                            onStopReachedGoalToggle(enabled)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF4A90E2),
                            checkedTrackColor = Color(0xFF4A90E2).copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 권한 설정
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
                    text = stringResource(R.string.permission_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 전체화면 알람 설정 (API 34+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    SettingItem(
                        icon = Icons.Default.Fullscreen,
                        title = stringResource(R.string.alarm_etc_fullscreen_setting),
                        subtitle = "화면 잠금 상태에서도 알람 표시",
                        onClick = onFullscreenSettingClick
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                    }
                }

                // 정확한 알람 설정 (API 31+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    SettingItem(
                        icon = Icons.Default.Timer,
                        title = stringResource(R.string.alarm_etc_exact_setting),
                        subtitle = "정확한 시간에 알람 수신",
                        onClick = onExactAlarmSettingClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4A90E2),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
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

@Composable
private fun PermissionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.permission_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = stringResource(R.string.permission_message),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.move))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

// Helper functions
private fun getRingtoneName(ringTone: RingTone): String {
    return when(ringTone) {
        RingTone.DEVICE -> "폰 설정과 동일"
        RingTone.BELL -> "벨소리"
        RingTone.VIBE -> "진동"
        RingTone.ALL -> "진동 및 벨소리"
        RingTone.IGNORE -> "무음"
    }
}

private fun getAlarmModeName(alarmMode: AlarmMode): String {
    return when(alarmMode) {
        AlarmMode.PERIOD -> "주기"
        AlarmMode.CUSTOM -> "맞춤"
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun AlarmSettingScreenPreview() {
    AlarmSettingScreenContent(
        isAlarmEnabled = true,
        isNotificationEnabled = true,
        ringtoneName = "진동 및 벨소리",
        alarmModeName = "주기",
        etcSettings = AlarmEtcSettings(stopReachedGoal = true)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun AlarmSettingScreenDisabledPreview() {
    AlarmSettingScreenContent(
        isAlarmEnabled = false,
        isNotificationEnabled = true,
        ringtoneName = "폰 설정과 동일",
        alarmModeName = "맞춤"
    )
}
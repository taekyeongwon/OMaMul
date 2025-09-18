package com.tkw.alarm

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.tkw.domain.model.AlarmEtcSettings
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.RingTone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterAlarmScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAlarmMode: () -> Unit,
    alarmViewModel: WaterAlarmViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val alarmSettings by alarmViewModel.alarmSettings.collectAsState()
    val isAlarmEnabled by alarmViewModel.isAlarmEnabled.collectAsState()
    var showRingtoneDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        alarmViewModel.loadAlarmSettings()
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
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar with Switch
            TopAppBar(
                title = { 
                    Text(
                        text = "알림 설정",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "뒤로",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Switch(
                        checked = isAlarmEnabled,
                        onCheckedChange = { enabled ->
                            alarmViewModel.setAlarmEnabled(enabled)
                            if (enabled) {
                                alarmViewModel.wakeAllAlarm()
                            } else {
                                alarmViewModel.sleepAllAlarm()
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF4CAF50),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFF757575)
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Alarm Settings Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "알림 설정",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Alarm Mode Setting
                        SettingItem(
                            title = "알림 모드",
                            subtitle = when (alarmSettings?.alarmMode) {
                                AlarmMode.PERIOD -> "주기 알림"
                                AlarmMode.CUSTOM -> "사용자 지정"
                                else -> "주기 알림"
                            },
                            onClick = onNavigateToAlarmMode
                        )
                        
                        Divider(color = Color(0xFFE0E0E0), modifier = Modifier.padding(vertical = 8.dp))
                        
                        // Alarm Sound Setting
                        SettingItem(
                            title = "알림 소리",
                            subtitle = when (alarmSettings?.ringToneMode?.getCurrentMode()) {
                                RingTone.DEVICE -> "시스템 기본음"
                                RingTone.BELL -> "벨소리"
                                RingTone.VIBE -> "진동"
                                RingTone.ALL -> "소리 + 진동"
                                RingTone.IGNORE -> "무음"
                                else -> "시스템 기본음"
                            },
                            onClick = { showRingtoneDialog = true }
                        )
                    }
                }
                
                // Additional Settings Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "추가 설정",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Stop when goal reached
                        SwitchSettingItem(
                            title = "목표 달성 시 알림 중지",
                            subtitle = "일일 목표를 달성하면 알림을 자동으로 중지합니다",
                            checked = alarmSettings?.etcSetting?.stopReachedGoal ?: false,
                            onCheckedChange = { checked ->
                                alarmSettings?.etcSetting?.let { etcSetting ->
                                    alarmViewModel.updateEtcSetting(
                                        etcSetting.copy(stopReachedGoal = checked)
                                    )
                                }
                            }
                        )
                        
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            Divider(color = Color(0xFFE0E0E0), modifier = Modifier.padding(vertical = 8.dp))
                            
                            SettingItem(
                                title = "정확한 알림 권한",
                                subtitle = "정확한 시간에 알림을 받으려면 권한이 필요합니다",
                                onClick = {
                                    val intent = Intent(
                                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                        Uri.parse("package:${context.packageName}")
                                    )
                                    context.startActivity(intent)
                                }
                            )
                        }
                        
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            Divider(color = Color(0xFFE0E0E0), modifier = Modifier.padding(vertical = 8.dp))
                            
                            SettingItem(
                                title = "전체 화면 알림 권한",
                                subtitle = "알림을 전체 화면으로 표시하려면 권한이 필요합니다",
                                onClick = {
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                                        Uri.parse("package:${context.packageName}")
                                    )
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
                
                // Delay Message
                if (!isAlarmEnabled) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch {
                                    alarmViewModel.delayAllAlarm(true, false)
                                }
                                alarmViewModel.setAlarmEnabled(true)
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                    ) {
                        Text(
                            text = "💡 지금 바로 알림을 시작하려면 여기를 눌러주세요",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp,
                            color = Color(0xFFFF9800),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
    
    // Ringtone Dialog
    if (showRingtoneDialog) {
        // TODO: Implement Ringtone Dialog in Compose
        showRingtoneDialog = false
    }
}

@Composable
fun SettingItem(
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
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )
        }
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF757575)
        )
    }
}

@Composable
fun SwitchSettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF2196F3),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFF757575)
            )
        )
    }
}

// Preview Functions
@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun WaterAlarmScreenPreview() {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Alarm Settings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "알림 설정",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SettingItem(
                        title = "알림 모드",
                        subtitle = "주기 알림",
                        onClick = {}
                    )
                    
                    Divider(color = Color(0xFFE0E0E0), modifier = Modifier.padding(vertical = 8.dp))
                    
                    SettingItem(
                        title = "알림 소리",
                        subtitle = "시스템 기본음",
                        onClick = {}
                    )
                }
            }
            
            // Additional Settings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "추가 설정",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SwitchSettingItem(
                        title = "목표 달성 시 알림 중지",
                        subtitle = "일일 목표를 달성하면 알림을 자동으로 중지합니다",
                        checked = true,
                        onCheckedChange = {}
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingItemPreview() {
    Column {
        SettingItem(
            title = "알림 모드",
            subtitle = "주기 알림",
            onClick = {}
        )
        Divider(color = Color(0xFFE0E0E0))
        SwitchSettingItem(
            title = "목표 달성 시 알림 중지",
            subtitle = "일일 목표를 달성하면 알림을 자동으로 중지합니다",
            checked = true,
            onCheckedChange = {}
        )
    }
}
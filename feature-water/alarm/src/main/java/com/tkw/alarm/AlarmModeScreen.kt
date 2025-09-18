package com.tkw.alarm

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tkw.domain.model.AlarmMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmModeScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPeriod: () -> Unit,
    onNavigateToCustom: () -> Unit,
    alarmViewModel: WaterAlarmViewModel = hiltViewModel()
) {
    val alarmSettings by alarmViewModel.alarmSettings.collectAsState()
    val currentMode = alarmSettings?.alarmMode ?: AlarmMode.PERIOD
    
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
            // Top Bar
            TopAppBar(
                title = { 
                    Text(
                        text = "알림 모드",
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
                // Period Mode Card
                AlarmModeCard(
                    title = "주기 알림",
                    subtitle = "설정한 시간 간격마다 알림을 받습니다",
                    isSelected = currentMode == AlarmMode.PERIOD,
                    onClick = {
                        alarmViewModel.updateAlarmMode(AlarmMode.PERIOD)
                        onNavigateToPeriod()
                    }
                )
                
                // Custom Mode Card
                AlarmModeCard(
                    title = "사용자 지정",
                    subtitle = "원하는 시간에 직접 알림을 설정합니다",
                    isSelected = currentMode == AlarmMode.CUSTOM,
                    onClick = {
                        alarmViewModel.updateAlarmMode(AlarmMode.CUSTOM)
                        onNavigateToCustom()
                    }
                )
                
                // Description Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F9FF))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "💡 알림 모드 설명",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "• 주기 알림: 30분, 1시간 등 일정한 간격으로 알림\n" +
                                  "• 사용자 지정: 아침 9시, 점심 12시 등 원하는 시간에 알림",
                            fontSize = 14.sp,
                            color = Color(0xFF757575),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmModeCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2196F3))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFF2196F3) else Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
            }
            
            if (isSelected) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Preview Functions
@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun AlarmModeScreenPreview() {
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
            AlarmModeCard(
                title = "주기 알림",
                subtitle = "설정한 시간 간격마다 알림을 받습니다",
                isSelected = true,
                onClick = {}
            )
            
            AlarmModeCard(
                title = "사용자 지정",
                subtitle = "원하는 시간에 직접 알림을 설정합니다",
                isSelected = false,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmModeCardPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AlarmModeCard(
            title = "주기 알림",
            subtitle = "설정한 시간 간격마다 알림을 받습니다",
            isSelected = true,
            onClick = {}
        )
        AlarmModeCard(
            title = "사용자 지정",
            subtitle = "원하는 시간에 직접 알림을 설정합니다",
            isSelected = false,
            onClick = {}
        )
    }
}
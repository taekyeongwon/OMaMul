package com.tkw.alarm.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.window.Dialog

enum class AlarmPeriod(val displayName: String, val minutes: Int) {
    FIFTEEN_MINUTES("15분마다", 15),
    THIRTY_MINUTES("30분마다", 30),
    ONE_HOUR("1시간마다", 60),
    TWO_HOURS("2시간마다", 120),
    THREE_HOURS("3시간마다", 180),
    CUSTOM("사용자 정의", 0)
}

@Composable
fun AlarmPeriodDialog(
    isVisible: Boolean,
    currentPeriod: AlarmPeriod = AlarmPeriod.ONE_HOUR,
    customMinutes: Int = 60,
    onDismiss: () -> Unit,
    onPeriodSelect: (AlarmPeriod, Int) -> Unit
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            AlarmPeriodDialogContent(
                currentPeriod = currentPeriod,
                customMinutes = customMinutes,
                onDismiss = onDismiss,
                onPeriodSelect = onPeriodSelect
            )
        }
    }
}

@Composable
private fun AlarmPeriodDialogContent(
    currentPeriod: AlarmPeriod = AlarmPeriod.ONE_HOUR,
    customMinutes: Int = 60,
    onDismiss: () -> Unit = {},
    onPeriodSelect: (AlarmPeriod, Int) -> Unit = { _, _ -> }
) {
    var selectedPeriod by remember { mutableStateOf(currentPeriod) }
    var localCustomMinutes by remember { mutableIntStateOf(customMinutes) }
    var customMinutesText by remember { mutableStateOf(customMinutes.toString()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
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
                .padding(24.dp)
        ) {
            // 헤더
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = "알람 주기",
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "알람 주기 설정",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 주기 옵션들
            AlarmPeriod.values().forEach { period ->
                PeriodOption(
                    period = period,
                    isSelected = selectedPeriod == period,
                    onSelect = { selectedPeriod = period }
                )
                if (period != AlarmPeriod.values().last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // 사용자 정의 주기 입력
            if (selectedPeriod == AlarmPeriod.CUSTOM) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF0F8FF)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, Color(0xFF4A90E2).copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "사용자 정의 주기",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customMinutesText,
                                onValueChange = { newValue ->
                                    if (newValue.all { it.isDigit() } && newValue.length <= 3) {
                                        customMinutesText = newValue
                                        localCustomMinutes = newValue.toIntOrNull() ?: 0
                                    }
                                },
                                modifier = Modifier.width(120.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4A90E2),
                                    focusedLabelColor = Color(0xFF4A90E2)
                                )
                            )
                            Text(
                                text = "분마다",
                                fontSize = 16.sp,
                                color = Color(0xFF666666)
                            )
                        }

                        if (localCustomMinutes < 5 && customMinutesText.isNotEmpty()) {
                            Text(
                                text = "최소 5분 이상 설정해주세요",
                                fontSize = 12.sp,
                                color = Color(0xFFFF5722),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
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
                        val minutes = if (selectedPeriod == AlarmPeriod.CUSTOM) {
                            localCustomMinutes
                        } else {
                            selectedPeriod.minutes
                        }

                        if (selectedPeriod != AlarmPeriod.CUSTOM || localCustomMinutes >= 5) {
                            onPeriodSelect(selectedPeriod, minutes)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    ),
                    enabled = selectedPeriod != AlarmPeriod.CUSTOM || localCustomMinutes >= 5
                ) {
                    Text("확인")
                }
            }
        }
    }
}

@Composable
private fun PeriodOption(
    period: AlarmPeriod,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color(0xFFF8F9FA)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF4A90E2) else Color(0xFFE0E0E0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = period.displayName,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFF1976D2) else Color(0xFF333333)
            )

            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "선택됨",
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun AlarmPeriodDialogPreview() {
    AlarmPeriodDialogContent(
        currentPeriod = AlarmPeriod.ONE_HOUR,
        customMinutes = 60,
        onDismiss = {},
        onPeriodSelect = { _, _ -> }
    )
}
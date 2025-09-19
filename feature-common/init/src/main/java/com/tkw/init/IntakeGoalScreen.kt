package com.tkw.init

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tkw.domain.IAlarmManager
import com.tkw.ui.R as CoreR
import javax.inject.Inject

@Composable
fun IntakeGoalScreen(
    onNavigateToHome: () -> Unit,
    viewModel: InitViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(null)

    var selectedAmount by remember { mutableStateOf(2000) }
    var showExactAlarmDialog by remember { mutableStateOf(false) }

    // Side Effect 처리
    LaunchedEffect(sideEffect) {
        when (sideEffect) {
            is InitContract.SideEffect.OnMoveNext -> {
                onNavigateToHome()
            }
            else -> {}
        }
    }

    // State 처리 - API 31 이상에서 정확한 알람 권한 확인
    LaunchedEffect(state) {
        if (state is InitContract.State.Complete) {
            if (Build.VERSION.SDK_INT >= 31) {
                // 정확한 알람 권한이 없는 경우 다이얼로그 표시
                showExactAlarmDialog = true
            } else {
                viewModel.setEvent(InitContract.Event.SaveInitialFlag(true))
            }
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
            ProgressIndicator(currentStep = 3)

            Spacer(modifier = Modifier.height(32.dp))

            // 제목
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(CoreR.string.init_intake_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(CoreR.string.init_intake_subtitle),
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 목표량 설정 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(CoreR.string.intake_amount),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 커스텀 Amount Picker
                    WaterAmountPicker(
                        amount = selectedAmount,
                        onAmountChange = { selectedAmount = it },
                        minValue = 100,
                        maxValue = 3000,
                        interval = 50
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 완료 버튼
            Button(
                onClick = {
                    viewModel.setEvent(InitContract.Event.SaveIntake(selectedAmount))
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
                    text = stringResource(CoreR.string.complete),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // 정확한 알람 권한 다이얼로그
    if (showExactAlarmDialog) {
        ExactAlarmPermissionDialog(
            onConfirm = {
                showExactAlarmDialog = false
                viewModel.setEvent(InitContract.Event.SaveInitialFlag(true))
            },
            onDismiss = {
                showExactAlarmDialog = false
                viewModel.setEvent(InitContract.Event.SaveInitialFlag(true))
            }
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
private fun WaterAmountPicker(
    amount: Int,
    onAmountChange: (Int) -> Unit,
    minValue: Int = 100,
    maxValue: Int = 3000,
    interval: Int = 50
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 증가 버튼
        IconButton(
            onClick = {
                val newAmount = (amount + interval).coerceAtMost(maxValue)
                onAmountChange(newAmount)
            },
            modifier = Modifier
                .size(48.dp)
                .background(
                    Color(0xFF2196F3).copy(alpha = 0.1f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "증가",
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 현재 값 표시
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF0F8FF)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = amount.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
                Text(
                    text = "ml",
                    fontSize = 16.sp,
                    color = Color(0xFF666666)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 감소 버튼
        IconButton(
            onClick = {
                val newAmount = (amount - interval).coerceAtLeast(minValue)
                onAmountChange(newAmount)
            },
            modifier = Modifier
                .size(48.dp)
                .background(
                    Color(0xFF2196F3).copy(alpha = 0.1f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "감소",
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 권장량 텍스트
        Text(
            text = "권장량: 1500-2500ml",
            fontSize = 12.sp,
            color = Color(0xFF999999)
        )
    }
}

@Composable
private fun ExactAlarmPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "정확한 알람 권한",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3)
            )
        },
        text = {
            Text(
                text = "물 마시기 알림을 정확한 시간에 받으려면 정확한 알람 권한이 필요합니다. 설정에서 권한을 허용해주세요.",
                color = Color(0xFF333333)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("확인", color = Color(0xFF2196F3))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("나중에", color = Color(0xFF666666))
            }
        }
    )
}

// Preview Functions
@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun IntakeGoalScreenPreview() {
    IntakeGoalScreenContent(
        onNavigateToHome = {}
    )
}

@Composable
private fun IntakeGoalScreenContent(
    onNavigateToHome: () -> Unit
) {
    var selectedAmount by remember { mutableStateOf(2000) }
    var showExactAlarmDialog by remember { mutableStateOf(false) }

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
            ProgressIndicator(currentStep = 3)

            Spacer(modifier = Modifier.height(32.dp))

            // 제목
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(CoreR.string.init_intake_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(CoreR.string.init_intake_subtitle),
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 목표량 설정 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(CoreR.string.intake_amount),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 커스텀 Amount Picker
                    WaterAmountPicker(
                        amount = selectedAmount,
                        onAmountChange = { selectedAmount = it },
                        minValue = 100,
                        maxValue = 3000,
                        interval = 50
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 완료 버튼
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= 31) {
                        showExactAlarmDialog = true
                    } else {
                        onNavigateToHome()
                    }
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
                    text = stringResource(CoreR.string.complete),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // 정확한 알람 권한 다이얼로그
    if (showExactAlarmDialog) {
        ExactAlarmPermissionDialog(
            onConfirm = {
                showExactAlarmDialog = false
                onNavigateToHome()
            },
            onDismiss = {
                showExactAlarmDialog = false
                onNavigateToHome()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WaterAmountPickerPreview() {
    WaterAmountPicker(
        amount = 2000,
        onAmountChange = {},
        minValue = 100,
        maxValue = 3000,
        interval = 50
    )
}
package com.tkw.setting.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tkw.setting.util.UnitConverter
import com.tkw.ui.R

@Composable
fun WaterIntakeDialog(
    isVisible: Boolean,
    currentIntake: Int, // ml 단위로 전달받음
    currentUnit: Int, // 0: ml, 1: fl.oz
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit // ml 단위로 반환
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            WaterIntakeDialogContent(
                currentIntake = currentIntake,
                currentUnit = currentUnit,
                onDismiss = onDismiss,
                onConfirm = onConfirm
            )
        }
    }
}

@Composable
private fun WaterIntakeDialogContent(
    currentIntake: Int, // ml 단위
    currentUnit: Int, // 0: ml, 1: fl.oz
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit // ml 단위로 반환
) {
    // 현재 단위로 변환된 값을 표시
    val unitString = UnitConverter.getUnitString(currentUnit)
    val displayValue = UnitConverter.convertFromMl(currentIntake, unitString)
    var intakeValue by remember {
        mutableStateOf(
            when(currentUnit) {
                0 -> displayValue.toInt().toString() // ml은 정수
                1 -> "%.1f".format(displayValue) // fl.oz는 소수점 1자리
                else -> displayValue.toInt().toString()
            }
        )
    }
    var isError by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE3F2FD),
                            Color.White
                        )
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 아이콘
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Color(0xFF4A90E2).copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalDrink,
                    contentDescription = null,
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 제목
            Text(
                text = "일일 목표 물 섭취량",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 설명
            Text(
                text = "하루에 마실 물의 목표량을 설정하세요",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 입력 필드
            OutlinedTextField(
                value = intakeValue,
                onValueChange = { value ->
                    // 단위에 따른 입력 형식 허용
                    val pattern = when(currentUnit) {
                        0 -> Regex("^\\d{0,4}$") // ml: 정수만
                        1 -> Regex("^\\d{0,3}(\\.\\d{0,1})?$") // fl.oz: 소수점 1자리까지
                        else -> Regex("^\\d{0,4}$")
                    }
                    if (value.isEmpty() || value.matches(pattern)) {
                        intakeValue = value
                        isError = false
                    }
                },
                label = {
                    Text("목표 섭취량")
                },
                suffix = {
                    Text(
                        text = unitString,
                        color = Color(0xFF4A90E2),
                        fontWeight = FontWeight.Medium
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                isError = isError,
                supportingText = if (isError) {
                    {
                        val minDisplay = UnitConverter.formatIntake(100, unitString)
                        val maxDisplay = UnitConverter.formatIntake(10000, unitString)
                        Text("${minDisplay} 이상 ${maxDisplay} 이하로 입력해주세요")
                    }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A90E2),
                    focusedLabelColor = Color(0xFF4A90E2)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 권장량 안내
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF3F9FF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💡 권장 섭취량",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• 성인 남성: ${UnitConverter.getRecommendedIntake("male", unitString)}\n" +
                                "• 성인 여성: ${UnitConverter.getRecommendedIntake("female", unitString)}\n" +
                                "• 활동량이 많거나 더운 날씨에는 더 많이 필요",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF424242),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 버튼들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 취소 버튼
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF757575)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF757575), Color(0xFF757575))
                        )
                    )
                ) {
                    Text("취소")
                }

                // 확인 버튼
                Button(
                    onClick = {
                        val inputValue = intakeValue.toDoubleOrNull()
                        if (inputValue != null && UnitConverter.isValidIntake(inputValue, unitString)) {
                            val mlValue = UnitConverter.convertToMl(inputValue, unitString)
                            onConfirm(mlValue)
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "설정",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WaterIntakeDialogPreview() {
    WaterIntakeDialogContent(
        currentIntake = 2000,
        currentUnit = 0, // ml 단위
        onDismiss = {},
        onConfirm = {}
    )
}
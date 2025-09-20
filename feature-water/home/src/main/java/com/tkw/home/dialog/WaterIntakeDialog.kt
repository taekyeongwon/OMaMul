package com.tkw.home.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun WaterIntakeDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (amount: Int) -> Unit,
    initialAmount: Int = 250
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            WaterIntakeDialogContent(
                initialAmount = initialAmount,
                onDismiss = onDismiss,
                onConfirm = onConfirm
            )
        }
    }
}

@Composable
private fun WaterIntakeDialogContent(
    initialAmount: Int = 250,
    onDismiss: () -> Unit = {},
    onConfirm: (amount: Int) -> Unit = {}
) {
    var amount by remember { mutableIntStateOf(initialAmount) }
    var amountText by remember { mutableStateOf(initialAmount.toString()) }

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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 헤더
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.LocalDrink,
                    contentDescription = "물 섭취",
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "물 섭취량 입력",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 현재 물 웨이브 효과 배경
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF4A90E2),
                                Color(0xFF1976D2)
                            )
                        ),
                        shape = RoundedCornerShape(60.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${amount}ml",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 입력 필드
            OutlinedTextField(
                value = amountText,
                onValueChange = { newText ->
                    amountText = newText
                    amount = newText.toIntOrNull() ?: 0
                },
                label = { Text("섭취량 (ml)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A90E2),
                    focusedLabelColor = Color(0xFF4A90E2)
                ),
                singleLine = true
            )

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
                        if (amount > 0) {
                            onConfirm(amount)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    ),
                    enabled = amount > 0
                ) {
                    Text("확인")
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun WaterIntakeDialogPreview() {
    WaterIntakeDialogContent(
        initialAmount = 250,
        onDismiss = {},
        onConfirm = { }
    )
}
package com.tkw.record.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class WaterLogEntry(
    val id: String,
    val amount: Int,
    val dateTime: LocalDateTime,
    val note: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogEditBottomSheet(
    isVisible: Boolean,
    logEntry: WaterLogEntry? = null,
    onDismiss: () -> Unit,
    onSave: (WaterLogEntry) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {
    if (isVisible && logEntry != null) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            LogEditBottomSheetContent(
                logEntry = logEntry,
                onDismiss = onDismiss,
                onSave = onSave,
                onDelete = onDelete
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogEditBottomSheetContent(
    logEntry: WaterLogEntry,
    onDismiss: () -> Unit = {},
    onSave: (WaterLogEntry) -> Unit = {},
    onDelete: ((String) -> Unit)? = null
) {
    var amount by remember { mutableIntStateOf(logEntry.amount) }
    var amountText by remember { mutableStateOf(logEntry.amount.toString()) }
    var note by remember { mutableStateOf(logEntry.note) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

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
                    Icons.Default.Edit,
                    contentDescription = "기록 편집",
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "물 섭취 기록 편집",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }

            if (onDelete != null) {
                IconButton(
                    onClick = { showDeleteConfirmation = true }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = Color(0xFFFF5722)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 시간 정보
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
                    text = "섭취 시간",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1976D2)
                )
                Text(
                    text = logEntry.dateTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm")),
                    fontSize = 16.sp,
                    color = Color(0xFF333333)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 섭취량 편집
        Column {
            Text(
                text = "섭취량",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1976D2)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 물방울 아이콘과 현재 양 표시
                Card(
                    modifier = Modifier.size(80.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4A90E2).copy(alpha = 0.1f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp, Color(0xFF4A90E2)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.LocalDrink,
                                contentDescription = null,
                                tint = Color(0xFF4A90E2),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "${amount}ml",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                }

                // 섭취량 입력
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 4) {
                            amountText = newValue
                            amount = newValue.toIntOrNull() ?: 0
                        }
                    },
                    label = { Text("섭취량 (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4A90E2),
                        focusedLabelColor = Color(0xFF4A90E2)
                    ),
                    singleLine = true
                )
            }

            // 빠른 선택 버튼들
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val quickAmounts = listOf(100, 200, 250, 500)
                quickAmounts.forEach { quickAmount ->
                    OutlinedButton(
                        onClick = {
                            amount = quickAmount
                            amountText = quickAmount.toString()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (amount == quickAmount) Color(0xFF4A90E2) else Color.Transparent,
                            contentColor = if (amount == quickAmount) Color.White else Color(0xFF4A90E2)
                        )
                    ) {
                        Text(
                            text = "${quickAmount}ml",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 메모 편집
        Column {
            Text(
                text = "메모 (선택사항)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1976D2)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("메모를 입력하세요") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A90E2),
                    focusedLabelColor = Color(0xFF4A90E2)
                ),
                maxLines = 3
            )
        }

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
                    val updatedEntry = logEntry.copy(
                        amount = amount,
                        note = note
                    )
                    onSave(updatedEntry)
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                ),
                enabled = amount > 0
            ) {
                Text("저장")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // 삭제 확인 다이얼로그
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFFF5722)
                )
            },
            title = {
                Text("기록 삭제")
            },
            text = {
                Text("이 물 섭취 기록을 삭제하시겠습니까?\n삭제된 기록은 복구할 수 없습니다.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete?.invoke(logEntry.id)
                        showDeleteConfirmation = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5722)
                    )
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("취소")
                }
            }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun LogEditBottomSheetPreview() {
    LogEditBottomSheetContent(
        logEntry = WaterLogEntry(
            id = "1",
            amount = 250,
            dateTime = LocalDateTime.now(),
            note = "맛있는 물"
        ),
        onDismiss = {},
        onSave = {},
        onDelete = {}
    )
}
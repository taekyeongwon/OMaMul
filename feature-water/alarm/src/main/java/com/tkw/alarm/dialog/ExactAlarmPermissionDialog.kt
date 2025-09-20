package com.tkw.alarm.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ExactAlarmPermissionDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            ExactAlarmPermissionDialogContent(
                onDismiss = onDismiss,
                onConfirm = onConfirm
            )
        }
    }
}

@Composable
private fun ExactAlarmPermissionDialogContent(
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
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
                            Color(0xFFFFF3E0),
                            Color.White
                        )
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 경고 아이콘
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFB74D),
                                Color(0xFFFF9800)
                            )
                        ),
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = "정확한 알람",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 제목
            Text(
                text = "정확한 알람 권한 필요",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 설명
            Text(
                text = "정확한 시간에 알람을 받으려면\n시스템 설정에서 권한을 허용해주세요.",
                fontSize = 16.sp,
                color = Color(0xFF424242),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 경고 메시지
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFFFF8E1),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "경고",
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "권한을 허용하지 않으면 알람이 정확하지 않을 수 있습니다.",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.weight(1f)
                )
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
                    Text("나중에")
                }

                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800)
                    )
                ) {
                    Text("설정으로 이동")
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun ExactAlarmPermissionDialogPreview() {
    ExactAlarmPermissionDialogContent(
        onDismiss = {},
        onConfirm = {}
    )
}
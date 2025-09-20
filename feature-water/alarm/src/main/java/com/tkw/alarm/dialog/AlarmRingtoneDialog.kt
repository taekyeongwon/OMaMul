package com.tkw.alarm.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

enum class AlarmRingtoneType(val displayName: String, val icon: ImageVector) {
    DEVICE("기본 알림음", Icons.Default.VolumeUp),
    BELL("벨소리", Icons.Default.MusicNote),
    VIBE("진동", Icons.Default.Vibration),
    ALL("벨소리 + 진동", Icons.Default.VolumeUp),
    SILENCE("무음", Icons.Default.VolumeOff)
}

@Composable
fun AlarmRingtoneDialog(
    isVisible: Boolean,
    currentRingtone: AlarmRingtoneType = AlarmRingtoneType.DEVICE,
    onDismiss: () -> Unit,
    onRingtoneSelect: (AlarmRingtoneType) -> Unit
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            AlarmRingtoneDialogContent(
                currentRingtone = currentRingtone,
                onDismiss = onDismiss,
                onRingtoneSelect = onRingtoneSelect
            )
        }
    }
}

@Composable
private fun AlarmRingtoneDialogContent(
    currentRingtone: AlarmRingtoneType = AlarmRingtoneType.DEVICE,
    onDismiss: () -> Unit = {},
    onRingtoneSelect: (AlarmRingtoneType) -> Unit = {}
) {
    var selectedRingtone by remember { mutableStateOf(currentRingtone) }

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
            modifier = Modifier.padding(24.dp)
        ) {
            // 헤더
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = "벨소리 설정",
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "알람 벨소리",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 벨소리 옵션들
            AlarmRingtoneType.values().forEach { ringtoneType ->
                RingtoneOption(
                    ringtoneType = ringtoneType,
                    isSelected = selectedRingtone == ringtoneType,
                    onSelect = { selectedRingtone = ringtoneType }
                )
                if (ringtoneType != AlarmRingtoneType.values().last()) {
                    Spacer(modifier = Modifier.height(8.dp))
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
                        onRingtoneSelect(selectedRingtone)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    )
                ) {
                    Text("확인")
                }
            }
        }
    }
}

@Composable
private fun RingtoneOption(
    ringtoneType: AlarmRingtoneType,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = ringtoneType.icon,
                    contentDescription = ringtoneType.displayName,
                    tint = if (isSelected) Color(0xFF4A90E2) else Color(0xFF666666),
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = ringtoneType.displayName,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color(0xFF1976D2) else Color(0xFF333333)
                )
            }

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
fun AlarmRingtoneDialogPreview() {
    AlarmRingtoneDialogContent(
        currentRingtone = AlarmRingtoneType.DEVICE,
        onDismiss = {},
        onRingtoneSelect = { }
    )
}
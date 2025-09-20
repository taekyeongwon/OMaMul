package com.tkw.setting.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun UnitSelectionDialog(
    isVisible: Boolean,
    currentUnit: String = "ml",
    onDismiss: () -> Unit,
    onUnitSelect: (String) -> Unit
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            UnitSelectionDialogContent(
                currentUnit = currentUnit,
                onDismiss = onDismiss,
                onUnitSelect = onUnitSelect
            )
        }
    }
}

@Composable
private fun UnitSelectionDialogContent(
    currentUnit: String = "ml",
    onDismiss: () -> Unit = {},
    onUnitSelect: (String) -> Unit = {}
) {
    var selectedUnit by remember { mutableStateOf(currentUnit) }

    val units = listOf(
        "ml" to "밀리리터 (ml)",
        "fl oz" to "플루이드 온스 (fl oz)",
        "cup" to "컵 (cup)"
    )

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
                    Icons.Default.Scale,
                    contentDescription = "단위 설정",
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "단위 선택",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 단위 옵션들
            units.forEach { (unitCode, unitName) ->
                UnitOption(
                    unitCode = unitCode,
                    unitName = unitName,
                    isSelected = selectedUnit == unitCode,
                    onSelect = {
                        selectedUnit = unitCode
                    }
                )
                if (unitCode != units.last().first) {
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
                        onUnitSelect(selectedUnit)
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
private fun UnitOption(
    unitCode: String,
    unitName: String,
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
            Column {
                Text(
                    text = unitName,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color(0xFF1976D2) else Color(0xFF333333)
                )
                Text(
                    text = when (unitCode) {
                        "ml" -> "1ml = 1ml"
                        "fl oz" -> "1fl oz ≈ 29.6ml"
                        "cup" -> "1cup ≈ 240ml"
                        else -> ""
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
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
fun UnitSelectionDialogPreview() {
    UnitSelectionDialogContent(
        currentUnit = "ml",
        onDismiss = {},
        onUnitSelect = { }
    )
}
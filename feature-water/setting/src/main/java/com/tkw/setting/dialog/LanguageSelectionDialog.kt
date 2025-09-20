package com.tkw.setting.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tkw.ui.R
import java.util.Locale

@Composable
fun LanguageSelectionDialog(
    isVisible: Boolean,
    currentLanguage: String = Locale.KOREAN.language,
    onDismiss: () -> Unit,
    onLanguageSelect: (String) -> Unit
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            LanguageSelectionDialogContent(
                currentLanguage = currentLanguage,
                onDismiss = onDismiss,
                onLanguageSelect = onLanguageSelect
            )
        }
    }
}

@Composable
private fun LanguageSelectionDialogContent(
    currentLanguage: String = Locale.KOREAN.language,
    onDismiss: () -> Unit = {},
    onLanguageSelect: (String) -> Unit = {}
) {
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

    val languages = listOf(
        Locale.KOREAN.language to "한국어",
        Locale.ENGLISH.language to "English",
        Locale.JAPANESE.language to "日本語",
        Locale.CHINESE.language to "中文"
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
                    Icons.Default.Language,
                    contentDescription = "언어 설정",
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "언어 선택",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 언어 옵션들
            languages.forEach { (code, name) ->
                LanguageOption(
                    languageCode = code,
                    languageName = name,
                    isSelected = selectedLanguage == code,
                    onSelect = {
                        selectedLanguage = code
                        onLanguageSelect(code)
                    }
                )
                if (code != languages.last().first) {
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
                        onLanguageSelect(selectedLanguage)
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
private fun LanguageOption(
    languageCode: String,
    languageName: String,
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
                text = languageName,
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
fun LanguageSelectionDialogPreview() {
    LanguageSelectionDialogContent(
        currentLanguage = Locale.KOREAN.language,
        onDismiss = {},
        onLanguageSelect = { }
    )
}
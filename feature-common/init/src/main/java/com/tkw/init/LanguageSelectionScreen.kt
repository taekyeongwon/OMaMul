package com.tkw.init

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tkw.common.LocaleHelper
import com.tkw.common.PermissionHelper
import com.tkw.ui.R as CoreR
import java.util.Locale

@Composable
fun LanguageSelectionScreen(
    onNavigateNext: () -> Unit,
    viewModel: InitViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle(null)

    var selectedLanguage by remember { mutableStateOf(Locale.KOREAN.language) }
    var isGrantNotificationPermission by remember { mutableStateOf(false) }

    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isGrantNotificationPermission = permissions[Manifest.permission.POST_NOTIFICATIONS] == true
    }

    // 최초 권한 요청
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
        }
    }

    // Side Effect 처리
    LaunchedEffect(sideEffect) {
        when (sideEffect) {
            is InitContract.SideEffect.OnMoveNext -> {
                onNavigateNext()
            }
            else -> {}
        }
    }

    // State 처리
    LaunchedEffect(state) {
        if (state is InitContract.State.Complete) {
            viewModel.setEvent(InitContract.Event.SaveAlarmEnableFlag(isGrantNotificationPermission))
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
            ProgressIndicator(currentStep = 1)

            Spacer(modifier = Modifier.height(32.dp))

            // 제목
            Text(
                text = stringResource(CoreR.string.init_language_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 언어 선택 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    val languages = listOf(
                        "ko" to stringResource(CoreR.string.lang_ko),
                        "en" to stringResource(CoreR.string.lang_en),
                        "ja" to stringResource(CoreR.string.lang_jp),
                        "zh" to stringResource(CoreR.string.lang_cn)
                    )

                    languages.forEach { (code, name) ->
                        LanguageOption(
                            languageCode = code,
                            languageName = name,
                            isSelected = selectedLanguage == code,
                            onSelect = {
                                selectedLanguage = code
                                LocaleHelper.setApplicationLocales(code)
                            }
                        )
                        if (code != languages.last().first) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 다음 버튼
            Button(
                onClick = {
                    viewModel.setEvent(InitContract.Event.SaveLanguage(selectedLanguage))
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
                    text = stringResource(CoreR.string.next),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
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
private fun LanguageOption(
    languageCode: String,
    languageName: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = languageName,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF2196F3)
            )
        )
    }
}

// Preview Functions
@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun LanguageSelectionScreenPreview() {
    LanguageSelectionScreenContent(
        onNavigateNext = {}
    )
}

@Composable
private fun LanguageSelectionScreenContent(
    onNavigateNext: () -> Unit
) {
    var selectedLanguage by remember { mutableStateOf(Locale.KOREAN.language) }

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
            ProgressIndicator(currentStep = 1)

            Spacer(modifier = Modifier.height(32.dp))

            // 제목
            Text(
                text = stringResource(CoreR.string.init_language_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 언어 선택 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    val languages = listOf(
                        "ko" to stringResource(CoreR.string.lang_ko),
                        "en" to stringResource(CoreR.string.lang_en),
                        "ja" to stringResource(CoreR.string.lang_jp),
                        "zh" to stringResource(CoreR.string.lang_cn)
                    )

                    languages.forEach { (code, name) ->
                        LanguageOption(
                            languageCode = code,
                            languageName = name,
                            isSelected = selectedLanguage == code,
                            onSelect = {
                                selectedLanguage = code
                            }
                        )
                        if (code != languages.last().first) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 다음 버튼
            Button(
                onClick = onNavigateNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(CoreR.string.next),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressIndicatorPreview() {
    ProgressIndicator(currentStep = 1)
}

@Preview(showBackground = true)
@Composable
fun LanguageOptionPreview() {
    LanguageOption(
        languageCode = "ko",
        languageName = "한국어",
        isSelected = true,
        onSelect = {}
    )
}
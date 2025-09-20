package com.tkw.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tkw.common.util.DateTimeUtils
import com.tkw.setting.dialog.LanguageSelectionDialog
import com.tkw.setting.dialog.UnitSelectionDialog
import com.tkw.ui.R

@Composable
fun WaterSettingScreen(
    onNavigateToCup: () -> Unit = {},
    onNavigateToAlarm: () -> Unit = {},
    onShowIntakeDialog: () -> Unit = {},
    onShowUnitDialog: () -> Unit = {},
    onShowLanguageDialog: () -> Unit = {},
    onShowLogoutDialog: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onSyncClick: () -> Unit = {},
    viewModel: SettingViewModel = hiltViewModel()
) {
    val totalIntake by viewModel.totalIntakeStateFlow.collectAsStateWithLifecycle()
    val totalAchieve by viewModel.totalAchieveStateFlow.collectAsStateWithLifecycle()
    val goalOfIntake by viewModel.goalOfIntakeStateFlow.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLangStateFlow.collectAsStateWithLifecycle()
    val unit by viewModel.unitStateFlow.collectAsStateWithLifecycle()
    val lastSync by viewModel.lastSyncStateFlow.collectAsStateWithLifecycle()
    val alarmMode by viewModel.alarmModeStateFlow.collectAsStateWithLifecycle()

    // Dialog 상태
    val showLanguageDialog by viewModel.showLanguageDialog.collectAsStateWithLifecycle()
    val showUnitDialog by viewModel.showUnitDialog.collectAsStateWithLifecycle()
    val alarmRingtone by viewModel.alarmRingtoneStateFlow.collectAsStateWithLifecycle()
    val alarmSchedule by viewModel.alarmScheduleStateFlow.collectAsStateWithLifecycle()
    val alarmTime by viewModel.alarmTimeStateFlow.collectAsStateWithLifecycle()

    WaterSettingScreenContent(
        totalIntake = totalIntake,
        totalAchieve = totalAchieve,
        goalOfIntake = goalOfIntake,
        currentLang = currentLang,
        unit = unit,
        lastSync = lastSync,
        alarmMode = alarmMode,
        alarmRingtone = alarmRingtone,
        alarmSchedule = alarmSchedule,
        alarmTime = alarmTime,
        onNavigateToCup = onNavigateToCup,
        onNavigateToAlarm = onNavigateToAlarm,
        onShowIntakeDialog = onShowIntakeDialog,
        onShowUnitDialog = { viewModel.showUnitDialog() },
        onShowLanguageDialog = { viewModel.showLanguageDialog() },
        onShowLogoutDialog = onShowLogoutDialog,
        onLoginClick = onLoginClick,
        onSyncClick = onSyncClick
    )

    // Language Selection Dialog
    LanguageSelectionDialog(
        isVisible = showLanguageDialog,
        currentLanguage = currentLang.toString(), // TODO: Int를 String으로 변환 로직 필요
        onDismiss = { viewModel.hideLanguageDialog() },
        onLanguageSelect = { language ->
            // TODO: 언어 변경 로직 구현
            viewModel.hideLanguageDialog()
        }
    )

    // Unit Selection Dialog
    UnitSelectionDialog(
        isVisible = showUnitDialog,
        currentUnit = "ml", // TODO: unit에서 현재 단위 추출
        onDismiss = { viewModel.hideUnitDialog() },
        onUnitSelect = { selectedUnit ->
            // TODO: 단위 변경 로직 구현
            viewModel.hideUnitDialog()
        }
    )
}

@Composable
private fun WaterSettingScreenContent(
    totalIntake: String = "0ml",
    totalAchieve: String = "0",
    goalOfIntake: String = "2000ml",
    currentLang: Int = R.string.lang_ko,
    unit: String = "ml, L",
    lastSync: Long = -1L,
    alarmMode: Int = R.string.alarm_mode_period,
    alarmRingtone: Int = R.string.alarm_sound_device,
    alarmSchedule: String = "-",
    alarmTime: String = "-",
    isLoggedIn: Boolean = false,
    userName: String = "사용자",
    onNavigateToCup: () -> Unit = {},
    onNavigateToAlarm: () -> Unit = {},
    onShowIntakeDialog: () -> Unit = {},
    onShowUnitDialog: () -> Unit = {},
    onShowLanguageDialog: () -> Unit = {},
    onShowLogoutDialog: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onSyncClick: () -> Unit = {}
) {
    var localIsLoggedIn by remember { mutableStateOf(isLoggedIn) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0F6FF),
                        Color(0xFFF0FAFF)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 계정 정보 섹션
            AccountInfoCard(
                isLoggedIn = localIsLoggedIn,
                userName = userName,
                lastSync = lastSync,
                totalIntake = totalIntake,
                totalAchieve = totalAchieve,
                onLoginClick = {
                    localIsLoggedIn = !localIsLoggedIn
                    onLoginClick()
                },
                onSyncClick = onSyncClick
            )

            // 물 섭취 설정 섹션
            SettingSectionCard(
                title = stringResource(R.string.setting_intake_setting_title),
                items = listOf(
                    SettingItem(
                        icon = Icons.Default.LocalDrink,
                        title = "물 섭취량",
                        subtitle = goalOfIntake,
                        onClick = onShowIntakeDialog
                    ),
                    SettingItem(
                        icon = Icons.Default.LocalDrink,
                        title = "컵 관리",
                        subtitle = "컵 설정 및 관리",
                        onClick = onNavigateToCup
                    ),
                    SettingItem(
                        icon = Icons.Default.Scale,
                        title = "단위 설정",
                        subtitle = unit,
                        onClick = onShowUnitDialog
                    )
                )
            )

            // 알람 설정 섹션
            SettingSectionCard(
                title = stringResource(R.string.setting_alarm_setting_title),
                items = listOf(
                    SettingItem(
                        icon = Icons.Default.Alarm,
                        title = stringResource(R.string.setting_alarm_title),
                        subtitle = "${stringResource(alarmMode)} · ${stringResource(alarmRingtone)}",
                        onClick = onNavigateToAlarm
                    ),
                    SettingItem(
                        icon = Icons.Default.Notifications,
                        title = stringResource(R.string.setting_alarm_schedule_title),
                        subtitle = "$alarmSchedule · $alarmTime",
                        onClick = onNavigateToAlarm
                    )
                )
            )

            // 기타 설정 섹션
            SettingSectionCard(
                title = stringResource(R.string.setting_etc_setting_title),
                items = listOf(
                    SettingItem(
                        icon = Icons.Default.Language,
                        title = stringResource(R.string.setting_language_title),
                        subtitle = stringResource(currentLang),
                        onClick = onShowLanguageDialog
                    )
                )
            )

            // 로그아웃 버튼
            if (localIsLoggedIn) {
                Button(
                    onClick = {
                        localIsLoggedIn = false
                        onShowLogoutDialog()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B6B)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.setting_logout),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountInfoCard(
    isLoggedIn: Boolean,
    userName: String,
    lastSync: Long,
    totalIntake: String,
    totalAchieve: String,
    onLoginClick: () -> Unit,
    onSyncClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 프로필 이미지
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4A90E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isLoggedIn) userName else stringResource(R.string.setting_sync),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )

                    if (isLoggedIn) {
                        val lastSyncText = if (lastSync == -1L) {
                            stringResource(R.string.setting_last_sync_empty)
                        } else {
                            DateTimeUtils.DateTime.getFormat(
                                DateTimeUtils.DateTime.getLocalDateTime(lastSync)
                            )
                        }
                        Text(
                            text = lastSyncText,
                            fontSize = 14.sp,
                            color = Color(0xFF7F8C8D)
                        )
                    }
                }

                if (isLoggedIn) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = stringResource(R.string.setting_sync),
                        tint = Color(0xFF4A90E2),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onSyncClick() }
                    )
                }
            }

            if (!isLoggedIn) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.setting_sync),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 통계 정보
            if (isLoggedIn) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        title = stringResource(R.string.setting_total_intake),
                        value = totalIntake
                    )
                    StatItem(
                        title = stringResource(R.string.setting_total_achieve),
                        value = totalAchieve
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    title: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A90E2)
        )
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color(0xFF7F8C8D)
        )
    }
}

@Composable
private fun SettingSectionCard(
    title: String,
    items: List<SettingItem>
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2C3E50),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                items.forEachIndexed { index, item ->
                    SettingItemRow(
                        item = item,
                        showDivider = index < items.size - 1
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingItemRow(
    item: SettingItem,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { item.onClick() }
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = Color(0xFF4A90E2),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2C3E50)
                )
                Text(
                    text = item.subtitle,
                    fontSize = 14.sp,
                    color = Color(0xFF7F8C8D)
                )
            }
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFECF0F1))
                    .padding(horizontal = 20.dp)
            )
        }
    }
}

private data class SettingItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun WaterSettingScreenPreview() {
    WaterSettingScreenContent(
        totalIntake = "1500ml",
        totalAchieve = "75",
        goalOfIntake = "2000ml",
        unit = "ml, L",
        alarmSchedule = "월, 화, 수, 목, 금",
        alarmTime = "09:00 - 21:00",
        isLoggedIn = true,
        userName = "김사용자"
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun WaterSettingScreenLoggedOutPreview() {
    WaterSettingScreenContent(
        isLoggedIn = false
    )
}
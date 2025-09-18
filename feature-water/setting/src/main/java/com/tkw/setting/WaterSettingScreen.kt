package com.tkw.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tkw.domain.model.DrinkUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterSettingScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCup: () -> Unit,
    onNavigateToAlarm: () -> Unit,
    settingViewModel: SettingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val goalIntake by settingViewModel.goalIntakeFlow.collectAsState(initial = 2000)
    val currentUnit by settingViewModel.unitFlow.collectAsState(initial = DrinkUnit.ML)
    val currentLanguage by settingViewModel.currentLangFlow.collectAsState(initial = "")
    val lastSync by settingViewModel.lastSync.collectAsState()
    val isLoggedIn by settingViewModel.isLoggedIn.collectAsState(initial = false)
    
    var showUnitDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showIntakeDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    
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
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopAppBar(
                title = { 
                    Text(
                        text = "설정",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "뒤로",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Account Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Profile Image
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(null) // TODO: Get actual profile image URL
                                    .placeholder(com.tkw.ui.R.drawable.account_circle)
                                    .error(com.tkw.ui.R.drawable.account_circle)
                                    .fallback(com.tkw.ui.R.drawable.account_circle)
                                    .build(),
                                contentDescription = "프로필 이미지",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isLoggedIn) "사용자 이름" else "로그인",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF212121)
                                )
                                if (isLoggedIn) {
                                    Text(
                                        text = if (lastSync == -1L) "동기화된 적 없음" 
                                               else "마지막 동기화: ${formatLastSync(lastSync)}",
                                        fontSize = 14.sp,
                                        color = Color(0xFF757575)
                                    )
                                }
                            }
                            
                            if (isLoggedIn) {
                                IconButton(
                                    onClick = { /* TODO: Sync */ }
                                ) {
                                    Icon(
                                        Icons.Default.Sync,
                                        contentDescription = "동기화",
                                        tint = Color(0xFF2196F3)
                                    )
                                }
                            }
                        }
                        
                        if (isLoggedIn) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showLogoutDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF5252)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("로그아웃", color = Color.White)
                            }
                        }
                    }
                }
                
                // Water Settings Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "💧 물 설정",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        SettingItem(
                            title = "일일 목표량",
                            subtitle = "${goalIntake}${currentUnit.unit}",
                            onClick = { showIntakeDialog = true }
                        )
                        
                        Divider(color = Color(0xFFE0E0E0), modifier = Modifier.padding(vertical = 8.dp))
                        
                        SettingItem(
                            title = "컵 관리",
                            subtitle = "자주 사용하는 컵을 등록하세요",
                            onClick = onNavigateToCup
                        )
                        
                        Divider(color = Color(0xFFE0E0E0), modifier = Modifier.padding(vertical = 8.dp))
                        
                        SettingItem(
                            title = "단위 설정",
                            subtitle = currentUnit.unit,
                            onClick = { showUnitDialog = true }
                        )
                    }
                }
                
                // Alarm Settings Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "⏰ 알림 설정",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        SettingItem(
                            title = "물 마시기 알림",
                            subtitle = "정기적으로 물 마시기 알림을 받으세요",
                            onClick = onNavigateToAlarm
                        )
                    }
                }
                
                // Additional Settings Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "⚙️ 기타 설정",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        SettingItem(
                            title = "언어 설정",
                            subtitle = getLanguageDisplayName(currentLanguage),
                            onClick = { showLanguageDialog = true }
                        )
                    }
                }
            }
        }
    }
    
    // Dialogs
    if (showUnitDialog) {
        // TODO: Implement Unit Dialog in Compose
        showUnitDialog = false
    }
    
    if (showLanguageDialog) {
        // TODO: Implement Language Dialog in Compose  
        showLanguageDialog = false
    }
    
    if (showIntakeDialog) {
        // TODO: Implement Intake Dialog in Compose
        showIntakeDialog = false
    }
    
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("로그아웃") },
            text = { Text("정말로 로그아웃하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        // TODO: Implement logout
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("취소")
                }
            }
        )
    }
}

@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )
        }
        Icon(
            Icons.Default.NavigateNext,
            contentDescription = null,
            tint = Color(0xFF757575)
        )
    }
}

private fun formatLastSync(lastSync: Long): String {
    if (lastSync == -1L) return "동기화된 적 없음"
    // TODO: Implement proper date formatting
    return "방금 전"
}

private fun getLanguageDisplayName(languageCode: String): String {
    return when (languageCode) {
        "ko" -> "한국어"
        "en" -> "English"
        "ja" -> "日本語"
        else -> "한국어"
    }
}

// Preview Functions
@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun WaterSettingScreenPreview() {
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Water Settings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "💧 물 설정",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SettingItem(
                        title = "일일 목표량",
                        subtitle = "2000ml",
                        onClick = {}
                    )
                    
                    Divider(color = Color(0xFFE0E0E0), modifier = Modifier.padding(vertical = 8.dp))
                    
                    SettingItem(
                        title = "컵 관리",
                        subtitle = "자주 사용하는 컵을 등록하세요",
                        onClick = {}
                    )
                    
                    Divider(color = Color(0xFFE0E0E0), modifier = Modifier.padding(vertical = 8.dp))
                    
                    SettingItem(
                        title = "단위 설정",
                        subtitle = "ml",
                        onClick = {}
                    )
                }
            }
            
            // Alarm Settings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "⏰ 알림 설정",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SettingItem(
                        title = "물 마시기 알림",
                        subtitle = "정기적으로 물 마시기 알림을 받으세요",
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingItemPreview() {
    Column {
        SettingItem(
            title = "일일 목표량",
            subtitle = "2000ml",
            onClick = {}
        )
        Divider(color = Color(0xFFE0E0E0))
        SettingItem(
            title = "컵 관리",
            subtitle = "자주 사용하는 컵을 등록하세요",
            onClick = {}
        )
    }
}
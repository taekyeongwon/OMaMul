package com.tkw.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.tkw.ui.icons.WaterIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.BarChart
import com.tkw.domain.model.DayOfWater
import com.tkw.domain.model.Water
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterLogScreen(
    onNavigateBack: () -> Unit,
    logViewModel: LogViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("일별", "주별", "월별")
    
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
                        text = "물 마시기 기록",
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
            ) {
                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = Color(0xFF2196F3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Content based on selected tab
                when (selectedTabIndex) {
                    0 -> LogDayContent(logViewModel)
                    1 -> LogWeekContent(logViewModel)
                    2 -> LogMonthContent(logViewModel)
                }
            }
        }
    }
}

@Composable
fun LogDayContent(logViewModel: LogViewModel) {
    val dayOfWater by logViewModel.currentDayOfWater.collectAsState()
    
    Column {
        // Chart Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "📊 오늘의 섭취 기록",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Summary Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        label = "총 섭취량",
                        value = "${dayOfWater?.getTotalIntakeByDate() ?: 0}ml",
                        color = Color(0xFF2196F3)
                    )
                    StatItem(
                        label = "섭취 횟수",
                        value = "${dayOfWater?.waterList?.size ?: 0}회",
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Detail Records Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "📋 상세 기록",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dayOfWater?.waterList?.let { waterList ->
                        items(waterList) { water ->
                            WaterIntakeItem(water = water)
                        }
                    } ?: run {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "아직 기록이 없습니다.",
                                    color = Color(0xFF757575)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogWeekContent(logViewModel: LogViewModel) {
    // Week view implementation would go here
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "주별 기록 화면\n(구현 예정)",
                fontSize = 16.sp,
                color = Color(0xFF757575)
            )
        }
    }
}

@Composable
fun LogMonthContent(logViewModel: LogViewModel) {
    // Month view implementation would go here
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "월별 기록 화면\n(구현 예정)",
                fontSize = 16.sp,
                color = Color(0xFF757575)
            )
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF757575)
        )
    }
}

@Composable
fun WaterIntakeItem(water: Water) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3F9FF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                WaterIcons.LocalDrink,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF2196F3)
            )
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${water.amount}ml",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = try {
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val date = inputFormat.parse(water.dateTime)
                        SimpleDateFormat("HH:mm", Locale.KOREAN).format(date ?: Date())
                    } catch (e: Exception) {
                        "-"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}

// Preview Functions
@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun WaterLogScreenPreview() {
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
                .padding(16.dp)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = 0,
                containerColor = Color.White,
                contentColor = Color(0xFF2196F3),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
            ) {
                listOf("일별", "주별", "월별").forEachIndexed { index, title ->
                    Tab(
                        selected = index == 0,
                        onClick = {},
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Chart Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "📊 오늘의 섭취 기록",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            label = "총 섭취량",
                            value = "1500ml",
                            color = Color(0xFF2196F3)
                        )
                        StatItem(
                            label = "섭취 횟수",
                            value = "6회",
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatItemPreview() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        StatItem(
            label = "총 섭취량",
            value = "1500ml",
            color = Color(0xFF2196F3)
        )
        StatItem(
            label = "섭취 횟수",
            value = "6회",
            color = Color(0xFF4CAF50)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WaterIntakeItemPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WaterIntakeItem(
            water = Water(amount = 250, dateTime = "2024-01-15 14:30:00")
        )
        WaterIntakeItem(
            water = Water(amount = 500, dateTime = "2024-01-15 12:15:00")
        )
        WaterIntakeItem(
            water = Water(amount = 350, dateTime = "2024-01-15 09:45:00")
        )
    }
}
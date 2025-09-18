package com.tkw.cup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import com.tkw.ui.icons.WaterIcons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tkw.domain.model.Cup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupManageScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Cup) -> Unit,
    cupViewModel: CupViewModel = hiltViewModel()
) {
    val cupList by cupViewModel.cupListLiveData.collectAsState(initial = emptyList())
    var isEditMode by remember { mutableStateOf(false) }
    
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
                        text = "컵 관리",
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
                actions = {
                    TextButton(
                        onClick = { isEditMode = !isEditMode }
                    ) {
                        Text(
                            text = if (isEditMode) "완료" else "편집",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
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
                // My Cups Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "내 컵 목록",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(cupList) { cup ->
                                CupItem(
                                    cup = cup,
                                    isEditMode = isEditMode,
                                    onEditClick = { onNavigateToEdit(cup) },
                                    onDeleteClick = { cupViewModel.deleteCup(cup.cupId) }
                                )
                            }
                            
                            item {
                                AddCupItem(
                                    onClick = onNavigateToCreate
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Recommended Cup Sizes
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "추천 컵 사이즈",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        RecommendedSizeItem("물병", "500ml", Color(0xFF2196F3))
                        Spacer(modifier = Modifier.height(8.dp))
                        RecommendedSizeItem("머그컵", "250ml", Color(0xFF4CAF50))
                        Spacer(modifier = Modifier.height(8.dp))
                        RecommendedSizeItem("텀블러", "400ml", Color(0xFFFF9800))
                    }
                }
            }
        }
    }
}

@Composable
fun CupItem(
    cup: Cup,
    isEditMode: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3F9FF)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    WaterIcons.LocalDrink,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFF2196F3)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cup.cupName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
                Text(
                    text = "${cup.cupAmount}ml",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
            }
            
            if (isEditMode) {
                // Edit button
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .background(
                            Color(0xFF2196F3),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "편집",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
                
                // Delete button
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(24.dp)
                        .background(
                            Color(0xFFFF5252),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddCupItem(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "새 컵 추가",
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "새 컵 추가",
                    fontSize = 10.sp,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}

@Composable
fun RecommendedSizeItem(
    name: String,
    size: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "$name ($size)",
            fontSize = 14.sp,
            color = Color(0xFF212121)
        )
    }
}

// Preview Functions
@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun CupManageScreenPreview() {
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
            // Mock content
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "내 컵 목록",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(3) { index ->
                            val cups = listOf(
                                Cup(cupId = "1", cupName = "물병", cupAmount = 500),
                                Cup(cupId = "2", cupName = "머그컵", cupAmount = 250),
                                Cup(cupId = "3", cupName = "텀블러", cupAmount = 350)
                            )
                            CupItem(
                                cup = cups[index],
                                isEditMode = false,
                                onEditClick = {},
                                onDeleteClick = {}
                            )
                        }
                        
                        item {
                            AddCupItem(onClick = {})
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CupItemPreview() {
    CupItem(
        cup = Cup(cupId = "1", cupName = "물병", cupAmount = 500),
        isEditMode = false,
        onEditClick = {},
        onDeleteClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun CupItemEditModePreview() {
    CupItem(
        cup = Cup(cupId = "1", cupName = "물병", cupAmount = 500),
        isEditMode = true,
        onEditClick = {},
        onDeleteClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun AddCupItemPreview() {
    AddCupItem(onClick = {})
}

@Preview(showBackground = true)
@Composable
fun RecommendedSizeItemPreview() {
    Column {
        RecommendedSizeItem("물병", "500ml", Color(0xFF2196F3))
        RecommendedSizeItem("머그컵", "250ml", Color(0xFF4CAF50))
        RecommendedSizeItem("텀블러", "400ml", Color(0xFFFF9800))
    }
}
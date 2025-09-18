package com.tkw.cup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.tkw.ui.icons.WaterIcons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tkw.domain.model.Cup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupCreateScreen(
    cupId: String?,
    onNavigateBack: () -> Unit,
    cupViewModel: CupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isCreateMode = cupId == null
    
    var cupName by remember { mutableStateOf("") }
    var cupAmount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    // ViewModel events 관찰
    cupViewModel.nextEvent.observe({ onNavigateBack() })

    cupViewModel.toastEvent.observe { error ->
        // Toast 표시 로직
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
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            TopAppBar(
                title = { 
                    Text(
                        text = if (isCreateMode) "새 컵 추가" else "컵 수정",
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
                // Cup Preview Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            WaterIcons.LocalDrink,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF2196F3)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = cupName.ifBlank { "컵 이름" },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (cupName.isBlank()) Color(0xFF757575) else Color(0xFF212121)
                        )
                        Text(
                            text = "${cupAmount.ifBlank { "0" }}ml",
                            fontSize = 16.sp,
                            color = if (cupAmount.isBlank()) Color(0xFF757575) else Color(0xFF2196F3)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Input Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "컵 정보",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Cup Name Input
                        OutlinedTextField(
                            value = cupName,
                            onValueChange = { cupName = it },
                            label = { Text("컵 이름") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2196F3),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Cup Amount Input
                        OutlinedTextField(
                            value = cupAmount,
                            onValueChange = { cupAmount = it.filter { char -> char.isDigit() } },
                            label = { Text("용량 (ml)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2196F3),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            ),
                            trailingIcon = {
                                Text(
                                    text = "ml",
                                    color = Color(0xFF757575),
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Quick Amount Selection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "빠른 선택",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickAmountButton(
                                amount = "150",
                                modifier = Modifier.weight(1f),
                                onClick = { cupAmount = "150" }
                            )
                            QuickAmountButton(
                                amount = "250",
                                modifier = Modifier.weight(1f),
                                onClick = { cupAmount = "250" }
                            )
                            QuickAmountButton(
                                amount = "350",
                                modifier = Modifier.weight(1f),
                                onClick = { cupAmount = "350" }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickAmountButton(
                                amount = "500",
                                modifier = Modifier.weight(1f),
                                onClick = { cupAmount = "500" }
                            )
                            QuickAmountButton(
                                amount = "750",
                                modifier = Modifier.weight(1f),
                                onClick = { cupAmount = "750" }
                            )
                            QuickAmountButton(
                                amount = "1000",
                                modifier = Modifier.weight(1f),
                                onClick = { cupAmount = "1000" }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Save Button
                Button(
                    onClick = {
                        if (cupName.isNotBlank() && cupAmount.isNotBlank()) {
                            isLoading = true
                            cupViewModel.cupNameLiveData.value = cupName
                            cupViewModel.cupAmountLiveData.value = cupAmount.toIntOrNull() ?: 0
                            if (isCreateMode) {
                                cupViewModel.insertCup()
                            } else {
                                cupViewModel.updateCup()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    enabled = !isLoading && cupName.isNotBlank() && cupAmount.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = if (isCreateMode) "컵 추가" else "수정 완료",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun QuickAmountButton(
    amount: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF2196F3)
        )
    ) {
        Text(
            text = "${amount}ml",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// Extension function for LiveData observation in Compose
@Composable
private fun <T> androidx.lifecycle.LiveData<T>.observe(onChanged: (T) -> Unit) {
    val context = LocalContext.current
    DisposableEffect(this) {
        val observer = androidx.lifecycle.Observer<T> { value ->
            onChanged(value)
        }
        observe(context as androidx.lifecycle.LifecycleOwner, observer)
        onDispose {
            removeObserver(observer)
        }
    }
}

// Preview Functions
@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun CupCreateScreenPreview() {
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
            // Cup Preview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        WaterIcons.LocalDrink,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF2196F3)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "새 컵",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Text(
                        text = "250ml",
                        fontSize = 16.sp,
                        color = Color(0xFF2196F3)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Input Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "컵 정보",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = "새 컵",
                        onValueChange = {},
                        label = { Text("컵 이름") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = "250",
                        onValueChange = {},
                        label = { Text("용량 (ml)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Text(
                                text = "ml",
                                color = Color(0xFF757575),
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuickAmountButtonPreview() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickAmountButton(
            amount = "250",
            modifier = Modifier.weight(1f),
            onClick = {}
        )
        QuickAmountButton(
            amount = "500",
            modifier = Modifier.weight(1f),
            onClick = {}
        )
        QuickAmountButton(
            amount = "750",
            modifier = Modifier.weight(1f),
            onClick = {}
        )
    }
}
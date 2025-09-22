package com.tkw.cup

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tkw.domain.model.Cup
import com.tkw.ui.R

@Composable
fun CupCreationScreen(
    cup: Cup?,  // 수정화면일 때 저장된 컵 정보
    onNavigateBack: () -> Unit,
    viewModel: CupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val createMode by viewModel.createModeStateFlow.collectAsStateWithLifecycle()
    val buttonName by viewModel.buttonNameStateFlow.collectAsStateWithLifecycle()

    // ViewModel의 StateFlow 값들
    val cupName by viewModel.cupNameStateFlow.collectAsStateWithLifecycle()
    val cupAmount by viewModel.cupAmountStateFlow.collectAsStateWithLifecycle()

    // cup 파라미터를 ViewModel에 전달하여 초기화
    LaunchedEffect(cup) {
        viewModel.initWithCup(cup)
    }

    // NextEvent와 ToastEvent 처리 (SharedFlow 방식)
    LaunchedEffect(Unit) {
        viewModel.nextEvent.collect {
            onNavigateBack()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { error ->
            Toast.makeText(context, error.getMessage(context), Toast.LENGTH_SHORT).show()
        }
    }

    CupCreationScreenContent(
        cupName = cupName,
        cupAmount = cupAmount,
        isCreateMode = createMode,
        buttonText = buttonName.ifEmpty {
            if (createMode) stringResource(R.string.add) else stringResource(R.string.modify)
        },
        onCupNameChange = { viewModel.updateCupName(it) },
        onCupAmountChange = { viewModel.updateCupAmount(it) },
        onSave = {
            if (createMode) {
                viewModel.insertCup()
            } else {
                viewModel.updateCup()
            }
        },
        onBack = onNavigateBack
    )
}

@Composable
private fun CupCreationScreenContent(
    cupName: String = "",
    cupAmount: Int = 200,
    isCreateMode: Boolean = true,
    buttonText: String = "",
    onCupNameChange: (String) -> Unit = {},
    onCupAmountChange: (Int) -> Unit = {},
    onSave: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var localCupName by remember { mutableStateOf(cupName) }
    var localCupAmount by remember { mutableStateOf(cupAmount.toString()) }

    // Preview용 로컬 상태 업데이트
    LaunchedEffect(cupName) { localCupName = cupName }
    LaunchedEffect(cupAmount) { localCupAmount = cupAmount.toString() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE3F2FD),
                        Color(0xFFBBDEFB)
                    )
                )
            )
            .padding(16.dp)
    ) {
        // 상단 타이틀
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                onBack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color(0xFF1565C0)
                )
            }

            Text(
                text = if (isCreateMode) stringResource(R.string.title_cup_add)
                      else stringResource(R.string.title_cup_edit),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 컵 시각화
        CupVisualization(
            cupName = localCupName.ifEmpty { "새 컵" },
            cupAmount = localCupAmount.toIntOrNull() ?: 200
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 입력 폼
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // 컵 이름 입력
                Text(
                    text = stringResource(R.string.cup_alias),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1565C0)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = localCupName,
                    onValueChange = {
                        localCupName = it
                        onCupNameChange(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(stringResource(R.string.cup_alias_hint))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocalDrink,
                            contentDescription = null,
                            tint = Color(0xFF4A90E2)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4A90E2),
                        unfocusedBorderColor = Color(0xFFBDBDBD)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 컵 용량 입력
                Text(
                    text = stringResource(R.string.cup_amount),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1565C0)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = localCupAmount,
                    onValueChange = { value ->
                        if (value.all { it.isDigit() } && value.length <= 4) {
                            localCupAmount = value
                            onCupAmountChange(value.toIntOrNull() ?: 0)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("200")
                    },
                    suffix = {
                        Text(
                            text = "ml",
                            color = Color(0xFF757575)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = Color(0xFF4A90E2)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4A90E2),
                        unfocusedBorderColor = Color(0xFFBDBDBD)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 저장 버튼
        Button(
            onClick = {
                onSave()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A90E2)
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = localCupName.isNotBlank() && (localCupAmount.toIntOrNull() ?: 0) > 0
        ) {
            Icon(
                imageVector = if (isCreateMode) Icons.Default.Add else Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buttonText.ifEmpty {
                    if (isCreateMode) stringResource(R.string.add) else stringResource(R.string.modify)
                },
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun CupVisualization(
    cupName: String,
    cupAmount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 컵 아이콘 (큰 사이즈)
            Icon(
                imageVector = Icons.Default.LocalDrink,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF4A90E2)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 컵 이름
            Text(
                text = cupName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 컵 용량
            Text(
                text = "${cupAmount}ml",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF757575)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun CupCreationScreenPreview() {
    CupCreationScreenContent(
        cupName = "물잔",
        cupAmount = 200,
        isCreateMode = true
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun CupCreationScreenEditPreview() {
    CupCreationScreenContent(
        cupName = "텀블러",
        cupAmount = 500,
        isCreateMode = false
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun CupVisualizationPreview() {
    CupVisualization(
        cupName = "머그컵",
        cupAmount = 300
    )
}
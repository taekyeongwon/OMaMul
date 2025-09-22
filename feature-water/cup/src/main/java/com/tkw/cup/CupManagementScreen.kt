package com.tkw.cup

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tkw.domain.model.Cup
import com.tkw.ui.R

@Composable
fun CupManagementScreen(
    onNavigateToCreate: (Cup?) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CupViewModel = hiltViewModel()
) {
    val cupList by viewModel.cupListLiveData.collectAsStateWithLifecycle()
    val modifyMode by viewModel.modifyModeStateFlow.collectAsStateWithLifecycle()

    // 로컬 상태로 드래그된 리스트와 체크 상태 관리
    var draggedList by remember { mutableStateOf<List<Cup>>(emptyList()) }
    var checkedCups by remember { mutableStateOf<Set<String>>(emptySet()) }

    // cupList가 변경되면 draggedList 업데이트
    LaunchedEffect(cupList) {
        if (draggedList.isEmpty()) {
            draggedList = cupList
        }
    }

    // 뒤로가기 처리
    LaunchedEffect(modifyMode) {
        if (!modifyMode) {
            checkedCups = emptySet()
        }
    }

    CupManagementScreenContent(
        cupList = if (draggedList.isNotEmpty()) draggedList else cupList,
        modifyMode = modifyMode,
        checkedCups = checkedCups,
        onCupChecked = { cupId, isChecked ->
            checkedCups = if (isChecked) {
                checkedCups + cupId
            } else {
                checkedCups - cupId
            }
        },
        onCupEdit = { cup ->
            val editCup = cup.copy()
            editCup.createMode = false
            onNavigateToCreate(editCup)
        },
        onCupLongClick = { cupId ->
            checkedCups = checkedCups + cupId
            viewModel.setModifyMode(true)
        },
        onReorderMode = {
            viewModel.setModifyMode(true)
        },
        onDeleteSelected = {
            checkedCups.forEach { cupId ->
                viewModel.deleteCup(cupId)
            }
            checkedCups = emptySet()
            viewModel.setModifyMode(false)
        },
        onAddCup = {
            onNavigateToCreate(null)
        },
        onDragEnd = { newList ->
            draggedList = newList
            viewModel.updateAll(newList)
        },
        onBackPressed = {
            if (modifyMode) {
                checkedCups = emptySet()
                viewModel.setModifyMode(false)
            } else {
                onNavigateBack()
            }
        }
    )
}

@Composable
private fun CupManagementScreenContent(
    cupList: List<Cup> = emptyList(),
    modifyMode: Boolean = false,
    checkedCups: Set<String> = emptySet(),
    onCupChecked: (String, Boolean) -> Unit = { _, _ -> },
    onCupEdit: (Cup) -> Unit = {},
    onCupLongClick: (String) -> Unit = {},
    onReorderMode: () -> Unit = {},
    onDeleteSelected: () -> Unit = {},
    onAddCup: () -> Unit = {},
    onDragEnd: (List<Cup>) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    var localCupList by remember { mutableStateOf(cupList) }
    var localModifyMode by remember { mutableStateOf(modifyMode) }
    var localCheckedCups by remember { mutableStateOf(checkedCups) }

    // Preview용 로컬 상태 업데이트
    LaunchedEffect(cupList) { localCupList = cupList }
    LaunchedEffect(modifyMode) { localModifyMode = modifyMode }
    LaunchedEffect(checkedCups) { localCheckedCups = checkedCups }

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
        Text(
            text = stringResource(R.string.title_cup_manage),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (localCupList.isEmpty()) {
            // 빈 상태
            EmptyCupState(
                onAddCup = {
                    localCupList = localCupList + Cup(
                        cupId = "temp_${System.currentTimeMillis()}",
                        cupName = "새 컵",
                        cupAmount = 200
                    )
                    onAddCup()
                }
            )
        } else {
            // 컵 리스트
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = localCupList,
                    key = { _, cup -> cup.cupId }
                ) { index, cup ->
                    CupItem(
                        cup = cup,
                        isModifyMode = localModifyMode,
                        isChecked = localCheckedCups.contains(cup.cupId),
                        onChecked = { isChecked ->
                            localCheckedCups = if (isChecked) {
                                localCheckedCups + cup.cupId
                            } else {
                                localCheckedCups - cup.cupId
                            }
                            onCupChecked(cup.cupId, isChecked)
                        },
                        onEdit = {
                            onCupEdit(cup)
                        },
                        onLongClick = {
                            localCheckedCups = localCheckedCups + cup.cupId
                            localModifyMode = true
                            onCupLongClick(cup.cupId)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 하단 버튼들
            BottomActionButtons(
                isModifyMode = localModifyMode,
                hasSelectedItems = localCheckedCups.isNotEmpty(),
                canReorder = localCupList.size > 1,
                onReorder = {
                    localModifyMode = true
                    onReorderMode()
                },
                onDelete = {
                    val remainingCups = localCupList.filter { !localCheckedCups.contains(it.cupId) }
                    localCupList = remainingCups
                    localCheckedCups = emptySet()
                    localModifyMode = false
                    onDeleteSelected()
                },
                onAdd = {
                    localCupList = localCupList + Cup(
                        cupId = "temp_${System.currentTimeMillis()}",
                        cupName = "새 컵",
                        cupAmount = 200
                    )
                    onAddCup()
                }
            )
        }
    }
}

@Composable
private fun EmptyCupState(
    onAddCup: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocalDrink,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color(0xFF90CAF9)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.empty_cup_message),
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF757575)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddCup,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A90E2)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.add_first_cup))
        }
    }
}

@Composable
private fun CupItem(
    cup: Cup,
    isModifyMode: Boolean,
    isChecked: Boolean,
    onChecked: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 체크박스 (수정 모드일 때만)
            AnimatedVisibility(
                visible = isModifyMode,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = onChecked,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF4A90E2)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // 컵 아이콘
            Icon(
                imageVector = Icons.Default.LocalDrink,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color(0xFF4A90E2)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 컵 정보
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cup.cupName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF1565C0)
                )
                Text(
                    text = "${cup.cupAmount}ml",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF757575)
                )
            }

            // 편집 버튼 (일반 모드일 때만)
            AnimatedVisibility(
                visible = !isModifyMode,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit),
                        tint = Color(0xFF4A90E2)
                    )
                }
            }

            // 드래그 핸들 (수정 모드일 때만)
            AnimatedVisibility(
                visible = isModifyMode,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = null,
                    tint = Color(0xFF757575)
                )
            }
        }
    }
}

@Composable
private fun BottomActionButtons(
    isModifyMode: Boolean,
    hasSelectedItems: Boolean,
    canReorder: Boolean,
    onReorder: () -> Unit,
    onDelete: () -> Unit,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isModifyMode) {
            // 삭제 버튼 (선택된 항목이 있을 때만)
            AnimatedVisibility(
                visible = hasSelectedItems,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE57373)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.delete))
                }
            }
        } else {
            // 순서 변경 버튼 (2개 이상일 때만)
            if (canReorder) {
                Button(
                    onClick = onReorder,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF81C784)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.reorder))
                }
            }

            // 추가 버튼
            Button(
                onClick = onAdd,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.add_cup))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun CupManagementScreenPreview() {
    val sampleCups = listOf(
        Cup(
            cupId = "1",
            cupName = "물잔",
            cupAmount = 200
        ),
        Cup(
            cupId = "2",
            cupName = "머그컵",
            cupAmount = 300
        ),
        Cup(
            cupId = "3",
            cupName = "텀블러",
            cupAmount = 500
        )
    )

    CupManagementScreenContent(
        cupList = sampleCups,
        modifyMode = false
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun CupManagementScreenEmptyPreview() {
    CupManagementScreenContent(
        cupList = emptyList()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFE0F6FF)
@Composable
fun CupManagementScreenModifyPreview() {
    val sampleCups = listOf(
        Cup(cupId = "1", cupName = "물잔", cupAmount = 200),
        Cup(cupId = "2", cupName = "머그컵", cupAmount = 300),
        Cup(cupId = "3", cupName = "텀블러", cupAmount = 500)
    )

    CupManagementScreenContent(
        cupList = sampleCups,
        modifyMode = true,
        checkedCups = setOf("1", "3")
    )
}
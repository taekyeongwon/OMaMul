package com.tkw.cup

import androidx.lifecycle.viewModelScope
import com.tkw.base.AppError
import com.tkw.base.BaseViewModel
import com.tkw.base.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import com.tkw.domain.CupRepository
import com.tkw.domain.model.Cup
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel
class CupViewModel @Inject constructor(
    private val cupRepository: CupRepository
): BaseViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val cupListLiveData: StateFlow<List<Cup>> =
        cupRepository.getCupList().mapLatest {
            it.cupList  //Flow<CupListEntity> -> Flow<List<Cup>>으로 최신값 매핑
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    //cup create fragment에서 관찰할 변수 (StateFlow로 통일)
    private val _cupNameStateFlow = MutableStateFlow("")
    val cupNameStateFlow: StateFlow<String> = _cupNameStateFlow.asStateFlow()

    private val _cupAmountStateFlow = MutableStateFlow(200)
    val cupAmountStateFlow: StateFlow<Int> = _cupAmountStateFlow.asStateFlow()

    private val _buttonNameStateFlow = MutableStateFlow("")
    val buttonNameStateFlow: StateFlow<String> = _buttonNameStateFlow.asStateFlow()

    // SharedFlow로 변환된 이벤트들
    private val _nextEvent = MutableSharedFlow<Unit>()
    val nextEvent: SharedFlow<Unit> = _nextEvent.asSharedFlow()

    private val _toastEvent = MutableSharedFlow<AppError>()
    val toastEvent: SharedFlow<AppError> = _toastEvent.asSharedFlow()

    private val _createModeStateFlow = MutableStateFlow(true)
    val createModeStateFlow: StateFlow<Boolean> = _createModeStateFlow.asStateFlow()

    private val _modifyModeStateFlow = MutableStateFlow(false)
    val modifyModeStateFlow: StateFlow<Boolean> = _modifyModeStateFlow.asStateFlow()


    // 편집할 컵의 ID
    private var editingCupId: String? = null

    fun initWithCup(cup: Cup?) {
        cup?.let {
            editingCupId = it.cupId
            _createModeStateFlow.value = false
            _cupNameStateFlow.value = it.cupName
            _cupAmountStateFlow.value = it.cupAmount
        } ?: run {
            editingCupId = null
            _createModeStateFlow.value = true
            _cupNameStateFlow.value = ""
            _cupAmountStateFlow.value = 200
        }
    }

    fun insertCup() {
        if(!validateCheck()) {
            launch { _toastEvent.emit(AppError(100)) }
            return
        }
        launch {
            val cupName = _cupNameStateFlow.value
            val cupAmount = _cupAmountStateFlow.value
            cupRepository.insertCup(cupName, cupAmount)
            _nextEvent.emit(Unit)
        }
    }

    fun updateCup() {
        if(!validateCheck()) {
            launch { _toastEvent.emit(AppError(100)) }
            return
        }
        launch {
            val cupName = _cupNameStateFlow.value
            val cupAmount = _cupAmountStateFlow.value
            editingCupId?.let { cupId ->
                cupRepository.updateCup(cupId, cupName, cupAmount)
                _nextEvent.emit(Unit)
            }
        }
    }

    fun deleteCup(cupId: String) {
        launch {
            cupRepository.deleteCup(cupId)
            _nextEvent.emit(Unit)
        }
    }

    private fun validateCheck(): Boolean = _cupNameStateFlow.value.isNotBlank()

    fun updateAll(list: List<Cup>) {
        launch {
            cupRepository.updateAll(list)
        }
    }

    fun setModifyMode(flag: Boolean) {
        _modifyModeStateFlow.value = flag
    }

    // Compose용 함수들
    fun updateCupName(name: String) {
        _cupNameStateFlow.value = name
    }

    fun updateCupAmount(amount: Int) {
        _cupAmountStateFlow.value = amount
    }

    fun updateButtonName(name: String) {
        _buttonNameStateFlow.value = name
    }
}
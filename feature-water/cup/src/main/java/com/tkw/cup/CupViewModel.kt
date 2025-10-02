package com.tkw.cup

import androidx.lifecycle.viewModelScope
import com.tkw.base.AppError
import com.tkw.base.BaseViewModel
import com.tkw.base.launch
import com.tkw.domain.CupRepository
import com.tkw.domain.model.Cup
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = CupViewModel.AssistFactory::class)
class CupViewModel
@AssistedInject constructor(
    private val cupRepository: CupRepository,
    @Assisted private val params: Cup
): BaseViewModel() {

    @AssistedFactory
    interface AssistFactory {
        fun create(params: Cup): CupViewModel
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val cupListFlow: StateFlow<List<Cup>> =
        cupRepository.getCupList().mapLatest {
            it.cupList  //Flow<CupListEntity> -> Flow<List<Cup>>으로 최신값 매핑
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            listOf()
        )

    // 현재 선택된 컵 ID 정보
    val currentSelectedCupIdFlow: StateFlow<String?> =
        cupRepository.getCurrentSelectedCupId().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    //cup create fragment에서 관찰할 변수
    val cupNameFlow = MutableStateFlow(params.cupName)
    val cupAmountFlow = MutableStateFlow(params.cupAmount)
    val cupUnitFlow = MutableStateFlow(params.cupUnit)
    val buttonNameFlow = MutableStateFlow("")

    private val _nextEvent = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
    val nextEvent: SharedFlow<Unit> = _nextEvent.asSharedFlow()

    private val _createModeFlow = MutableStateFlow(true)
    val createModeFlow: StateFlow<Boolean> = _createModeFlow.asStateFlow()
    //end

    private val _toastEvent = MutableSharedFlow<AppError>(replay = 0, extraBufferCapacity = 1)
    val toastEvent: SharedFlow<AppError> = _toastEvent.asSharedFlow()

    private val _modifyModeFlow = MutableStateFlow(false)
    val modifyModeFlow: StateFlow<Boolean> = _modifyModeFlow.asStateFlow()

    init {
        //createMode true면 추가 모드, 그 외 수정 모드
        _createModeFlow.value = params.createMode
    }

    fun insertCup() {
        if(!validateCheck()) {
            _toastEvent.tryEmit(AppError(100))
            return
        }
        launch {
            val cupName = cupNameFlow.value
            val cupAmount = cupAmountFlow.value
            val cupUnit = cupUnitFlow.value
            cupRepository.insertCup(cupName, cupAmount, cupUnit)
            _nextEvent.tryEmit(Unit)
        }
    }

    fun updateCup() {
        if(!validateCheck()) {
            _toastEvent.tryEmit(AppError(100))
            return
        }
        launch {
            val cupName = cupNameFlow.value
            val cupAmount = cupAmountFlow.value
            val cupUnit = cupUnitFlow.value
            cupRepository.updateCup(params.cupId, cupName, cupAmount, cupUnit)
            _nextEvent.tryEmit(Unit)
        }
    }

    fun deleteCup(cupId: String) {
        launch {
            cupRepository.deleteCup(cupId)
            // nextEvent 제거 - Fragment에서 직접 처리
        }
    }

    private fun validateCheck(): Boolean = cupNameFlow.value.isNotBlank()

    fun updateAll(list: List<Cup>) {
        launch {
            cupRepository.updateAll(list)
        }
    }

    fun setModifyMode(flag: Boolean) {
        _modifyModeFlow.value = flag
    }

    // 현재 선택된 컵 설정
    fun setCurrentSelectedCup(cupId: String?) {
        launch {
            cupRepository.setCurrentSelectedCupId(cupId)
        }
    }
}
package com.tkw.cup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import androidx.lifecycle.viewModelScope
import com.tkw.base.AppError
import com.tkw.base.BaseViewModel
import com.tkw.base.launch
import com.tkw.common.SingleLiveEvent
import com.tkw.domain.CupRepository
import com.tkw.domain.model.Cup
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

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
    val cupListLiveData: LiveData<List<Cup>> =
        cupRepository.getCupList().mapLatest {
            it.cupList  //Flow<CupListEntity> -> Flow<List<Cup>>으로 최신값 매핑
        }.asLiveData()

    // Compose용 StateFlow 버전
    @OptIn(ExperimentalCoroutinesApi::class)
    val cupListStateFlow: StateFlow<List<Cup>> =
        cupRepository.getCupList().mapLatest {
            it.cupList
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    //cup create fragment에서 관찰할 변수
    val cupNameLiveData = MutableLiveData(params.cupName)
    val cupAmountLiveData = MutableLiveData(params.cupAmount)
    val buttonName = MutableLiveData<String>()

    private val _nextEvent = SingleLiveEvent<Unit>()
    val nextEvent: LiveData<Unit> = _nextEvent

    private val _createMode = MutableLiveData(true)
    val createMode: LiveData<Boolean> = _createMode
    //end

    private val _toastEvent = SingleLiveEvent<AppError>()
    val toastEvent: LiveData<AppError> = _toastEvent

    private val _modifyMode = MutableLiveData(false)
    val modifyMode: LiveData<Boolean> = _modifyMode

    // Compose용 StateFlow 추가
    private val _modifyModeStateFlow = MutableStateFlow(false)
    val modifyModeStateFlow: StateFlow<Boolean> = _modifyModeStateFlow.asStateFlow()

    private val _cupNameStateFlow = MutableStateFlow(params.cupName)
    val cupNameStateFlow: StateFlow<String> = _cupNameStateFlow.asStateFlow()

    private val _cupAmountStateFlow = MutableStateFlow(params.cupAmount)
    val cupAmountStateFlow: StateFlow<Int> = _cupAmountStateFlow.asStateFlow()

    private val _createModeStateFlow = MutableStateFlow(params.createMode)
    val createModeStateFlow: StateFlow<Boolean> = _createModeStateFlow.asStateFlow()

    private val _buttonNameStateFlow = MutableStateFlow("")
    val buttonNameStateFlow: StateFlow<String> = _buttonNameStateFlow.asStateFlow()

    init {
        //createMode true면 추가 모드, 그 외 수정 모드
        _createMode.value = params.createMode
        _createModeStateFlow.value = params.createMode
        _cupNameStateFlow.value = params.cupName
        _cupAmountStateFlow.value = params.cupAmount
    }

    fun insertCup() {
        if(!validateCheck()) {
            _toastEvent.value = AppError(100)
            return
        }
        launch {
            val cupName = cupNameLiveData.value!!
            val cupAmount = cupAmountLiveData.value!!
            cupRepository.insertCup(cupName, cupAmount)
            _nextEvent.call()
        }
    }

    fun updateCup() {
        if(!validateCheck()) {
            _toastEvent.value = AppError(100)
            return
        }
        launch {
            val cupName = cupNameLiveData.value!!
            val cupAmount = cupAmountLiveData.value!!
            cupRepository.updateCup(params.cupId, cupName, cupAmount)
            _nextEvent.call()
        }
    }

    fun deleteCup(cupId: String) {
        launch {
            cupRepository.deleteCup(cupId)
            _nextEvent.call()
        }
    }

    private fun validateCheck(): Boolean = cupNameLiveData.value!!.isNotBlank()

    fun updateAll(list: List<Cup>) {
        launch {
            cupRepository.updateAll(list)
        }
    }

    fun setModifyMode(flag: Boolean) {
        _modifyMode.value = flag
        _modifyModeStateFlow.value = flag
    }

    // Compose용 함수들
    fun updateCupName(name: String) {
        cupNameLiveData.value = name
        _cupNameStateFlow.value = name
    }

    fun updateCupAmount(amount: Int) {
        cupAmountLiveData.value = amount
        _cupAmountStateFlow.value = amount
    }

    fun updateButtonName(name: String) {
        buttonName.value = name
        _buttonNameStateFlow.value = name
    }
}
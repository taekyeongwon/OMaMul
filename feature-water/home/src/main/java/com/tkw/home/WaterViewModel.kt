package com.tkw.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tkw.base.BaseViewModel
import com.tkw.base.launch
import com.tkw.common.util.DateTimeUtils
import com.tkw.domain.CupRepository
import com.tkw.domain.PrefDataRepository
import com.tkw.domain.SettingRepository
import com.tkw.domain.WaterRepository
import com.tkw.domain.model.Cup
import com.tkw.domain.model.DayOfWater
import com.tkw.domain.model.Water
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WaterViewModel
@Inject constructor(
    private val waterRepository: WaterRepository,
    private val cupRepository: CupRepository,
    private val settingRepository: SettingRepository,
    private val prefDataRepository: PrefDataRepository,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel() {

    //최초 진입 여부
    private val initFlag = prefDataRepository.fetchInitialFlag()
    suspend fun getInitFlag(): Boolean = initFlag.first()

    // Compose에서 사용하기 위한 StateFlow 버전 (null로 초기화하여 로딩 상태 처리)
    val initFlagStateFlow: StateFlow<Boolean?> = initFlag.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Dialog 상태 관리
    private val _showWaterIntakeDialog = MutableStateFlow(false)
    val showWaterIntakeDialog = _showWaterIntakeDialog.asStateFlow()

    fun showWaterIntakeDialog() {
        _showWaterIntakeDialog.value = true
    }

    fun hideWaterIntakeDialog() {
        _showWaterIntakeDialog.value = false
    }

    //현재 날짜
    private val dateStringFlow = MutableStateFlow(DateTimeUtils.Date.getToday())

    //현재 날짜로 조회한 DayOfWater, 마지막 데이터 제거하기 위해 관찰
    @OptIn(ExperimentalCoroutinesApi::class)
    val amountLiveData: StateFlow<DayOfWater> = dateStringFlow.flatMapLatest { date ->
        waterRepository.getAmountByFlow(date)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DayOfWater("", listOf())
    )

    //메인화면에 표시할 컵 리스트
    @OptIn(ExperimentalCoroutinesApi::class)
    val cupListStateFlow: StateFlow<List<Cup>> =
        cupRepository.getCupList().mapLatest {
            it.cupList
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            listOf()
        )

    //컵 관리 화면 이동 후 돌아왔을 때 위치 저장용
    private val _cupPagerScrollPosition = MutableStateFlow(0)
    val cupPagerScrollPosition: StateFlow<Int> = _cupPagerScrollPosition.asStateFlow()

    //섭취량 변경 완료 여부 (SharedFlow로 변경)
    private val _amountSaveEvent = MutableSharedFlow<Unit>()
    val amountSaveEvent: SharedFlow<Unit> = _amountSaveEvent.asSharedFlow()

    //date값 변경에 따라 flow에서 새로운 DayOfWater 객체 collect하기 위한 메서드
    fun setToday() {
        dateStringFlow.value = DateTimeUtils.Date.getToday()
    }

    fun addCount(amount: Int, date: String) {
        launch {
            waterRepository.addAmount(dateStringFlow.value, amount, date)
        }
    }

    fun removeCount(obj: Water) {
        launch {
            waterRepository.deleteAmount(dateStringFlow.value, obj.dateTime)
        }
    }

    suspend fun getIntakeAmount(): Int =
        settingRepository.getSetting().first().intake

    fun saveIntakeAmount(amount: Int) {
        launch {
            settingRepository.saveIntake(amount)
            _amountSaveEvent.emit(Unit)
        }
    }

    fun updateCupPagerPosition(position: Int) {
        _cupPagerScrollPosition.value = position
    }
}
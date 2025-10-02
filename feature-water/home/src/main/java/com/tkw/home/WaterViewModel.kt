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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    private val alarmRepository: com.tkw.domain.AlarmRepository,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel() {

    // 설정 정보 (단위 변환용)
    val settingsFlow = settingRepository.getSetting().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        com.tkw.domain.model.Settings()
    )

    //최초 진입 여부
    private val initFlag = prefDataRepository.fetchInitialFlag()
    suspend fun getInitFlag(): Boolean = initFlag.first()

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
    val cupListLiveData: StateFlow<List<Cup>> =
        cupRepository.getCupList().mapLatest {
            it.cupList
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            listOf()
        )

    //현재 선택된 컵 ID
    val currentSelectedCupIdFlow: StateFlow<String?> =
        cupRepository.getCurrentSelectedCupId().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    //현재 선택된 컵 정보
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentSelectedCupFlow: StateFlow<Cup?> =
        currentSelectedCupIdFlow.flatMapLatest { cupId ->
            if (cupId != null) {
                cupRepository.getCupById(cupId)
            } else {
                MutableStateFlow(null)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    //컵 관리 화면 이동 후 돌아왔을 때 위치 저장용
    val cupPagerScrollPosition = MutableStateFlow(0)

    //섭취량 변경 완료 여부
    private val _amountSaveEvent = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
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
            _amountSaveEvent.tryEmit(Unit)
        }
    }

    // 현재 선택된 컵 설정
    fun setCurrentSelectedCup(cupId: String?) {
        launch {
            cupRepository.setCurrentSelectedCupId(cupId)
        }
    }

    // 컵 리스트가 비어있지 않은 경우 첫 번째 컵을 기본 선택으로 설정
    fun initCurrentCupIfNeeded() {
        launch {
            val currentSelected = currentSelectedCupIdFlow.first()
            val cupList = cupListLiveData.first()

            if (currentSelected == null && cupList.isNotEmpty()) {
                setCurrentSelectedCup(cupList.first().cupId)
            }
        }
    }

    // 다음 알람까지 남은 시간 (밀리초)
    val nextAlarmTimeFlow: StateFlow<Long> =
        alarmRepository.getRemainAlarmTime().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            -1L
        )
}
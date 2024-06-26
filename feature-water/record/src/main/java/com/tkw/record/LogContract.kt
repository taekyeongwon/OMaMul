package com.tkw.record

import com.tkw.base.IEvent
import com.tkw.base.ISideEffect
import com.tkw.base.IState
import com.tkw.domain.model.DayOfWaterList
import com.tkw.domain.model.Water

class LogContract {
    sealed class Event: IEvent {
        class DayAmountEvent(val move: Move): Event()
        class WeekAmountEvent(val move: Move): Event()
        class MonthAmountEvent(val move: Move): Event()
        object ShowAddDialog: Event()
        class ShowEditDialog(val water: Water): Event()
        class RemoveDayAmount(val water: Water): Event()
    }

    sealed class SideEffect: ISideEffect {
        class ShowEditDialog(val water: Water?): SideEffect()
    }

    sealed class State: IState {
        data class Loading(val flag: Boolean): State()
        data class Complete(val data: DayOfWaterList, val unit: DateUnit): State()
        object Error: State()
    }

    enum class Move {
        LEFT, RIGHT, INIT
    }

    enum class DateUnit {
        DAY, WEEK, MONTH
    }
}
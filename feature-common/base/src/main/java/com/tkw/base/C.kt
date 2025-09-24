package com.tkw.base

object C {
    val FirstInstallFlag = "firstInstallFlag"
    enum class CupViewType(val viewType: Int) {
        CUP(0)//, ADD(1)
    }

    enum class CupListViewType(val viewType: Int) {
        NORMAL(0), DRAG(1)
    }

    enum class AlarmListViewType(val viewType: Int) {
        NORMAL(0), DRAG(1)
    }
}
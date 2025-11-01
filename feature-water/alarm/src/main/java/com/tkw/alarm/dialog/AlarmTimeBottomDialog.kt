package com.tkw.alarm.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tkw.alarm.databinding.DialogTimepickerBinding
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.ui.dialog.CustomBottomDialog
import java.time.LocalTime

class AlarmTimeBottomDialog(
    private val selectedTime: LocalTime? = null,
    private val resultListener: (LocalTime) -> Unit
) : CustomBottomDialog<DialogTimepickerBinding>() {
    override var childBinding by autoCleared<DialogTimepickerBinding>()
    override var buttonCount: Int = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        childBinding = DialogTimepickerBinding.inflate(layoutInflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        val hour = selectedTime?.hour ?: 9
        val minute = selectedTime?.minute ?: 0

        childBinding.apply {
            // AM/PM 설정
            val amPmArray = arrayOf("오전", "오후")
            npAmpm.minValue = 0
            npAmpm.maxValue = 1
            npAmpm.displayedValues = amPmArray
            npAmpm.value = if (hour < 12) 0 else 1
            npAmpm.wrapSelectorWheel = false

            // 시간 설정 (1~12)
            npHour.minValue = 1
            npHour.maxValue = 12
            npHour.value = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
            npHour.wrapSelectorWheel = true

            // 분 설정 (0~59)
            npMinute.minValue = 0
            npMinute.maxValue = 59
            npMinute.value = minute
            npMinute.wrapSelectorWheel = true
        }
    }

    private fun initListener() {
        setButtonListener(
            cancelAction = {
                dismiss()
            },
            confirmAction = {
                sendSelectTime()
                dismiss()
            }
        )
    }

    private fun sendSelectTime() {
        val amPm = childBinding.npAmpm.value // 0: AM, 1: PM
        val hour12 = childBinding.npHour.value
        val minute = childBinding.npMinute.value

        // 12시간제를 24시간제로 변환
        val hour24 = when {
            amPm == 0 && hour12 == 12 -> 0 // 오전 12시 = 0시
            amPm == 1 && hour12 == 12 -> 12 // 오후 12시 = 12시
            amPm == 1 -> hour12 + 12 // 오후 1~11시
            else -> hour12 // 오전 1~11시
        }

        val selectedTime = DateTimeUtils.Time.getLocalTime(hour24, minute)
        resultListener(selectedTime)
    }
}

package com.tkw.ui.custom

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import com.tkw.ui.R

class WaterAmountPicker
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = android.R.attr.numberPickerStyle)
    : NumberPicker(context, attrs, defStyle) {  //최소, 최대 및 단위, 값 간격 변경 가능하도록, 현재값 가져오기

    // 간단한 UnitType enum (domain 모듈 의존성 없이 사용)
    enum class UnitType(val mlPerUnit: Double) {
        ML(1.0),
        L(1000.0),
        CUP(200.0),
        FL_OZ(29.5735)
    }

    private var minValue = 0
    private var maxValue = 0
    private var interval = 0
    private var currentMlValue = 0  // 현재 ml 기준 값 저장

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.WaterAmountPicker,
            0,
            0
        )
        with(typedArray) {
            try {
                minValue = getInt(R.styleable.WaterAmountPicker_minValue, 100)
                maxValue = getInt(R.styleable.WaterAmountPicker_maxValue, 3000)
                interval = getInt(R.styleable.WaterAmountPicker_interval, 5)

                initNumberPicker()
            } finally {
                recycle()
            }
        }
    }

    private fun initNumberPicker() {
        val values = getIntervalDisplayedValues(interval)
        displayedValues = values.toTypedArray()
        setMinValue(0)
        setMaxValue(values.size - 1)
        value = (maxValue - minValue) / 2
        wrapSelectorWheel = false
        setInputTypeNumber(this)
    }

    /**
     * xml에서 value값 안 줬다면 최대~최소값의 평균 값으로 initNumberPicker에서 기본값 설정.
     * xml에서 value값 줬다면 해당 값으로 설정
     *
     * displayedValues를 설정함으로써 value에 값을 set하면 해당 값이 배열의 인덱스로 들어가게 됨.
     * 직접 값을 세팅 해주기 위해 재정의.
     */
    override fun setValue(value: Int) {
        val newValue = (value - minValue) / interval
        currentMlValue = value
        super.setValue(newValue)
    }

    fun getCurrentValue(): Int {
        val currentIndex = super.getValue()
        // 배열 범위 안전 체크
        if (displayedValues == null || currentIndex < 0 || currentIndex >= displayedValues.size) {
            return currentMlValue.coerceIn(minValue, maxValue)
        }
        val value = displayedValues[currentIndex].toInt()
        currentMlValue = value
        return value
    }

    fun updateUnit(unitType: UnitType, currentValue: Int) {
        // 현재 ml 값 저장
        currentMlValue = if (currentValue > 0) currentValue else {
            // 안전하게 현재 값 가져오기
            val currentIndex = super.getValue()
            if (displayedValues != null && currentIndex >= 0 && currentIndex < displayedValues.size) {
                displayedValues[currentIndex].toInt()
            } else {
                200 // 기본값
            }
        }

        // 단위에 따라 min/max/interval 변경
        when (unitType) {
            UnitType.ML -> {
                minValue = 50
                maxValue = 2000
                interval = 50
            }
            UnitType.L -> {
                minValue = 1
                maxValue = 5
                interval = 1
            }
            UnitType.CUP -> {
                minValue = 1
                maxValue = 10
                interval = 1
            }
            UnitType.FL_OZ -> {
                minValue = 2
                maxValue = 70
                interval = 2
            }
        }

        // NumberPicker 재초기화 (순서 중요!)
        val values = getIntervalDisplayedValues(interval)
        
        // 1. 먼저 현재 value를 0으로 리셋
        super.setValue(0)
        
        // 2. displayedValues를 null로 초기화
        displayedValues = null
        
        // 3. min/max 범위 설정
        setMinValue(0)
        setMaxValue(values.size - 1)
        
        // 4. 새로운 displayedValues 설정
        displayedValues = values.toTypedArray()
        
        wrapSelectorWheel = false

        // 현재 ml 값을 새 단위로 변환하여 설정
        val convertedValue = when (unitType) {
            UnitType.ML -> currentMlValue
            UnitType.L -> kotlin.math.round(currentMlValue / 1000.0).toInt().coerceIn(minValue, maxValue)
            UnitType.CUP -> kotlin.math.round(currentMlValue / 200.0).toInt().coerceIn(minValue, maxValue)
            UnitType.FL_OZ -> kotlin.math.round(currentMlValue / 29.5735).toInt().coerceIn(minValue, maxValue)
        }

        // interval에 맞춰 반올림 (가장 가까운 interval 배수로)
        val adjustedValue = ((convertedValue - minValue).toDouble() / interval).let { ratio ->
            minValue + (kotlin.math.round(ratio) * interval).toInt()
        }.coerceIn(minValue, maxValue)

        // 변환된 값을 표시 (안전한 인덱스 계산)
        val index = ((adjustedValue - minValue) / interval).coerceIn(0, values.size - 1)
        super.setValue(index)

        // 현재 ml 값 업데이트 (adjustedValue 기준으로)
        currentMlValue = when (unitType) {
            UnitType.ML -> adjustedValue
            UnitType.L -> adjustedValue * 1000
            UnitType.CUP -> adjustedValue * 200
            UnitType.FL_OZ -> (adjustedValue * 29.5735).toInt()
        }
    }

    fun getCurrentValueInMl(unitType: UnitType): Int {
        val currentIndex = super.getValue()
        // 배열 범위 안전 체크
        if (displayedValues == null || currentIndex < 0 || currentIndex >= displayedValues.size) {
            return currentMlValue
        }
        
        val currentDisplayValue = displayedValues[currentIndex].toInt()
        return when (unitType) {
            UnitType.ML -> currentDisplayValue
            UnitType.L -> currentDisplayValue * 1000
            UnitType.CUP -> currentDisplayValue * 200
            UnitType.FL_OZ -> (currentDisplayValue * 29.5735).toInt()
        }
    }

    fun getIntervalDisplayedValues(interval: Int): ArrayList<String> {
        val displayedArray = arrayListOf<String>()
        val index = (maxValue - minValue) / interval
        for(i in 0 .. index) {
            val value = minValue + (i * interval)
            displayedArray.add(value.toString())
        }
        return displayedArray
    }

    /**
     * number picker 선택 시 키보드 inputType 설정
     * https://stackoverflow.com/questions/16793414/android-number-picker-keyboard-type
     */
    private fun setInputTypeNumber(vg: ViewGroup) {
        (0..vg.childCount).map { vg.getChildAt(it) }.forEach {
            when (it) {
                is ViewGroup -> setInputTypeNumber(it) // recurse
                is EditText -> it.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }
    }
}
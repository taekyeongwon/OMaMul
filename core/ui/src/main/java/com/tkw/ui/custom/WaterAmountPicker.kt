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

    //인덱스 값이 아닌 displayedValue 값 리턴
    fun getCurrentValue(): Int {
        val value = displayedValues[super.getValue()].toInt()
        currentMlValue = value
        return value
    }

    /**
     * 단위 변경 시 호출
     * ml 기준 값을 유지한 채로 단위만 변경하여 표시
     */
    fun updateUnit(unitType: UnitType, currentValue: Int) {
        // 현재 ml 값 저장
        currentMlValue = if (currentValue > 0) currentValue else this.getCurrentValue()

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

        // NumberPicker 재초기화
        val values = getIntervalDisplayedValues(interval)
        displayedValues = values.toTypedArray()
        setMinValue(0)
        setMaxValue(values.size - 1)
        wrapSelectorWheel = false

        // 현재 ml 값을 새 단위로 변환하여 설정
        val convertedValue = when (unitType) {
            UnitType.ML -> currentMlValue
            UnitType.L -> (currentMlValue / 1000.0).toInt().coerceIn(minValue, maxValue)
            UnitType.CUP -> (currentMlValue / 200.0).toInt().coerceIn(minValue, maxValue)
            UnitType.FL_OZ -> (currentMlValue / 29.5735).toInt().coerceIn(minValue, maxValue)
        }

        // 변환된 값을 표시
        val index = (convertedValue - minValue) / interval
        super.setValue(index.coerceIn(0, values.size - 1))
    }

    /**
     * 현재 표시된 값을 ml로 변환하여 반환
     */
    fun getCurrentValueInMl(unitType: UnitType): Int {
        val currentDisplayValue = getCurrentValue()
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
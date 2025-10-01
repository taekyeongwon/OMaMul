package com.tkw.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.NumberPicker

class UnitPicker
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = android.R.attr.numberPickerStyle
) : NumberPicker(context, attrs, defStyle) {

    init {
        minValue = 0
        maxValue = 2
        wrapSelectorWheel = false
        descendantFocusability = FOCUS_BLOCK_DESCENDANTS
        displayedValues = arrayOf("ml, L", "fl oz", "컵")
    }

    fun getCurrentValue(): String {
        return displayedValues[super.getValue()]
    }
}
package com.tkw.cup

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.tkw.domain.model.Cup
import com.tkw.ui.custom.WaterAmountPicker

object CupBindingAdapter {
    /**
     * 물의 양 number picker 양방향 바인딩
     */
    @JvmStatic
    @BindingAdapter("value")
    fun setValue(view: WaterAmountPicker, value: Int) {
        val old = view.value
        if(old != value) {
            view.value = value
        }
    }

    @JvmStatic
    @BindingAdapter("valueAttrChanged")
    fun setValueChanged(view: WaterAmountPicker, listener: InverseBindingListener) {
        view.setOnValueChangedListener { _, _, _ ->
            listener.onChange()
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    fun getValue(view: WaterAmountPicker): Int {
        return view.getCurrentValue()
    }

    /**
     * 컵의 용량을 컵 생성/수정 시 선택한 단위로 표시
     * cupAmount는 ml 기준으로 저장되므로, cupUnit으로 변환하여 표시
     */
    @JvmStatic
    @BindingAdapter("cupInfo")
    fun setCupInfo(view: TextView, cup: Cup?) {
        cup?.let {
            // ml 값을 선택한 단위로 변환
            val convertedValue = it.cupUnit.fromMl(it.cupAmount)

            // 소수점 처리 (정수면 정수로, 소수점 있으면 소수점 표시)
            val formattedValue = if (convertedValue % 1.0 == 0.0) {
                convertedValue.toInt().toString()
            } else {
                String.format("%.1f", convertedValue)
            }

            view.text = "$formattedValue${it.cupUnit.displayName}"
        }
    }

    /**
     * 이전 버전 호환성을 위한 unit 어트리뷰트 (ml 단위로만 표시)
     */
    @JvmStatic
    @BindingAdapter("unit")
    fun setUnit(view: TextView, value: Int) {
        view.text = "${value}ml"
    }
}
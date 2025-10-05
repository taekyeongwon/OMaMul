package com.tkw.home.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.tkw.common.autoCleared
import com.tkw.domain.model.UnitType
import com.tkw.home.WaterViewModel
import com.tkw.home.databinding.DialogWaterIntakeBinding
import com.tkw.ui.custom.WaterAmountPicker
import com.tkw.ui.dialog.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WaterIntakeDialog : CustomDialog() {
    private var dataBinding by autoCleared<DialogWaterIntakeBinding>()
    private val viewModel: WaterViewModel by activityViewModels()

    private var currentUnitType: UnitType = UnitType.ML
    private var currentMlValue: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DialogWaterIntakeBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
        initListener()
    }

    private fun initView() {
        setView(dataBinding.root)

        lifecycleScope.launch {
            // 현재 설정된 단위 가져오기
            currentUnitType = viewModel.settingsFlow.first().getUnitType()

            // 현재 설정된 섭취량(ml) 가져오기
            currentMlValue = viewModel.getIntakeAmount()

            // 단위에 맞게 NumberPicker 업데이트
            val pickerUnitType = when (currentUnitType) {
                UnitType.ML -> WaterAmountPicker.UnitType.ML
                UnitType.L -> WaterAmountPicker.UnitType.L
                UnitType.CUP -> WaterAmountPicker.UnitType.CUP
                UnitType.FL_OZ -> WaterAmountPicker.UnitType.FL_OZ
            }

            dataBinding.npAmount.updateUnit(pickerUnitType, currentMlValue)
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.amountSaveEvent.collect {
                dismiss()
            }
        }
    }

    private fun initListener() {
        setButtonListener(
            cancelAction = {
                dismiss()
            },
            confirmAction = {
                // 현재 단위에 맞게 ml로 변환하여 저장
                val pickerUnitType = when (currentUnitType) {
                    UnitType.ML -> WaterAmountPicker.UnitType.ML
                    UnitType.L -> WaterAmountPicker.UnitType.L
                    UnitType.CUP -> WaterAmountPicker.UnitType.CUP
                    UnitType.FL_OZ -> WaterAmountPicker.UnitType.FL_OZ
                }
                val amountInMl = dataBinding.npAmount.getCurrentValueInMl(pickerUnitType)
                viewModel.saveIntakeAmount(amountInMl)
            }
        )
    }
}
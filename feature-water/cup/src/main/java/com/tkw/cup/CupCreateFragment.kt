package com.tkw.cup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tkw.common.autoCleared
import kotlinx.coroutines.launch
import com.tkw.cup.databinding.FragmentCupCreateBinding
import com.tkw.domain.model.Cup
import com.tkw.domain.model.UnitType
import com.tkw.ui.custom.WaterAmountPicker
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback

// UnitType 변환 확장 함수
private fun UnitType.toPickerUnitType(): WaterAmountPicker.UnitType {
    return when (this) {
        UnitType.ML -> WaterAmountPicker.UnitType.ML
        UnitType.L -> WaterAmountPicker.UnitType.L
        UnitType.CUP -> WaterAmountPicker.UnitType.CUP
        UnitType.FL_OZ -> WaterAmountPicker.UnitType.FL_OZ
    }
}

@AndroidEntryPoint
class CupCreateFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentCupCreateBinding>()
    private val viewModel: CupViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<CupViewModel.AssistFactory> { factory ->
                val cupArgs: CupCreateFragmentArgs by navArgs()
                factory.create(cupArgs.cupArgument ?: Cup())
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentCupCreateBinding.inflate(layoutInflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
        initListener()
    }

    private fun initView() {
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CupCreateFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.createModeFlow.collect {
                        viewModel.buttonNameFlow.value =
                            if(it) getString(com.tkw.ui.R.string.add)
                            else getString(com.tkw.ui.R.string.modify)
                    }
                }

                launch {
                    viewModel.nextEvent.collect {
                        findNavController().navigateUp()
                    }
                }

                launch {
                    viewModel.toastEvent.collect {
                        Toast.makeText(
                            requireContext(),
                            it.getMessage(requireContext()),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun initListener() {
        dataBinding.btnNext.setOnClickListener {
            val isCreate = viewModel.createModeFlow.value
            val currentUnit = viewModel.cupUnitFlow.value
            // ml 기준으로 변환하여 저장 (기존 방식 유지)
            val mlAmount = dataBinding.npAmount.getCurrentValueInMl(currentUnit.toPickerUnitType())
            viewModel.cupAmountFlow.value = mlAmount

            if(isCreate) viewModel.insertCup()
            else viewModel.updateCup()
        }

        // 단위 선택 리스너
        dataBinding.rgUnit.setOnCheckedChangeListener { _, checkedId ->
            val selectedUnit = when (checkedId) {
                R.id.rb_ml -> UnitType.ML
                R.id.rb_liter -> UnitType.L
                R.id.rb_cup -> UnitType.CUP
                R.id.rb_fl_oz -> UnitType.FL_OZ
                else -> UnitType.ML
            }

            // 현재 선택된 값의 ml 환산값을 구해서 새 단위로 변환하여 표시
            val currentUnit = viewModel.cupUnitFlow.value
            val currentMlValue = dataBinding.npAmount.getCurrentValueInMl(currentUnit.toPickerUnitType())

            dataBinding.npAmount.updateUnit(selectedUnit.toPickerUnitType(), currentMlValue)
            viewModel.cupUnitFlow.value = selectedUnit
        }

        // 초기 선택 상태 설정
        val currentUnit = viewModel.cupUnitFlow.value
        dataBinding.rgUnit.check(
            when (currentUnit) {
                UnitType.ML -> R.id.rb_ml
                UnitType.L -> R.id.rb_liter
                UnitType.CUP -> R.id.rb_cup
                UnitType.FL_OZ -> R.id.rb_fl_oz
            }
        )

        // 초기 NumberPicker 설정 (저장된 값은 항상 ml 기준)
        val initialMlValue = viewModel.cupAmountFlow.value
        dataBinding.npAmount.updateUnit(currentUnit.toPickerUnitType(), initialMlValue)
    }
}
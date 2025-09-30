package com.tkw.cup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tkw.common.autoCleared
import com.tkw.cup.databinding.FragmentCupCreateBinding
import com.tkw.domain.model.Cup
import com.tkw.domain.model.UnitType
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback

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
        viewModel.createMode.observe(viewLifecycleOwner) {
            viewModel.buttonName.value =
                if(it) getString(com.tkw.ui.R.string.add)
                else getString(com.tkw.ui.R.string.modify)
        }

        viewModel.nextEvent.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.toastEvent.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                it.getMessage(requireContext()),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initListener() {
        dataBinding.btnNext.setOnClickListener {
            val isCreate = viewModel.createMode.value ?: false
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
            viewModel.cupUnitLiveData.value = selectedUnit
        }

        // 초기 선택 상태 설정
        val currentUnit = viewModel.cupUnitLiveData.value ?: UnitType.ML
        dataBinding.rgUnit.check(
            when (currentUnit) {
                UnitType.ML -> R.id.rb_ml
                UnitType.L -> R.id.rb_liter
                UnitType.CUP -> R.id.rb_cup
                UnitType.FL_OZ -> R.id.rb_fl_oz
            }
        )
    }
}
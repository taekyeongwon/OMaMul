package com.tkw.home.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.tkw.common.autoCleared
import com.tkw.home.WaterViewModel
import com.tkw.home.databinding.DialogWaterIntakeBinding
import com.tkw.ui.dialog.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WaterIntakeDialog : CustomDialog() {
    private var dataBinding by autoCleared<DialogWaterIntakeBinding>()
    private val viewModel: WaterViewModel by activityViewModels()

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
            dataBinding.npAmount.setValue(viewModel.getIntakeAmount())
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
                val amount = dataBinding.npAmount.getCurrentValue()
                viewModel.saveIntakeAmount(amount)
            }
        )
    }
}
package com.tkw.setting.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tkw.common.autoCleared
import com.tkw.setting.SettingViewModel
import com.tkw.setting.databinding.DialogUnitBinding
import com.tkw.ui.dialog.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UnitDialog(
    private val unit: Int
): CustomDialog() {
    private var dataBinding by autoCleared<DialogUnitBinding>()
    private val viewModel: SettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DialogUnitBinding.inflate(inflater, container, false)
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
        isCancelable = false
        dataBinding.unitPicker.value = unit
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.nextEvent.collect {
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
                viewModel.saveUnit(dataBinding.unitPicker.value)
            }
        )
    }
}
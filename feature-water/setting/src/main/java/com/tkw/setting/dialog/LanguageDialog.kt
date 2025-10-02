package com.tkw.setting.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tkw.common.LocaleHelper
import com.tkw.common.autoCleared
import com.tkw.setting.R
import com.tkw.setting.SettingViewModel
import com.tkw.setting.databinding.DialogLanguageBinding
import com.tkw.ui.dialog.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.Locale

@AndroidEntryPoint
class LanguageDialog(
    private val currentLang: String
) : CustomDialog() {
    private var dataBinding by autoCleared<DialogLanguageBinding>()
    private val viewModel: SettingViewModel by viewModels()
    private var selectedLanguage = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DialogLanguageBinding.inflate(inflater, container, false)
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
        when (currentLang) {
            Locale.KOREAN.language -> dataBinding.rgLanguage.check(R.id.rb_ko)
            Locale.ENGLISH.language -> dataBinding.rgLanguage.check(R.id.rb_en)
            Locale.JAPANESE.language -> dataBinding.rgLanguage.check(R.id.rb_jp)
            Locale.CHINESE.language -> dataBinding.rgLanguage.check(R.id.rb_cn)
        }
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.nextEvent.collect {
                LocaleHelper.setApplicationLocales(selectedLanguage)
                LocaleHelper.restartApplication(WeakReference(requireActivity()), requireActivity().javaClass)
            }
        }
    }

    private fun initListener() {
        dataBinding.rgLanguage.setOnCheckedChangeListener { group, checkedId ->
            selectedLanguage = when (checkedId) {
                R.id.rb_ko -> Locale.KOREAN.language
                R.id.rb_en -> Locale.ENGLISH.language
                R.id.rb_jp -> Locale.JAPANESE.language
                R.id.rb_cn -> Locale.CHINESE.language
                else -> Locale.KOREAN.language
            }
        }

        setButtonListener(
            cancelAction = {
                dismiss()
            },
            confirmAction = {
                if (selectedLanguage.isNotEmpty()) {
                    viewModel.saveLanguage(selectedLanguage)
                } else {
                    dismiss()
                }
            }
        )
    }
}
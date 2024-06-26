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
        dataBinding.viewModel = viewModel
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
    }
}
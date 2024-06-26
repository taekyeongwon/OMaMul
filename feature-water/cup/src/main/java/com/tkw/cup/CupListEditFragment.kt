package com.tkw.cup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tkw.common.autoCleared
import com.tkw.cup.adapter.CupListAdapter
import com.tkw.cup.databinding.FragmentCupListEditBinding
import com.tkw.domain.model.Cup
import com.tkw.ui.DividerDecoration
import com.tkw.ui.ItemTouchHelperCallback
import com.tkw.ui.OnItemDrag
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback

@AndroidEntryPoint
class CupListEditFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentCupListEditBinding>()
    private val viewModel: CupViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<CupViewModel.AssistFactory> { factory ->
                factory.create(Cup())
            }
        }
    )
    private lateinit var cupListAdapter: CupListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val saveCupList: ArrayList<Cup> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentCupListEditBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
        initListener()
    }

    private fun initView() {
        cupListAdapter = CupListAdapter(dragListener = object : OnItemDrag<Cup> {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                itemTouchHelper.startDrag(viewHolder)
            }

            override fun onStopDrag(list: List<Cup>) {
                saveCupList.clear()
                saveCupList.addAll(list)
            }
        })
        cupListAdapter.setDraggable(true)
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(cupListAdapter, false))

        dataBinding.rvCupList.apply {
            adapter = cupListAdapter
            addItemDecoration(DividerDecoration(10f))
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun initObserver() {
        viewModel.cupListLiveData.observe(viewLifecycleOwner) {
            saveCupList.addAll(it)
            cupListAdapter.submitList(it)
        }

        viewModel.nextEvent.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun initListener() {
        dataBinding.btnComplete.setOnClickListener {
            if(saveCupList.isNotEmpty()) {
                viewModel.updateAll(saveCupList)
            }
        }
    }
}
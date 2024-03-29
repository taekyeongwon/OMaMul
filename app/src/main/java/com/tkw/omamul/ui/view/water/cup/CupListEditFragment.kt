package com.tkw.omamul.ui.view.water.cup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tkw.omamul.common.ItemTouchHelperCallback
import com.tkw.omamul.common.OnItemDrag
import com.tkw.omamul.common.autoCleared
import com.tkw.omamul.common.getViewModelFactory
import com.tkw.omamul.data.model.Cup
import com.tkw.omamul.data.model.Draggable
import com.tkw.omamul.databinding.FragmentCupListEditBinding
import com.tkw.omamul.ui.custom.DividerDecoration
import com.tkw.omamul.ui.view.water.cup.adapter.CupListAdapter

class CupListEditFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentCupListEditBinding>()
    private val viewModel: CupViewModel by viewModels { getViewModelFactory(null) }
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
        cupListAdapter = CupListAdapter(dragListener = object : OnItemDrag {
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
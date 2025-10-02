package com.tkw.alarm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tkw.alarm.adapter.AlarmListAdapter
import com.tkw.alarm.databinding.FragmentAlarmModeCustomBinding
import com.tkw.alarm.dialog.CustomAlarmBottomDialog
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.domain.model.Alarm
import com.tkw.ui.ItemTouchHelperCallback
import com.tkw.ui.OnItemDrag
import com.tkw.ui.decoration.VerticalSpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmModeCustomFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentAlarmModeCustomBinding>()
    private val viewModel: WaterAlarmViewModel by hiltNavGraphViewModels(R.id.alarm_nav_graph)
    private var isModified = false

    private lateinit var alarmListAdapter: AlarmListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    private val draggedList: ArrayList<Alarm> = ArrayList()

    private val adapterEditListener: (Int) -> Unit = { position ->
        val currentAlarm = alarmListAdapter.currentList[position]
        showCustomAlarmDialog(currentAlarm)
    }

    private val deleteCheckListener: (Int, Boolean) -> Unit = { position, isChecked ->
        alarmListAdapter.currentList[position].isChecked = isChecked
        setDeleteBtnVisibility()
    }

    private val adapterLongClickListener: (Int) -> Unit = { position ->
        alarmListAdapter.currentList[position].isChecked = true
        modeChanged(true)
    }

    private val dragListener = object: OnItemDrag<Alarm> {
        override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
            itemTouchHelper.startDrag(viewHolder)
        }

        override fun onStopDrag(list: List<Alarm>) {
            draggedList.clear()
            draggedList.addAll(list)
            viewModel.updateList(list)
        }
    }

    private val alarmOnOffListener = { position: Int, isChecked: Boolean ->
        val alarm = alarmListAdapter.currentList[position]
        setAlarm(alarm.copy(enabled = isChecked))
    }

    private val positionObserver = object: RecyclerView.AdapterDataObserver() {
        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            if(fromPosition == 0 || toPosition == 0) {
                dataBinding.rvAlarm.scrollToPosition(0)
            }
        }
    }

    private val callback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if(isModified) {
                clearChecked()
                modeChanged(false)
            } else {
                findNavController().navigateUp()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentAlarmModeCustomBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
        initListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        alarmListAdapter.unregisterAdapterDataObserver(positionObserver)
    }

    private fun initView() {
        initAdapter()
        initRecyclerView()
    }

    private fun initAdapter() {
        alarmListAdapter = AlarmListAdapter(
            editListener = adapterEditListener,
            deleteCheckListener = deleteCheckListener,
            longClickListener = adapterLongClickListener,
            dragListener = dragListener,
            alarmOnOffListener = alarmOnOffListener
        )
        alarmListAdapter.registerAdapterDataObserver(positionObserver)

    }

    private fun initRecyclerView() {
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(alarmListAdapter, false))
        dataBinding.rvAlarm.apply {
            adapter = alarmListAdapter
            itemTouchHelper.attachToRecyclerView(this)
            addItemDecoration(VerticalSpaceItemDecoration(20))
            itemAnimator = null
//            setHasFixedSize(true)
        }
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.customAlarmListFlow.collect {
                    val list = draggedList.ifEmpty { it.alarmList }
                    alarmListAdapter.submitList(list.map { it.copy() }) {
                        draggedList.clear()
                        dataChanged()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nextEvent.collect {
                    modeChanged(false)
                }
            }
        }
    }

    private fun dataChanged() {
        if(alarmListAdapter.itemCount == 0) {
            dataBinding.rvAlarm.visibility = View.GONE
            dataBinding.tvEmptyAlarm.visibility = View.VISIBLE
        } else {
            dataBinding.rvAlarm.visibility = View.VISIBLE
            dataBinding.tvEmptyAlarm.visibility = View.GONE
        }
        dataBinding.ivEdit.visibility =
            if (alarmListAdapter.itemCount > 1 && !isModified) View.VISIBLE
            else View.INVISIBLE
    }

    private fun initListener() {
        dataBinding.btnAdd.setOnClickListener {
            //새로 추가할 알람 객체 생성
            val currentTime = System.currentTimeMillis()
            val alarmId = DateTimeUtils.DateTime.getFormatTrim(currentTime)
            val alarm = Alarm(
                alarmId,
                currentTime,
                weekList = listOf()
            )
            showCustomAlarmDialog(alarm)
        }

        dataBinding.ivEdit.setOnClickListener {
            modeChanged(true)
        }

        dataBinding.btnDelete.setOnClickListener {
            alarmListAdapter.currentList
                .filter { it.isChecked }
                .also {
                    viewModel.deleteAlarm(it)
                }
        }
    }

    private fun showCustomAlarmDialog(alarm: Alarm) {
        val dialog = CustomAlarmBottomDialog(
            alarm = alarm,
            resultListener = { result ->
                setAlarm(result)
            }
        )
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun setAlarm(alarm: Alarm) {
        lifecycleScope.launch {
            viewModel.setCustomAlarm(alarm)
        }
    }

    private fun modeChanged(isModified: Boolean) {
        this.isModified = isModified
        alarmListAdapter.setDraggable(isModified)
        if(isModified) {
            setDeleteBtnVisibility()
            dataBinding.btnAdd.visibility = View.GONE
            dataBinding.ivEdit.visibility = View.INVISIBLE
        } else {
            dataBinding.btnDelete.visibility = View.GONE
            dataBinding.btnAdd.visibility = View.VISIBLE
            dataBinding.ivEdit.visibility =
                if (alarmListAdapter.itemCount > 1) View.VISIBLE
                else View.INVISIBLE
        }
    }

    private fun setDeleteBtnVisibility() {
        alarmListAdapter.currentList
            .count { it.isChecked }
            .also {
                dataBinding.btnDelete.visibility =
                    if(it > 0) View.VISIBLE
                    else View.INVISIBLE
            }
    }

    private fun clearChecked() {
        alarmListAdapter.currentList
            .forEach {
                it.isChecked = false
            }
    }
}
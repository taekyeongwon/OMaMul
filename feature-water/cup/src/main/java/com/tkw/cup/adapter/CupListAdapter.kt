package com.tkw.cup.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tkw.base.C
import com.tkw.cup.databinding.ItemCupListBinding
import com.tkw.cup.databinding.ItemCupListEditBinding
import com.tkw.domain.model.Cup
import com.tkw.ui.ItemMoveListener
import com.tkw.ui.OnItemDrag

class CupListAdapter(
    private val editListener: (Int) -> Unit = {},
    private val deleteCheckListener: (Int, Boolean) -> Unit = {_, _ -> },
    private val longClickListener: (Int) -> Unit = {},
    private val dragListener: OnItemDrag<Cup>? = null,
    private val cupSelectListener: (Cup) -> Unit = {},
    private var currentSelectedCupId: String? = null
): ListAdapter<Cup, RecyclerView.ViewHolder>(CupDiffCallback()),
    ItemMoveListener {

    private var draggable: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(C.CupListViewType.values()[viewType]) {
            C.CupListViewType.NORMAL -> {
                val binding = ItemCupListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CupListViewHolder(binding, editListener, longClickListener, cupSelectListener)
            }
            C.CupListViewType.DRAG -> {
                val binding = ItemCupListEditBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CupEditViewHolder(binding, deleteCheckListener, dragListener)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is CupListViewHolder -> {
                val cup = getItem(position)
                val isSelected = cup.cupId == currentSelectedCupId
                holder.onBind(cup, isSelected)
            }
            is CupEditViewHolder -> holder.onBind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(draggable) C.CupListViewType.DRAG.viewType
        else C.CupListViewType.NORMAL.viewType
    }

    override fun onItemMove(from: Int, to: Int) {
        val current = currentList[from]
        submitList(currentList.toMutableList().apply {
            removeAt(from)
            add(to, current)
        })
    }

    override fun onStopDrag() {
        dragListener?.onStopDrag(currentList)
    }

    fun setDraggable(isDraggable: Boolean) {
        draggable = isDraggable
        notifyDataSetChanged()
    }

    // 현재 선택된 컵 ID 업데이트용 메서드 (프래그먼트에서 호출)
    fun updateSelectedCupId(selectedCupId: String?) {
        val oldSelectedCupId = currentSelectedCupId
        currentSelectedCupId = selectedCupId

        // 이전 선택된 아이템과 새로 선택된 아이템만 업데이트
        if (oldSelectedCupId != selectedCupId) {
            notifyDataSetChanged()
        }
    }

    class CupListViewHolder(
        private val binding: ItemCupListBinding,
        editListener: (Int) -> Unit,
        longClickListener: (Int) -> Unit,
        cupSelectListener: (Cup) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        private var currentCup: Cup? = null

        init {
            with(binding) {
                ibEdit.setOnClickListener { editListener(adapterPosition) }
                root.setOnClickListener {
                    currentCup?.let { cupSelectListener(it) }
                }
                root.setOnLongClickListener {
                    longClickListener(adapterPosition)
                    return@setOnLongClickListener true
                }
            }
        }

        fun onBind(data: Cup, isSelected: Boolean = false) {
            currentCup = data
            binding.cup = data
            binding.isSelected = isSelected
            binding.executePendingBindings()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    class CupEditViewHolder(
        private val binding: ItemCupListEditBinding,
        private val deleteCheckListener: (Int, Boolean) -> Unit,
        dragListener: OnItemDrag<Cup>?
    ): RecyclerView.ViewHolder(binding.root) {
        init {
            with(binding) {
                ibDrag.setOnTouchListener { v, event ->
                    if(event.action == MotionEvent.ACTION_DOWN) {
                        dragListener?.onStartDrag(this@CupEditViewHolder)
                    }
                    true
                }
                root.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        cbDelete.isChecked = !cbDelete.isChecked
                    }
                }
                // 체크박스 영역 클릭도 처리
                layoutCheckbox.setOnClickListener {
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        cbDelete.isChecked = !cbDelete.isChecked
                    }
                }
            }
        }

        fun onBind(data: Cup) {
            binding.cup = data
            // 리스너를 임시로 제거하고 상태 설정 후 다시 연결
            binding.cbDelete.setOnCheckedChangeListener(null)
            binding.cbDelete.isChecked = data.isChecked
            binding.cbDelete.setOnCheckedChangeListener { _, isChecked ->
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    deleteCheckListener(adapterPosition, isChecked)
                }
            }
            binding.executePendingBindings()
        }
    }
}
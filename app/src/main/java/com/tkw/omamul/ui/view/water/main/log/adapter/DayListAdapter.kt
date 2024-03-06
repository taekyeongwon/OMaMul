package com.tkw.omamul.ui.view.water.main.log.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tkw.omamul.util.DateTimeUtils
import com.tkw.omamul.data.model.WaterEntity
import com.tkw.omamul.databinding.ItemDayAmountBinding
import com.tkw.omamul.common.DiffCallback

class DayListAdapter: ListAdapter<WaterEntity, DayListAdapter.DayAmountViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayAmountViewHolder {
        val binding = ItemDayAmountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayAmountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayAmountViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class DayAmountViewHolder(private val binding: ItemDayAmountBinding): ViewHolder(binding.root) {
        fun onBind(item: WaterEntity) {
            binding.tvAmount.text = item.amount.toString()
            binding.tvDate.text = DateTimeUtils.getFormattedTime(item.date)
        }
    }
}
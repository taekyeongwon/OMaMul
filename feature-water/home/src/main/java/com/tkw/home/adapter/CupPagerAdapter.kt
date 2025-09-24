package com.tkw.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tkw.base.C
import com.tkw.domain.model.Cup
import com.tkw.home.databinding.ItemCupBinding

class CupPagerAdapter(
    private val onClick: (Int) -> Unit
)
    : ListAdapter<Cup, ViewHolder>(CupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CupViewHolder(binding, onClick)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder as CupViewHolder).onBind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return C.CupViewType.CUP.viewType
    }

    class CupViewHolder(private val binding: ItemCupBinding, listener: (Int) -> Unit): ViewHolder(binding.root) {
        init {
            binding.ivCup.setOnClickListener {
                listener(adapterPosition)
            }
        }
        fun onBind(item: Cup) {
            binding.tvName.text = item.cupName
        }
    }

}
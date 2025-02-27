package uz.sanjar.photoeditor.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.sanjar.photoeditor.databinding.ItemFilterBinding

/**   Created by Sanjar Karimov 11:22 AM 2/3/2025   */

class FilterAdapter : ListAdapter<FilterData, FilterAdapter.Holder>(TranslationDiffUtil) {

    var onFilterClick: ((FilterData) -> Unit)? = null

    inner class Holder(private val binding: ItemFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(item: FilterData) {
            binding.filterImage.apply {
                setOnClickListener{
                    onFilterClick?.invoke(item)
                }
                setImageResource(item.resId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(getItem(position))
    }


    object TranslationDiffUtil : DiffUtil.ItemCallback<FilterData>() {
        override fun areItemsTheSame(
            oldItem: FilterData,
            newItem: FilterData,
        ): Boolean = oldItem == newItem


        override fun areContentsTheSame(
            oldItem: FilterData,
            newItem: FilterData,
        ): Boolean = oldItem == newItem

    }
}

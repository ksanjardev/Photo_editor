package uz.sanjar.photoeditor.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.sanjar.photoeditor.R
import uz.sanjar.photoeditor.databinding.ItemColorBinding

/**   Created by Sanjar Karimov 11:22 AM 2/3/2025   */

class ColorAdapter : ListAdapter<TextFilterData, ColorAdapter.Holder>(TranslationDiffUtil) {

    var onColorClick: ((Int) -> Unit)? = null
    var onFontClick: ((Int) -> Unit)? = null

    private var selectedPosition = -1
    private var selectedPosition2 = -1

    inner class Holder(private val binding: ItemColorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(item: TextFilterData, position: Int) {
            val typeface = ResourcesCompat.getFont(binding.root.context, item.fontRes)
            binding.colorBg.setBackgroundColor(item.colorRes)
            binding.fontText.typeface = typeface


            if (position == selectedPosition) {
                binding.fontText.setBackgroundResource(R.drawable.selected_bg)
            } else {
                binding.fontText.background = null
            }

            binding.colorBg.setOnClickListener {
                onColorClick?.invoke(item.colorRes)
                setSelectedPosition2(position)
            }
            binding.fontText.setOnClickListener{
                onFontClick?.invoke(item.fontRes)
                setSelectedPosition(position)
            }
        }
    }

    fun setSelectedPosition(position: Int) {
        val previousSelected = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousSelected)
        notifyItemChanged(selectedPosition)
    }

    fun setSelectedPosition2(position: Int) {
        val previousSelected = selectedPosition2
        selectedPosition2 = position
        notifyItemChanged(previousSelected)
        notifyItemChanged(selectedPosition2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemColorBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(getItem(position), position)
    }


    object TranslationDiffUtil : DiffUtil.ItemCallback<TextFilterData>() {
        override fun areItemsTheSame(
            oldItem: TextFilterData,
            newItem: TextFilterData,
        ): Boolean = oldItem == newItem


        override fun areContentsTheSame(
            oldItem: TextFilterData,
            newItem: TextFilterData,
        ): Boolean = oldItem == newItem

    }
}

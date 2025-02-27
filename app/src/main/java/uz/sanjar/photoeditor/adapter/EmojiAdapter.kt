package uz.sanjar.photoeditor.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.sanjar.photoeditor.databinding.ItemEmojiBinding

/**   Created by Sanjar Karimov 11:22 AM 2/3/2025   */
class EmojiAdapter : ListAdapter<String, EmojiAdapter.Holder>(TranslationDiffUtil) {

    var onEmojiClick: ((String) -> Unit)? = null

    inner class Holder(private val binding: ItemEmojiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(item: String) {
            binding.apply {
                element.setOnClickListener {
                    onEmojiClick?.invoke(item)
                }
                element.text = item
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemEmojiBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(getItem(position))
    }


    object TranslationDiffUtil : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String,
        ): Boolean = oldItem == newItem


        override fun areContentsTheSame(
            oldItem: String,
            newItem: String,
        ): Boolean = oldItem == newItem

    }
}

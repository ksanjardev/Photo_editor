package uz.sanjar.photoeditor.utils

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import uz.sanjar.photoeditor.databinding.ControlPanelBinding

/**   Created by Sanjar Karimov 3:58 PM 2/1/2025   */

fun Int.dpToPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()


fun ViewGroup.hideControlPanel() {
    val controlPanelBinding = ControlPanelBinding.bind(this)
    controlPanelBinding.groupItem.isVisible = false
}

fun ViewGroup.showControlPanel(onRemoveClick: () -> Unit) {
    val controlPanelBinding = ControlPanelBinding.bind(this)
    controlPanelBinding.groupItem.isVisible = true
    controlPanelBinding.btnRemove.setOnClickListener { onRemoveClick.invoke() }
}
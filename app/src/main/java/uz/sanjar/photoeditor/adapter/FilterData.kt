package uz.sanjar.photoeditor.adapter

/**   Created by Sanjar Karimov 4:38 PM 2/4/2025   */

data class FilterData(
    val resId: Int,
    val filterEnum: FilterEnum
)
data class TextFilterData(
    val colorRes: Int,
    val fontRes: Int
)
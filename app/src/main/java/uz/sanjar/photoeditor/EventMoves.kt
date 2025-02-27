package uz.sanjar.photoeditor

import android.graphics.PointF
import android.view.ViewGroup

/**   Created by Sanjar Karimov 8:45 PM 2/3/2025   */
sealed interface EventMoves {
    data class Added(val view: ViewGroup) : EventMoves
    data class Position(
        val view: ViewGroup,
        val coordinates: PointF,
        val scale: Float,
        val rotation: Float,
    ) : EventMoves

    data class Removed(val view: ViewGroup) : EventMoves
}
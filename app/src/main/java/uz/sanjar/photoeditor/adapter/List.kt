package uz.sanjar.photoeditor.adapter

import android.graphics.Color
import uz.sanjar.photoeditor.R

/**   Created by Sanjar Karimov 4:25 PM 2/4/2025   */

val emojiList = listOf(
    "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃",
    "😉", "😊", "😇", "😍", "😘", "😜", "🤪", "😝", "🤑", "🤗",
    "🤩", "🥳", "🤔", "🤨", "😐", "😑", "😶", "🙄", "😏", "😣",
    "😥", "😮", "🤐", "😯", "😪", "😫", "🥱", "😴", "😷", "🤒",
    "🤕", "🤢", "🤮", "🥵", "🥶", "😵", "🤯", "🤠", "🥳", "😎",
    "👋", "🤚", "🖐️", "✋", "🖖", "👌", "🤌", "🤏", "✌️", "🤞",
    "🤟", "🤘", "🤙", "👈", "👉", "👆", "👇", "👍", "👎", "✊",
    "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝", "🙏", "💪",
    "❤", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
    "❤️‍🔥", "❤️‍🩹", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟",
    "🎉", "🎊", "🎁", "🎈", "🔥", "💥", "✨", "💫", "⭐", "🌟",
    "🌍", "🌎", "🌏", "🪐", "🌙", "☀️", "🌞", "⏳", "⌛", "⚡",
    "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯",
    "🦁", "🐮", "🐷", "🐸", "🐵", "🙈", "🙉", "🙊", "🐧", "🐦",
    "🚗", "🚕", "🚙", "🚌", "🚎", "🏎", "🚓", "🚑", "🚒", "🚚",
    "🚛", "🚜", "✈️", "🚀", "🛸", "🚁", "🛶", "🚢", "⛵", "⚓"
)

val filterList = listOf(
    FilterData(
        resId = R.drawable.original,
        filterEnum = FilterEnum.NONE
    ),
    FilterData(
        resId = R.drawable.auto_fix,
        filterEnum = FilterEnum.AUTO_FIX
    ),
    FilterData(
        resId = R.drawable.brightness,
        filterEnum = FilterEnum.BRIGHTNESS
    ),
    FilterData(
        resId = R.drawable.contrast,
        filterEnum = FilterEnum.CONTRAST
    ),
    FilterData(
        resId = R.drawable.documentary,
        filterEnum = FilterEnum.DOCUMENTARY
    ),
    FilterData(
        resId = R.drawable.dual_tone,
        filterEnum = FilterEnum.DUE_TONE
    ),
    FilterData(
        resId = R.drawable.fish_eye,
        filterEnum = FilterEnum.FISH_EYE
    ),
    FilterData(
        resId = R.drawable.fill_light,
        filterEnum = FilterEnum.FILL_LIGHT
    ),
    FilterData(
        resId = R.drawable.grain,
        filterEnum = FilterEnum.GRAIN
    ),
    FilterData(
        resId = R.drawable.gray_scale,
        filterEnum = FilterEnum.GRAY_SCALE
    ),
    FilterData(
        resId = R.drawable.lomish,
        filterEnum = FilterEnum.LOMISH
    ),
    FilterData(
        resId = R.drawable.negative,
        filterEnum = FilterEnum.NEGATIVE
    ),
    FilterData(
        resId = R.drawable.posterize,
        filterEnum = FilterEnum.POSTERIZE
    ),
    FilterData(
        resId = R.drawable.saturate,
        filterEnum = FilterEnum.SATURATE
    ),
    FilterData(
        resId = R.drawable.sepia,
        filterEnum = FilterEnum.SEPIA
    ),
    FilterData(
        resId = R.drawable.sharpen,
        filterEnum = FilterEnum.SHARPEN
    ),
    FilterData(
        resId = R.drawable.temprature,
        filterEnum = FilterEnum.TEMPERATURE
    ),
    FilterData(
        resId = R.drawable.tint,
        filterEnum = FilterEnum.TINT
    ),
    FilterData(
        resId = R.drawable.vignette,
        filterEnum = FilterEnum.VIGNETTE
    ),
    FilterData(
        resId = R.drawable.cross_process,
        filterEnum = FilterEnum.CROSS_PROCESS
    ),
    FilterData(
        resId = R.drawable.b_n_w,
        filterEnum = FilterEnum.BLACK_WHITE
    ),
    FilterData(
        resId = R.drawable.flip_horizental,
        filterEnum = FilterEnum.FLIP_HORIZONTAL
    ),
    FilterData(
        resId = R.drawable.flip_vertical,
        filterEnum = FilterEnum.FLIP_VERTICAL
    ),
    FilterData(
        resId = R.drawable.rotate,
        filterEnum = FilterEnum.ROTATE
    )
)

val colorList = listOf(
    TextFilterData(
        colorRes = Color.parseColor("#FF0000"), // Red
        fontRes = R.font.bebas // Replace with your font in res/font
    ),
    TextFilterData(
        colorRes = Color.parseColor("#00FF00"), // Green
        fontRes = R.font.rubik // Replace with your font in res/font
    ),
    TextFilterData(
        colorRes = Color.parseColor("#0000FF"), // Blue
        fontRes = R.font.fredoka
    ),
    TextFilterData(
        colorRes = Color.parseColor("#FFFF00"), // Yellow
        fontRes = R.font.recommended_text_font
    ),
    TextFilterData(
        colorRes = Color.parseColor("#FF00FF"), // Magenta
        fontRes = R.font.comforta
    ),
    TextFilterData(
        colorRes = Color.parseColor("#00FFFF"), // Cyan
        fontRes = R.font.intro_black
    ),
    TextFilterData(
        colorRes = Color.parseColor("#FFA500"), // Orange
        fontRes = R.font.rubik_regular
    )
)

package uz.sanjar.photoeditor

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import dev.androidbroadcast.vbpd.viewBinding
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBulgeDistortionFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorInvertFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorMatrixFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageExposureFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImagePosterizeFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter
import uz.sanjar.photoeditor.adapter.ColorAdapter
import uz.sanjar.photoeditor.adapter.CustomAutoEnhanceFilter
import uz.sanjar.photoeditor.adapter.CustomSepiaFilter
import uz.sanjar.photoeditor.adapter.EmojiAdapter
import uz.sanjar.photoeditor.adapter.FilterAdapter
import uz.sanjar.photoeditor.adapter.FilterData
import uz.sanjar.photoeditor.adapter.FilterEnum
import uz.sanjar.photoeditor.adapter.GPUImageFlipHorizontalFilter
import uz.sanjar.photoeditor.adapter.GPUImageFlipVerticalFilter
import uz.sanjar.photoeditor.adapter.GPUImageGrainFilter
import uz.sanjar.photoeditor.adapter.GPUImageRotateFilter
import uz.sanjar.photoeditor.adapter.colorList
import uz.sanjar.photoeditor.adapter.emojiList
import uz.sanjar.photoeditor.adapter.filterList
import uz.sanjar.photoeditor.databinding.DialogEnterTextBinding
import uz.sanjar.photoeditor.databinding.EditorScreenBinding
import uz.sanjar.photoeditor.databinding.ItemEmojiBinding
import uz.sanjar.photoeditor.databinding.ItemTextBinding
import uz.sanjar.photoeditor.utils.hideControlPanel
import uz.sanjar.photoeditor.utils.showControlPanel
import java.io.InputStream
import java.util.Stack
import kotlin.math.atan2

/**   Created by Sanjar Karimov 3:33 PM 2/1/2025   */


class EditorScreen : Fragment(R.layout.editor_screen) {
    private val binding: EditorScreenBinding by viewBinding(EditorScreenBinding::bind)
    private val emojiAdapter by lazy { EmojiAdapter() }
    private val filterAdapter by lazy { FilterAdapter() }
    private val textColorAdapter by lazy { ColorAdapter() }
    private val redoStack = Stack<EventMoves>()
    private val undoStack = Stack<EventMoves>()
    private var selectedView: ViewGroup? = null
    private var isMoved: Boolean = false
    private var originalBitmap: Bitmap? = null
    private var currentBitmap: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (originalBitmap == null) {
            originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.paris_tower)
            currentBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
            binding.mainImage.setImageBitmap(currentBitmap)
        }
        handleOnClicks()
    }

    private fun handleOnClicks() {
        binding.apply {
            selectFromGallery.setOnClickListener { pickImageLauncher.launch("image/*") }
            enterText.setOnClickListener { showInputDialog() }
            selectEmoji.setOnClickListener { showEmojis() }
            redo.setOnClickListener { redo() }
            undo.setOnClickListener { undo() }
            cancelButton.setOnClickListener { binding.emojiContainer.visibility = View.GONE }
            buttonSave.setOnClickListener { saveImage(imageContainer) }
            filter.setOnClickListener { showFilters() }
        }
    }

    @SuppressLint("NewApi")
    private fun saveImage(frameLayout: FrameLayout) {
        val bitmap = getBitmapFromView(frameLayout)
        val contentResolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "PaintDrawing_${System.currentTimeMillis()}.png"
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES
            )
        }

        val imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let { uri ->
            contentResolver.openOutputStream(uri).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream!!)
                Toast.makeText(requireContext(), "Image saved to gallery", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun showEmojis() {
        binding.emojiContainer.visibility = View.VISIBLE
        binding.rv.adapter = emojiAdapter
        emojiAdapter.submitList(emojiList)
        emojiAdapter.onEmojiClick = { emoji ->
            addEmoji(emoji)
        }
    }

    private fun showFilters() {
        binding.emojiContainer.visibility = View.VISIBLE
        binding.rv.adapter = filterAdapter
        filterAdapter.submitList(filterList)
        filterAdapter.onFilterClick = { filter ->
            addFilter(filter)
        }
    }

    private fun addEmoji(emoji: String) {
        val context = requireContext()
        val emojiBinding =
            ItemEmojiBinding.inflate(LayoutInflater.from(context), binding.emojiContainer, false)
        emojiBinding.element.text = emoji

        selectedView?.hideControlPanel()
        selectedView = emojiBinding.root

        binding.imageContainer.addView(emojiBinding.root)

        emojiBinding.root.setOnClickListener {
            selectedView?.hideControlPanel()
            selectedView = emojiBinding.root
            selectedView?.showControlPanel {
                undoStack.push(EventMoves.Removed(emojiBinding.root))
                binding.imageContainer.removeView(emojiBinding.root)
            }
            selectedView?.bringToFront()
            emojiBinding.root.elevation = 20f
        }
        binding.imageContainer.setOnClickListener {
            selectedView?.hideControlPanel()
            selectedView = null
        }

        setupTextViewTouchListener(emojiBinding.root)
        undoStack.push(EventMoves.Added(emojiBinding.root))
        redoStack.clear()
        updateUndoRedoButtons()
    }

    private fun addFilter(filter: FilterData) {
        originalBitmap?.let { bitmap ->
            val gpuImage = GPUImage(requireContext()).apply {
                setImage(bitmap)  // Always start from the original image
                setFilter(getGpuImageFilter(filter.filterEnum))
            }
            currentBitmap = gpuImage.bitmapWithFilterApplied
            binding.mainImage.setImageBitmap(currentBitmap)
        } ?: Toast.makeText(requireContext(), "No image loaded", Toast.LENGTH_SHORT).show()
    }

    private fun getGpuImageFilter(filterEnum: FilterEnum): GPUImageFilter {
        return when (filterEnum) {
            FilterEnum.NONE -> GPUImageFilter()
            FilterEnum.AUTO_FIX -> CustomAutoEnhanceFilter()
            FilterEnum.BRIGHTNESS -> GPUImageBrightnessFilter(0.2f)
            FilterEnum.CONTRAST -> GPUImageContrastFilter(1.5f)
            FilterEnum.DOCUMENTARY -> CustomSepiaFilter()
            FilterEnum.DUE_TONE -> GPUImageColorInvertFilter()
            FilterEnum.FISH_EYE -> GPUImageBulgeDistortionFilter().apply {
                setScale(0.7f)
                setRadius(0.25f)
            }

            FilterEnum.TEMPERATURE -> GPUImageWhiteBalanceFilter(4000f, 0.1f)
            FilterEnum.FILL_LIGHT -> GPUImageExposureFilter(0.5f)
            FilterEnum.GRAIN -> GPUImageGrainFilter(0.2f)
            FilterEnum.GRAY_SCALE -> GPUImageGrayscaleFilter()
            FilterEnum.LOMISH -> GPUImageVignetteFilter()
            FilterEnum.NEGATIVE -> GPUImageColorInvertFilter()
            FilterEnum.POSTERIZE -> GPUImagePosterizeFilter(10)
            FilterEnum.SATURATE -> GPUImageSaturationFilter(1.5f)
            FilterEnum.SEPIA -> CustomSepiaFilter()
            FilterEnum.SHARPEN -> GPUImageSharpenFilter(2.0f)
//            FilterEnum.TEMPERATURE -> GPUImageWhiteBalanceFilter(6500f, 0.0f)
            FilterEnum.TINT -> GPUImageHueFilter(45.0f)
            FilterEnum.VIGNETTE -> GPUImageVignetteFilter()
            FilterEnum.CROSS_PROCESS -> GPUImageColorMatrixFilter(
                /*floatArrayOf(
                    1.5f, -0.5f, 0f, 0f,
                    -0.5f, 1.5f, 0f, 0f,
                    0f, 0f, 1.5f, 0f,
                    0f, 0f, 0f, 1f
                )*/
            )

            FilterEnum.BLACK_WHITE -> GPUImageGrayscaleFilter()
            FilterEnum.FLIP_HORIZONTAL -> GPUImageFlipHorizontalFilter()
            FilterEnum.FLIP_VERTICAL -> GPUImageFlipVerticalFilter()
            FilterEnum.ROTATE -> GPUImageRotateFilter(Math.PI.toFloat() / 2)
        }
    }

    private fun showInputDialog() {
        val dialogBinding = DialogEnterTextBinding.inflate(LayoutInflater.from(requireContext()))

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        dialogBinding.inputText.requestFocus()
        var textColor: Int? = null
        var textFont: Int? = null

        dialogBinding.rvColor.adapter = textColorAdapter
        textColorAdapter.submitList(colorList)
        textColorAdapter.onColorClick = {
            textColor = it
            dialogBinding.inputText.setTextColor(it)
        }
        textColorAdapter.onFontClick = {
            textFont = it
            dialogBinding.inputText.typeface = ResourcesCompat.getFont(requireContext(), it)
        }

        dialogBinding.inputText.postDelayed({
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.showSoftInput(
                dialogBinding.inputText,
                InputMethodManager.SHOW_IMPLICIT
            )
        }, 100)

        dialogBinding.okButton.setOnClickListener {
            val userInput = dialogBinding.inputText.text.toString().trim()

            if (userInput.isNotEmpty()) {
                addText(userInput, textColor, textFont)
                alertDialog.dismiss()
            } else {
                dialogBinding.inputText.error = "Input required"
            }
        }

        dialogBinding.cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addText(text: String, textColor: Int?, textFont: Int?) {
        val itemTextBinding = ItemTextBinding.inflate(
            LayoutInflater.from(requireContext()),
            binding.imageContainer,
            false
        )
        itemTextBinding.element.apply {
            this.text = text
            textFont?.let {
                typeface = ResourcesCompat.getFont(requireContext(), it)
            }
            textColor?.let { setTextColor(it) }
        }
        selectedView?.hideControlPanel()
        selectedView = itemTextBinding.root

        binding.imageContainer.addView(itemTextBinding.root)
        itemTextBinding.root.setOnClickListener {
            selectedView?.hideControlPanel()
            selectedView = itemTextBinding.root
            selectedView?.showControlPanel {
                undoStack.push(EventMoves.Removed(itemTextBinding.root))
                binding.imageContainer.removeView(itemTextBinding.root)
            }
            selectedView?.bringToFront()
            itemTextBinding.root.elevation = 20f
        }

        binding.imageContainer.setOnClickListener {
            selectedView?.hideControlPanel()
            selectedView = null
        }

        setupTextViewTouchListener(itemTextBinding.root)
        undoStack.push(EventMoves.Added(itemTextBinding.root))
        redoStack.clear()
        updateUndoRedoButtons()
    }

    private fun undo() {
        if (undoStack.isNotEmpty()) {
            when (val lastView = undoStack.pop()) {
                is EventMoves.Added -> {
                    redoStack.push(EventMoves.Added(lastView.view))
                    binding.imageContainer.removeView(lastView.view)
                }

                is EventMoves.Position -> {

                    val currentX = lastView.view.x
                    val currentY = lastView.view.y
                    val currentScale = lastView.view.scaleX
                    val currentRotation = lastView.view.rotation

                    lastView.view.x = lastView.coordinates.x
                    lastView.view.y = lastView.coordinates.y
                    lastView.view.rotation = lastView.rotation
                    lastView.view.scaleX = lastView.scale
                    lastView.view.scaleY = lastView.scale

                    redoStack.push(
                        EventMoves.Position(
                            view = lastView.view,
                            coordinates = PointF(currentX, currentY),
                            scale = currentScale,
                            rotation = currentRotation
                        )
                    )
                }

                is EventMoves.Removed -> {
                    redoStack.push(EventMoves.Removed(lastView.view))
                    binding.imageContainer.addView(lastView.view)
                }
            }
        }
    }

    private fun redo() {
        if (redoStack.isNotEmpty()) {
            when (val lastView = redoStack.pop()) {
                is EventMoves.Added -> {
                    undoStack.push(EventMoves.Added(lastView.view))
                    binding.imageContainer.addView(lastView.view)
                }

                is EventMoves.Position -> {

                    val currentX = lastView.view.x
                    val currentY = lastView.view.y
                    val currentScale = lastView.view.scaleX
                    val currentRotation = lastView.view.rotation

                    lastView.view.x = lastView.coordinates.x
                    lastView.view.y = lastView.coordinates.y
                    lastView.view.rotation = lastView.rotation
                    lastView.view.scaleX = lastView.scale
                    lastView.view.scaleY = lastView.scale

                    undoStack.push(
                        EventMoves.Position(
                            view = lastView.view,
                            coordinates = PointF(currentX, currentY),
                            scale = currentScale,
                            rotation = currentRotation
                        )
                    )
                }

                is EventMoves.Removed -> {
                    undoStack.push(EventMoves.Added(lastView.view))
                    binding.imageContainer.removeView(lastView.view)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTextViewTouchListener(viewGroup: ViewGroup) {
        var scaleFactor = 1.0f
        var rotationAngle = 0f
        var lastRotation = 0f
        var dX = 0f
        var dY = 0f

        val scaleGestureDetector = ScaleGestureDetector(
            requireContext(),
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    if (selectedView != viewGroup) return true

                    scaleFactor *= detector.scaleFactor
                    scaleFactor = scaleFactor.coerceIn(0.2f, 20.0f)
                    viewGroup.scaleX = scaleFactor
                    viewGroup.scaleY = scaleFactor
                    return false
                }
            })

        viewGroup.setOnTouchListener { view, event ->
            if (selectedView != viewGroup) return@setOnTouchListener false

            scaleGestureDetector.onTouchEvent(event)
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    when (event.pointerCount) {
                        1 -> if (isMoved) {
                            view.x = event.rawX + dX
                            view.y = event.rawY + dY

                        }

                        2 -> {
                            isMoved = false
                            val deltaX = event.getX(1) - event.getX(0)
                            val deltaY = event.getY(1) - event.getY(0)
                            val angle =
                                Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble()))
                                    .toFloat()

                            if (lastRotation == 0f) {
                                lastRotation = angle
                            }
                            rotationAngle += angle - lastRotation
                            lastRotation = angle

                            view.rotation = rotationAngle
                            Log.d(
                                "isMoved",
                                "setupTextViewTouchListener: worked $isMoved rotation = "
                            )
                        }
                    }
                }

                MotionEvent.ACTION_UP -> {
                    isMoved = true
                    lastRotation = 0f
                    undoStack.push(
                        EventMoves.Position(
                            view = viewGroup,
                            coordinates = PointF(viewGroup.x, viewGroup.y),
                            scale = viewGroup.scaleX,
                            rotation = viewGroup.rotation
                        )
                    )
                }
            }
            false
        }
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            try {
                val inputStream = uri?.let { requireContext().contentResolver.openInputStream(it) }
                    ?: R.drawable.paris_tower
                originalBitmap = BitmapFactory.decodeStream(inputStream as InputStream?)
                currentBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
                binding.mainImage.setImageBitmap(currentBitmap)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading image", Toast.LENGTH_SHORT).show()
            }
        }

    private fun updateUndoRedoButtons() {
        binding.redo.setImageResource(
            if (redoStack.isEmpty()) R.drawable.ic_redo_disabled else R.drawable.ic_redo
        )
        binding.undo.setImageResource(
            if (undoStack.isEmpty()) R.drawable.ic_undo_disabled else R.drawable.ic_undo
        )
    }
}


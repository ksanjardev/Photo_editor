package uz.sanjar.photoeditor

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.permissionx.guolindev.PermissionX

class MainActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        requestGalleryPermissions()

    }

    private fun requestGalleryPermissions() {
        val permissions = mutableListOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        PermissionX.init(this)
            .permissions(permissions)
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Denied: $deniedList", Toast.LENGTH_SHORT).show()
                }
            }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, EditorScreen())
            .commit()
    }

    private fun makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    /*val imageView = findViewById<ImageView>(R.id.img)


       scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
           override fun onScale(detector: ScaleGestureDetector): Boolean {
               scaleFactor *= detector.scaleFactor
               scaleFactor = scaleFactor.coerceIn(0.5f, 5.0f)
               imageView.scaleX = scaleFactor
               imageView.scaleY = scaleFactor
               return true
           }
       })

       imageView.setOnTouchListener { view, event ->
           scaleGestureDetector.onTouchEvent(event)

           when (event.actionMasked) {
               MotionEvent.ACTION_DOWN -> {
                   dX = view.x - event.rawX
                   dY = view.y - event.rawY
                   lastRotation = Float.NaN
               }

               MotionEvent.ACTION_POINTER_DOWN -> {
                   if (event.pointerCount == 2) {
                       lastRotation = calculateAngle(event)
                   }
               }

               MotionEvent.ACTION_MOVE -> {
                   when (event.pointerCount) {
                       1 -> {
                           view.x = event.rawX + dX
                           view.y = event.rawY + dY
                       }
                       2 -> {
                           val angle = calculateAngle(event)
                           if (!lastRotation.isNaN()) {
                               val deltaRotation = angle - lastRotation
                               rotationAngle += deltaRotation
                               view.rotation = rotationAngle
                           }
                           lastRotation = angle
                       }
                   }
               }

               MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                   lastRotation = Float.NaN
               }
           }
           true
       }
*/

    /*    private fun calculateAngle(event: MotionEvent): Float {
            val deltaX = event.getX(1) - event.getX(0)
            val deltaY = event.getY(1) - event.getY(0)
            return Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
        }*/

    /*
        private var scaleFactor = 1.0f
        private var rotationAngle = 0f
        private var lastRotation = Float.NaN
        private lateinit var scaleGestureDetector: ScaleGestureDetector
        private var dX = 0f
        private var dY = 0f
    */
}

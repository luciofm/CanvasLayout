package mobi.largemind.canvaslayout

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.*
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    val images = arrayOf(
        R.drawable.image_01,
        R.drawable.image_02,
        R.drawable.image_03,
        R.drawable.image_04,
        R.drawable.image_06,
        R.drawable.image_07,
        R.drawable.image_08,
        R.drawable.image_09,
        R.drawable.image_11,
        R.drawable.image_12,
        R.drawable.image_13,
        R.drawable.image_14,
        R.drawable.image_15,
        R.drawable.image_16,
        R.drawable.image_17,
        R.drawable.image_18,
        R.drawable.image_19,
        R.drawable.image_20
    )

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var canvasGrid: CanvasGridLayout
    private var currentIndex: Int = 0
    private val transitionSet = GridTransition()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        canvasGrid = findViewById(R.id.canvas)

        images.take(5).forEachIndexed { index, image ->
            val imageView = ImageView(this)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setImageResource(image);
            canvasGrid.addView(imageView)
            Log.d("MainActivity", "Index: $index, image: $image")
        }
        currentIndex = 6
    }

    override fun onStart() {
        handler.postDelayed(updateImagesRunnable, 5000);
        super.onStart()
    }

    override fun onStop() {
        handler.removeCallbacks(updateImagesRunnable)
        super.onStop()
    }

    private val updateImagesRunnable = Runnable {
        updateImages()
    }

    private fun updateImages() {
        TransitionManager.beginDelayedTransition(canvasGrid, transitionSet);

        if (currentIndex >= images.size) currentIndex = 0
        val v = canvasGrid.getChildAt(0) as ImageView
        canvasGrid.removeView(v)

        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setImageResource(images[currentIndex++]);
        canvasGrid.addView(imageView)

        handler.postDelayed(updateImagesRunnable, 5000)
    }

    class GridTransition : TransitionSet() {
        init {
            val bounds = TransitionSet()
            bounds.addTransition(ChangeBounds())
                .addTransition(ChangeImageTransform())
            ordering = ORDERING_SEQUENTIAL
            addTransition(Fade(Fade.OUT)).addTransition(bounds).addTransition(Fade(Fade.IN))
        }
    }
}
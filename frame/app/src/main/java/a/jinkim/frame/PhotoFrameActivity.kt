package a.jinkim.frame

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.timer

class PhotoFrameActivity : AppCompatActivity() {

    private val photoList = mutableListOf<Uri>()

    private val imageviewPhoto : ImageView by lazy {
        findViewById<ImageView>(R.id.imageviewPhoto)
    }
    private val imageviewBackgroundPhoto : ImageView by lazy {
        findViewById<ImageView>(R.id.imageviewBackgroundPhoto)
    }

    private var timer : Timer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoframe)
        getPhotoUriIntent()
    }

    private fun getPhotoUriIntent()
    {
        val size = intent.getIntExtra("photoListSize", 0)
        for (i in 0..size) {
            intent.getStringExtra("photo$i")?.let {
                photoList.add(Uri.parse(it))
            }
        }
    }


    private var currentPosition : Int = 0
    private fun startTimer()
    {
        timer = timer(period = 5 * 1000){
            runOnUiThread{
                val current = currentPosition
                val next = if (photoList.size <= currentPosition + 1) 0 else currentPosition + 1

                imageviewBackgroundPhoto.setImageURI(photoList[current])
                imageviewPhoto.alpha = 0f
                imageviewPhoto.setImageURI(photoList[next])
                imageviewPhoto.animate()
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()

                currentPosition = next


            }
        }
    }

    override fun onStop() {
        super.onStop()
        timer?.cancel()
    }

    override fun onStart() {
        super.onStart()
        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
package a.jinkim.timer

import android.annotation.SuppressLint
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val textViewRemainMinutes: TextView by lazy {
        findViewById(R.id.textviewReaminMinutes)
    }

    private val textViewRemainSeconds: TextView by lazy {
        findViewById(R.id.textviewReaminSecends)
    }

    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekbar)
    }

    private var currentCountDownTimer: CountDownTimer? = null

    private val soundPool = SoundPool.Builder().build()
    private var tickingSoundID: Int? = null
    private var bellSoundID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        bindViews()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()

    }


    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun initSounds() {
        tickingSoundID = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundID = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun bindViews() {
        //각각의 뷰에 있는 리스너를 연결.
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2) {
                        updateRemainTims(p1 * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    stopCountDownTimer()
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    p0 ?: return
                    if (p0.progress == 0) {
                        stopCountDownTimer()
                    } else {
                        startCountDown(p0)
                    }


                }
            }
        )
    }

    private fun stopCountDownTimer() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }

    private fun createCountDownTimer(initailMills: Long) =

        object : CountDownTimer(initailMills, 1000L) {
            override fun onTick(p0: Long) {
                updateRemainTims(p0)
                updateSeekbar(p0)

            }

            override fun onFinish() {
                completeCountDown()

            }
        }

    private fun startCountDown(p0: SeekBar) {
        currentCountDownTimer = createCountDownTimer(p0.progress * 60 * 1000L)
        currentCountDownTimer?.start()

        tickingSoundID?.let { soundID ->
            soundPool.play(soundID, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun completeCountDown() {
        updateRemainTims(0)
        updateSeekbar(0)
        soundPool.autoPause()
        bellSoundID?.let { soundID ->
            soundPool.play(soundID, 1F, 1F, 0, 0, 1F)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateRemainTims(remainMillis: Long) {
        val remainSeconds = remainMillis / 1000

        textViewRemainMinutes.text = "%02d'".format(remainSeconds / 60)
        textViewRemainSeconds.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekbar(remainMillis: Long) {
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }

}
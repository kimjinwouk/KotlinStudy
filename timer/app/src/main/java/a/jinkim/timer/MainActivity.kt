package a.jinkim.timer

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        bindViews()
    }

    private fun bindViews() {
        //각각의 뷰에 있는 리스너를 연결.
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    textViewRemainMinutes.text = "%02d".format(p1)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    TODO("Not yet implemented")
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    TODO("Not yet implemented")
                }
            }
        )
    }
}
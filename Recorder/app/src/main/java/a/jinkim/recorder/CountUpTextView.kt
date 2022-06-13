package a.jinkim.recorder

import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CountUpTextView(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatTextView (context,attributeSet){

    private var startTimeStemp : Long = 0L

    private val countUpAction : Runnable = object : Runnable{
        override fun run() {
            val currentTimeStamp = SystemClock.elapsedRealtime()
            val countTimeSeconds = ((currentTimeStamp - startTimeStemp) / 1000L).toInt()
            updateCountTime(countTimeSeconds)
            handler.postDelayed(this,1000L)
        }
    }


    fun startCountUp(){
        startTimeStemp = SystemClock.elapsedRealtime()
        handler?.post(countUpAction)
    }


    fun stopCountUp()
    {
        handler?.removeCallbacks(countUpAction)
    }

    fun clearCountTime()
    {
        updateCountTime(0)
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountTime(countTimeSecond : Int)
    {
        val minutes = countTimeSecond / 60
        val seconds = countTimeSecond % 60

        text = "%02d:%02d".format(minutes,seconds)
    }

}
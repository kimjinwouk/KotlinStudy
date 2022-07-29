package com.kimjinwouk.petwalk.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.kimjinwouk.petwalk.R
import com.kimjinwouk.petwalk.ui.fragment.WalkingListFragment
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import java.util.concurrent.TimeUnit

class PetWalkUtil {
    companion object {
        // 권한 확인
        fun hasLocationPermissions(context: Context): Boolean {
            // API 23 미만 버전에서는 ACCESS_BACKGROUND_LOCATION X
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                (ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            }
            else {
                (ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
            }
        }

        // 타이머 표시 형식
        fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String {
            var milliseconds = ms
            val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
            milliseconds -= TimeUnit.HOURS.toMillis(hours)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
            milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
            if (!includeMillis) {
                return "${if (hours < 10) "0" else ""}$hours:" +
                        "${if (minutes < 10) "0" else ""}$minutes:" +
                        "${if (seconds < 10) "0" else ""}$seconds"
            }
            milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
            milliseconds /= 10
            return "${if (hours < 10) "0" else ""}$hours:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds:" +
                    "${if (milliseconds < 10) "0" else ""}$milliseconds"
        }

        // 거리 표시 형식
        fun getFormattedDistance(dis: Float): String {
            val df = DecimalFormat("###0.000")
            return df.format((dis / 1000f))
        }



        fun daysOfWeekFromLocale(): Array<DayOfWeek> {
            val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
            var daysOfWeek = DayOfWeek.values()
            // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
            // Only necessary if firstDayOfWeek != DayOfWeek.MONDAY which has ordinal 0.
            if (firstDayOfWeek != DayOfWeek.MONDAY) {
                val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
                val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
                daysOfWeek = rhs + lhs
            }
            return daysOfWeek
        }

        fun GradientDrawable.setCornerRadius(
            topLeft: Float = 0F,
            topRight: Float = 0F,
            bottomRight: Float = 0F,
            bottomLeft: Float = 0F
        ) {
            cornerRadii = arrayOf(
                topLeft, topLeft,
                topRight, topRight,
                bottomRight, bottomRight,
                bottomLeft, bottomLeft
            ).toFloatArray()
        }

        internal val Context.layoutInflater: LayoutInflater
            get() = LayoutInflater.from(this)

        internal val Context.inputMethodManager
            get() = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        internal inline fun Boolean?.orFalse(): Boolean = this ?: false

        internal fun Context.getDrawableCompat(@DrawableRes drawable: Int) = ContextCompat.getDrawable(this, drawable)

        internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

        internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))


    }


}

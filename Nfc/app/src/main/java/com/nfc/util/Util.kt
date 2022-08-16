package com.nfc.util

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*

class Util {
    // My Phne Number
    companion object {
        var dateFormat = "M.dd"

        internal fun formatTelNumber(TelNum: String): String? {

            try {
                TelNum.toInt()
            } catch (ex: NumberFormatException) {
                return TelNum
            }

            val nLen = TelNum.length
            if (nLen == 10) {
                val Tel1 = TelNum.substring(0, 3)
                val Tel2 = TelNum.substring(3, 6)
                val Tel3 = TelNum.substring(6, 10)
                return String.format("%s-%s-%s", Tel1, Tel2, Tel3)
            }
            if (nLen >= 11) {
                val str = TelNum.substring(0, 3)
                val bType = str == "050"
                val Tel1 = if (bType) TelNum.substring(0, 4) else TelNum.substring(0, 3)
                val Tel2 = if (bType) TelNum.substring(4, 7) else TelNum.substring(3, 7)
                val Tel3 = if (bType) TelNum.substring(7, nLen) else TelNum.substring(7, nLen)
                return String.format("%s-%s-%s", Tel1, Tel2, Tel3)
            }
            return TelNum
        }

        internal fun byteArrayToHexString(bytes: ByteArray): String? {
            var data = ""
            for (b in bytes) {
                val st = String.format("%02X", b)
                data += st
            }
            return data
        }

        internal fun riderSituation(Type: String): String = when (Type) {
            "1" -> "( 근무 )"
            "3" -> "( 업무휴직 )"
            else -> ""
        }

        internal fun riderMisu(RiderId: String, Misu: String): String {
            var result = ""
            if (RiderId.toInt() > 0) {
                result = "잔액 : " + Misu
            }

            return result
        }

        internal fun statisticsXvalueFormat(value: Float): String {
            /*
            오늘이 8월 12일 일 경우
            * 0이 들어왔을경우 8.12(금)
            * 1이 들어왔을경우 8.13(토)
            * */

            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, value.toInt() - 7)
            }
            val df = SimpleDateFormat(dateFormat)

            val date = df.format(cal.time)
            val dow = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.KOREAN)
            var result = date + "(" + dow.replace("요일", "") + ")"
            return result
        }

        internal fun daysOfWeekFromLocale(): Array<DayOfWeek> {
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

        internal val Context.layoutInflater: LayoutInflater
            get() = LayoutInflater.from(this)

        internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))
        internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)


        internal fun moneyFormatToWon(inputMoney : String) : String{
            val decimalFormat = DecimalFormat("#,##0")
            return decimalFormat.format(inputMoney.toInt())+"원"
        }

        /*
        7 기사 수익금
        13 가상계좌수수료
        1 기사 충전금
        */
        internal fun calendarUnderListType(type : String) : String{
            return when(type)
            {
                "1" -> "기사 충전금"
                "13" -> "가상 계좌 수수료"
                "7" -> "기사 수익금"
                else -> "기타"
            }
        }
    }
}
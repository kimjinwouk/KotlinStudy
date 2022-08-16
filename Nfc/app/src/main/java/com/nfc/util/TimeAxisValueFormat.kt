package com.nfc.util

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.util.concurrent.TimeUnit

class TimeAxisValueFormat : IndexAxisValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return Util.statisticsXvalueFormat(value)
    }
}
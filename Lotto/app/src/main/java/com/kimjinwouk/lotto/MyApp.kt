package com.kimjinwouk.lotto

import android.app.Application
import com.kimjinwouk.lotto.Util.PreferenceUtil


class MyApp : Application() {
    companion object {
        lateinit var prefs: PreferenceUtil
    }



    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
    }


}
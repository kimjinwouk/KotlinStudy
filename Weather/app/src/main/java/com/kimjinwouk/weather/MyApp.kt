package com.kimjinwouk.weather

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.kimjinwouk.weather.Util.PreferenceUtil


class MyApp : Application() {
    companion object {
        lateinit var prefs: PreferenceUtil
    }



    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)

        super.onCreate()
    }


}
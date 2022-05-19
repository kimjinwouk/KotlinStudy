package com.kimjinwouk.lotto

import android.app.Application
import com.kimjinwouk.lotto.Util.PreferenceUtil


class MyApp : Application() {
    //싱글톤객체로 만들어주는 것.
    companion object {
        lateinit var prefs: PreferenceUtil
    }



    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
    }


}
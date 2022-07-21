package com.kimjinwouk.petwalk

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp



/*
* Hilt를 사용하기위해 적용
* */
@HiltAndroidApp
class RunApplication : Application() {
    companion object {
        lateinit var instance: RunApplication
            private set

        fun context() : Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}
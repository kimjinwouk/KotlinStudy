package com.nfc

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class nfcApplication : Application() {
    companion object {
        lateinit var instance: nfcApplication
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
package com.kimjinwouk.petwalk

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {



    public fun log(context: Context, msg: String) {
        android.util.Log.d("${context::class.java.simpleName}", msg)
    }
    public fun toast(context: Context, msg: String) {
        android.widget.Toast.makeText(context,msg, android.widget.Toast.LENGTH_SHORT).show()
    }
}
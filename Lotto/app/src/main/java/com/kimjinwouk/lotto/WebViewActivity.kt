package com.kimjinwouk.lotto

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class WebViewActivity : AppCompatActivity() {


    private val wv: WebView by lazy {
        findViewById(R.id.wv)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        wv.settings.javaScriptEnabled = true
        wv.webViewClient = WebViewClient()

        //웹뷰를 띄운다.
        wv.loadUrl(intent.putExtra("url","").toString())
    }
}
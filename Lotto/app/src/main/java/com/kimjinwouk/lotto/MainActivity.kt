package com.kimjinwouk.lotto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.zxing.integration.android.IntentIntegrator
import com.kimjinwouk.lotto.DTO.LottoNumber
import com.kimjinwouk.lotto.Retrofit.Interface.RetrofitService
import com.kimjinwouk.lotto.Retrofit.Interface.RetroifitManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : BaseActivity() {

    private val resetButton: Button by lazy {
        findViewById(R.id.btn_Reset)
    }

    private val addButton: Button by lazy {
        findViewById(R.id.btn_Add)
    }

    private val runButton: Button by lazy {
        findViewById(R.id.btn_Run)
    }
    private val runQr: Button by lazy {
        findViewById(R.id.btn_Qr)
    }


    private val numberPicker: NumberPicker by lazy {
        findViewById(R.id.numberPicker)
    }


    private var didRun = false
    private val pickNumberSet = mutableSetOf<Int>()

    private val numberTvList: List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.tv_FirstView),
            findViewById<TextView>(R.id.tv_SecondView),
            findViewById<TextView>(R.id.tv_ThirdView),
            findViewById<TextView>(R.id.tv_FourthView),
            findViewById<TextView>(R.id.tv_FifthView),
            findViewById<TextView>(R.id.tv_sixthView)
        )
    }

    private val web_view: WebView by lazy {
        findViewById(R.id.wb_qr)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        numberPicker.minValue = 1
        numberPicker.maxValue = 45
        initRunButton()
        initAddButton()
        initResetButton()
        initQr()

        RetroifitManager.service.getNumber()?.enqueue(object : Callback<LottoNumber> {
            override fun onResponse(call: Call<LottoNumber>, response: Response<LottoNumber>) {
                if(response.isSuccessful){
                    // 정상적으로 통신이 성고된 경우
                    var result: LottoNumber? = response.body()
                    Log.d("YMC", "onResponse 성공: " + result?.toString());
                }else{
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("YMC", "onResponse 실패")
                }
            }

            override fun onFailure(call: Call<LottoNumber>, t: Throwable) {
                // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                Log.d("YMC", "onFailure 에러: " + t.message.toString());
            }
        })

    }


    private fun initQr()
    {
        runQr.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE) // 여러가지 바코드중에 특정 바코드 설정 가능
            integrator.setPrompt("QR 코드를 스캔하여 주세요.") // 스캔할 때 하단의 문구
            integrator.setCameraId(0) // 0은 후면 카메라, 1은 전면 카메라
            integrator.setBeepEnabled(false) // 바코드를 인식했을 때 삑 소리유무
            integrator.setBarcodeImageEnabled(true) // 스캔 했을 때 스캔한 이미지 사용여부
            integrator.setOrientationLocked(false)
            integrator.initiateScan() // 스캔
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // QR 코드를 찍은 결과를 변수에 담는다.

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        Log.d("TTT", "QR 코드 체크")

        //결과가 있으면
        if (result != null) {
            // 컨텐츠가 없으면
            if (result.contents == null) {
                //토스트를 띄운다.
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
            // 컨텐츠가 있으면
            else {
                //토스트를 띄운다.
                Toast.makeText(this, "scanned" + result.contents, Toast.LENGTH_LONG).show()
                Log.d("TTT", "QR 코드 URL:${result.contents}")

                //웹뷰 설정
                web_view.settings.javaScriptEnabled = true
                web_view.webViewClient = WebViewClient()

                //웹뷰를 띄운다.
                web_view.loadUrl(result.contents)
            }
            // 결과가 없으면
        }
    }

    private fun initRunButton() {
        runButton.setOnClickListener {
            val list = getRandomNumber()

            didRun = true;

            list.forEachIndexed { index, number ->

                if( index >= pickNumberSet.size)
                {
                    val textView = numberTvList[index]
                    textView.text = number.toString()
                    textView.isVisible = true
                    setNumberBackgroun(number, textView)
                }
            }

        }
    }

    private fun getRandomNumber(): List<Int> {
        val numberList = mutableListOf<Int>().apply {
            for (i in 1..45)
                this.add(i)
        }
        numberList.shuffle()
        val newList = numberList.subList(0, 6)
        return newList.sorted()
    }

    private fun initAddButton() {
        addButton.setOnClickListener {
            if (didRun) {
                Toast.makeText(this, "초기화 후 사용해주세요.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (pickNumberSet.size >= 5) {
                Toast.makeText(this, "번호는 5개까지 선택 가능합니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (pickNumberSet.contains(numberPicker.value)) {
                Toast.makeText(this, "이미 선택한 번호입니다.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val textView = numberTvList[pickNumberSet.size]
            textView.isVisible = true
            textView.text = numberPicker.value.toString()
            setNumberBackgroun(numberPicker.value, textView)
            pickNumberSet.add(numberPicker.value)
        }
    }

    private fun setNumberBackgroun(number: Int, tv: TextView) {
        tv.background = when (number) {
            in 1..10 -> ContextCompat.getDrawable(this, R.drawable.circle_yellow)
            in 11..20 -> ContextCompat.getDrawable(this, R.drawable.circle_blue)
            in 21..30 -> ContextCompat.getDrawable(this, R.drawable.circle_red)
            in 31..40 -> ContextCompat.getDrawable(this, R.drawable.circle_gray)
            else -> ContextCompat.getDrawable(this, R.drawable.circle_green)
        }
    }

    private fun initResetButton() {
        resetButton.setOnClickListener {
            pickNumberSet.clear()
            numberTvList.forEach {
                it.isVisible = false
            }
            didRun = false;
        }
    }
}
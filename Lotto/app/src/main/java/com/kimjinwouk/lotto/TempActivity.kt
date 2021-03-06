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
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.zxing.integration.android.IntentIntegrator
import com.kimjinwouk.lotto.DTO.LottoNumber
import com.kimjinwouk.lotto.DTO.winLotto
import com.kimjinwouk.lotto.Retrofit.Interface.RetroifitManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TempActivity : BaseActivity() {

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
    private val winLottoData = mutableSetOf<winLotto>()

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
        initCrawling()

        RetroifitManager.service.getNumber()?.enqueue(object : Callback<LottoNumber> {
            override fun onResponse(call: Call<LottoNumber>, response: Response<LottoNumber>) {
                if (response.isSuccessful) {
                    // ??????????????? ????????? ????????? ??????
                    var result: LottoNumber? = response.body()
                    Log.d("YMC", "onResponse ??????: " + result?.toString());
                } else {
                    // ????????? ????????? ??????(???????????? 3xx, 4xx ???)
                    Log.d("YMC", "onResponse ??????")
                }
            }

            override fun onFailure(call: Call<LottoNumber>, t: Throwable) {
                // ?????? ?????? (????????? ??????, ?????? ?????? ??? ??????????????? ??????)
                Log.d("YMC", "onFailure ??????: " + t.message.toString());
            }
        })

    }


    private fun initCrawling() {

        var contentData: Element
        var address:String = ""
        var type : String = ""
        var name : String = ""

        CoroutineScope(Dispatchers.IO).async {
            val url = "https://dhlottery.co.kr/store.do?method=topStore&pageGubun=L645"
            val doc = Jsoup.connect(url).timeout(1000 * 10).get()  //???????????? 10???
            contentData =
                doc.select("html body div section div div div div.group_content")[0].select("table tbody")[0]

            //?????? 1????????? ????????? ??????????????? ??????
            contentData.children().forEachIndexed { index, element ->
                val _winLotto =  winLotto(
                    element.select("td")[1].text(),
                    element.select("td")[2].text(),
                    element.select("td")[3].text()
                )
                winLottoData.add(_winLotto)
            }
        }
    }

    private fun initQr() {
        runQr.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE) // ???????????? ??????????????? ?????? ????????? ?????? ??????
            integrator.setPrompt("QR ????????? ???????????? ?????????.") // ????????? ??? ????????? ??????
            integrator.setCameraId(0) // 0??? ?????? ?????????, 1??? ?????? ?????????
            integrator.setBeepEnabled(false) // ???????????? ???????????? ??? ??? ????????????
            integrator.setBarcodeImageEnabled(true) // ?????? ?????? ??? ????????? ????????? ????????????
            integrator.setOrientationLocked(false)
            integrator.initiateScan() // ??????
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // QR ????????? ?????? ????????? ????????? ?????????.

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        Log.d("TTT", "QR ?????? ??????")

        //????????? ?????????
        if (result != null) {
            // ???????????? ?????????
            if (result.contents == null) {
                //???????????? ?????????.
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
            // ???????????? ?????????
            else {
                //???????????? ?????????.
                Toast.makeText(this, "scanned" + result.contents, Toast.LENGTH_LONG).show()
                Log.d("TTT", "QR ?????? URL:${result.contents}")

                //?????? ??????
                web_view.settings.javaScriptEnabled = true
                web_view.webViewClient = WebViewClient()

                //????????? ?????????.
                web_view.loadUrl(result.contents)
            }
            // ????????? ?????????
        }
    }

    private fun initRunButton() {
        runButton.setOnClickListener {
            val list = getRandomNumber()

            didRun = true;

            list.forEachIndexed { index, number ->

                if (index >= pickNumberSet.size) {
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
                Toast.makeText(this, "????????? ??? ??????????????????.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (pickNumberSet.size >= 5) {
                Toast.makeText(this, "????????? 5????????? ?????? ???????????????.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (pickNumberSet.contains(numberPicker.value)) {
                Toast.makeText(this, "?????? ????????? ???????????????.", Toast.LENGTH_LONG).show()
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
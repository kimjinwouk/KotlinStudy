package com.kimjinwouk.lotto

import android.os.Bundle
import android.util.Log
import com.kimjinwouk.lotto.DTO.winLotto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Element


class ResultActivity : BaseActivity() {

    private val winLottoData = mutableSetOf<winLotto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        init()
        initCrawling()
    }


    private fun init() {

    }


    private fun initCrawling() {

        var contentData: Element
        var address: String = ""
        var type: String = ""
        var name: String = ""

        CoroutineScope(Dispatchers.Main).launch {
            Log.d(
                "coroutine Dispatchers.Main start",
                "test"
            )
            //getData();
            getNumber();
            Log.d("coroutine Dispatchers.Main end", "test")
        }
    }

    suspend fun getData(): Boolean = withContext(Dispatchers.IO)
    {
        Log.d("coroutine Dispatchers.IO start", "test") // 페이지 끝 여부 var isEnd = false
        var isEnd = false
        var contentData: Element

        try {
            val url = "https://dhlottery.co.kr/store.do?method=topStore&pageGubun=L645"
            val doc = Jsoup.connect(url).timeout(1000 * 10).get()  //타임아웃 10초
            contentData =
                doc.select("html body div section div div div div.group_content")[0].select("table tbody")[0]

            //로또 1등부터 마지막 등수까지의 전체
            contentData.children().forEachIndexed { index, element ->
                val _winLotto = winLotto(
                    element.select("td")[1].text(),
                    element.select("td")[2].text(),
                    element.select("td")[3].text()
                )
                winLottoData.add(_winLotto)
            }

        } catch (httpStatusException: HttpStatusException) {
            isEnd = true
            Log.e("getImageInformation", httpStatusException.message.toString())
            httpStatusException.printStackTrace()
        } // 기타 오류
        catch (exception: Exception) {
            isEnd = true
            Log.e("getImageInformation", exception.message.toString())
            exception.printStackTrace()
        }

        Log.d("coroutine Dispatchers.IO end", "test")
        isEnd
    }


    suspend fun getNumber(): Boolean = withContext(Dispatchers.IO)
    {
        Log.d("coroutine Dispatchers.IO start", "test") // 페이지 끝 여부 var isEnd = false
        var isEnd = false
        var contentData: Element

        try {
            val url = "https://dhlottery.co.kr/gameResult.do?method=byWin"
            val doc = Jsoup.connect(url).timeout(1000 * 10).get()  //타임아웃 10초

            contentData = doc.select("html body div section div div div div h4 strong")[0].select("table tbody")[0]

            //로또 1등부터 마지막 등수까지의 전체
            contentData.children().forEachIndexed { index, element ->
                val _winLotto = winLotto(
                    element.select("td")[1].text(),
                    element.select("td")[2].text(),
                    element.select("td")[3].text()
                )
                winLottoData.add(_winLotto)
            }

        } catch (httpStatusException: HttpStatusException) {
            isEnd = true
            Log.e("getImageInformation", httpStatusException.message.toString())
            httpStatusException.printStackTrace()
        } // 기타 오류
        catch (exception: Exception) {
            isEnd = true
            Log.e("getImageInformation", exception.message.toString())
            exception.printStackTrace()
        }

        Log.d("coroutine Dispatchers.IO end", "test")
        isEnd
    }


}
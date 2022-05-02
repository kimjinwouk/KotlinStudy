package com.kimjinwouk.lotto.Fragment

import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.kimjinwouk.lotto.Adapter.ResultAdapter
import com.kimjinwouk.lotto.DTO.winLotto
import com.kimjinwouk.lotto.MyApp
import com.kimjinwouk.lotto.R
import kotlinx.coroutines.*
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.*


class resultFragment : Fragment() {


    private var winLottoData = ArrayList<winLotto>()
    private lateinit var tv_round: TextView
    private lateinit var tv_date: TextView
    private lateinit var tv_game_1: TextView
    private lateinit var tv_game_2: TextView
    private lateinit var tv_game_3: TextView
    private lateinit var tv_game_4: TextView
    private lateinit var tv_game_5: TextView
    private lateinit var tv_game_6: TextView
    private lateinit var tv_game_bonus: TextView
    private lateinit var tv_1st_cost_top: TextView
    private lateinit var tv_1st_cost: TextView
    private lateinit var tv_2st_cost: TextView
    private lateinit var tv_3st_cost: TextView
    private lateinit var tv_4st_cost: TextView
    private lateinit var tv_5st_cost: TextView
    private lateinit var tv_1st_cnt: TextView
    private lateinit var tv_2st_cnt: TextView
    private lateinit var tv_3st_cnt: TextView
    private lateinit var tv_4st_cnt: TextView
    private lateinit var tv_5st_cnt: TextView
    private lateinit var tv_text_result: TextView
    private lateinit var tv_text_shop_list: TextView
    private lateinit var ct_shop_list: ConstraintLayout
    private lateinit var ct_result: ConstraintLayout

    private lateinit var lv_1st: ListView
    private lateinit var mAdapter: ResultAdapter
    private lateinit var gson: Gson

    private lateinit var progressDialog: AppCompatDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_result, container, false)
        progressON()
        init(view);
        initListner();
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCrawling()
    }

    private fun init(view: View) {
        tv_round = view.findViewById(R.id.tv_round)
        tv_date = view.findViewById(R.id.tv_date)
        tv_game_1 = view.findViewById(R.id.tv_game_1)
        tv_game_2 = view.findViewById(R.id.tv_game_2)
        tv_game_3 = view.findViewById(R.id.tv_game_3)
        tv_game_4 = view.findViewById(R.id.tv_game_4)
        tv_game_5 = view.findViewById(R.id.tv_game_5)
        tv_game_6 = view.findViewById(R.id.tv_game_6)
        tv_game_bonus = view.findViewById(R.id.tv_game_bonus)
        tv_1st_cost_top = view.findViewById(R.id.tv_1st_cost_top)
        tv_1st_cost = view.findViewById(R.id.tv_1st_cost)
        tv_2st_cost = view.findViewById(R.id.tv_2st_cost)
        tv_3st_cost = view.findViewById(R.id.tv_3st_cost)
        tv_4st_cost = view.findViewById(R.id.tv_4st_cost)
        tv_5st_cost = view.findViewById(R.id.tv_5st_cost)
        tv_1st_cnt = view.findViewById(R.id.tv_1st_cnt)
        tv_2st_cnt = view.findViewById(R.id.tv_2st_cnt)
        tv_3st_cnt = view.findViewById(R.id.tv_3st_cnt)
        tv_4st_cnt = view.findViewById(R.id.tv_4st_cnt)
        tv_5st_cnt = view.findViewById(R.id.tv_5st_cnt)
        tv_text_result = view.findViewById(R.id.tv_text_result)
        tv_text_shop_list = view.findViewById(R.id.tv_text_shop_list)
        ct_shop_list = view.findViewById(R.id.ct_shop_list)
        ct_result = view.findViewById(R.id.ct_result)

        lv_1st = view.findViewById(R.id.lv_1st)

        gson = GsonBuilder().create()

    }

    private fun valueSetting() {

        tv_round.text = MyApp.prefs.getString("회차", "")
        tv_date.text = MyApp.prefs.getString("년월일", "")

        tv_game_1.text = MyApp.prefs.getString("1번호", "")
        setNumberBackgroun((tv_game_1.text as String).toInt(), tv_game_1)
        tv_game_2.text = MyApp.prefs.getString("2번호", "")
        setNumberBackgroun((tv_game_2.text as String).toInt(), tv_game_2)
        tv_game_3.text = MyApp.prefs.getString("3번호", "")
        setNumberBackgroun((tv_game_3.text as String).toInt(), tv_game_3)
        tv_game_4.text = MyApp.prefs.getString("4번호", "")
        setNumberBackgroun((tv_game_4.text as String).toInt(), tv_game_4)
        tv_game_5.text = MyApp.prefs.getString("5번호", "")
        setNumberBackgroun((tv_game_5.text as String).toInt(), tv_game_5)
        tv_game_6.text = MyApp.prefs.getString("6번호", "")
        setNumberBackgroun((tv_game_6.text as String).toInt(), tv_game_6)
        tv_game_bonus.text = MyApp.prefs.getString("보너스", "")
        setNumberBackgroun((tv_game_bonus.text as String).toInt(), tv_game_bonus)


        tv_1st_cost_top.text = MyApp.prefs.getString("1등_당첨금", "")
        tv_1st_cost.text = MyApp.prefs.getString("1등_당첨금", "")
        tv_1st_cnt.text = MyApp.prefs.getString("1등_당첨수", "")

        tv_2st_cost.text = MyApp.prefs.getString("2등_당첨금", "")
        tv_2st_cnt.text = MyApp.prefs.getString("2등_당첨수", "")

        tv_3st_cost.text = MyApp.prefs.getString("3등_당첨금", "")
        tv_3st_cnt.text = MyApp.prefs.getString("3등_당첨수", "")

        tv_4st_cost.text = MyApp.prefs.getString("4등_당첨금", "")
        tv_4st_cnt.text = MyApp.prefs.getString("4등_당첨수", "")

        tv_5st_cost.text = MyApp.prefs.getString("5등_당첨금", "")
        tv_5st_cnt.text = MyApp.prefs.getString("5등_당첨수", "")

        val doc: Document = Jsoup.parse(MyApp.prefs.getString("당첨판매점", ""))

        val element: Element = doc
        //로또 1등부터 마지막 등수까지의 전체


        val test: String = MyApp.prefs.getString("당첨판매점", "").toString()
        winLottoData = gson.fromJson(
            test,
            object : TypeToken<ArrayList<winLotto?>>() {}.type
        )

/*
        (Jsoup.parse(MyApp.prefs.getString("당첨판매점","")) as Element).children().forEachIndexed { index, element ->
            val _winLotto = winLotto(
                element.select("td")[1].text(),
                element.select("td")[2].text(),
                element.select("td")[3].text()
            )
            winLottoData.add(_winLotto)
        }
 */
        mAdapter = ResultAdapter(winLottoData)
        lv_1st.adapter = mAdapter
        progressOFF()
    }

    private fun initCrawling() {


        /*
        * 크롤링하기전에 해당일자에 값이 있는지 판단하고 처리.
        *
        * 오늘날짜와 MyApp.prefs.getString("년월일", "") 비교.
        * MyApp.prefs.getString("년월일", "") <-- 토요일 9시 고정. 오늘날짜가 저 시간보다 + 7일 이상이면 새롭게 받아와야한다.
        * ex) 2022년 04월 23일 21시 이면 오늘날짜가 30일 21시 이전에만 유효하고 21시 9시 이후가 되면 새롭게 받아와야한다.
        * */

        val test: String = MyApp.prefs.getString("년월일", "(2022년 01월 01일)")

        val Result = Calendar.getInstance()
        Result.set(Calendar.YEAR, test.split(" ")[0].substring(1, 5).toInt())
        Result.set(Calendar.MONTH, test.split(" ")[1].substring(0, 2).toInt() - 1)
        Result.set(Calendar.DAY_OF_MONTH, test.split(" ")[2].substring(0, 2).toInt())
        Result.set(Calendar.HOUR_OF_DAY, 21)

        val Today = Calendar.getInstance()

        Today.set(Calendar.YEAR, 2022)
        Today.set(Calendar.MONTH, 3)
        Today.set(Calendar.DAY_OF_MONTH, 30)
        Today.set(Calendar.HOUR_OF_DAY, 21)

        val day =
            (getIgnoredTimeDays(Today.timeInMillis) - getIgnoredTimeDays(Result.timeInMillis)) / (24 * 60 * 60 * 1000)

        if (day >= 7) {
            reNewData()
        } else {
            uiUpdate()
        }
    }

    private fun setNumberBackgroun(number: Int, tv: TextView) {
        tv.background = when (number) {
            in 1..10 -> ContextCompat.getDrawable(requireContext(), R.drawable.circle_yellow)
            in 11..20 -> ContextCompat.getDrawable(requireContext(), R.drawable.circle_blue)
            in 21..30 -> ContextCompat.getDrawable(requireContext(), R.drawable.circle_red)
            in 31..40 -> ContextCompat.getDrawable(requireContext(), R.drawable.circle_gray)
            else -> ContextCompat.getDrawable(requireContext(), R.drawable.circle_green)
        }
    }


    private fun reNewData() {
        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.IO).async {
                getNumber()
                getData()
            }.await()
            uiUpdate()
        }
    }


    private fun getIgnoredTimeDays(time: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun initListner() {

        tv_text_result.setOnClickListener {
            tv_text_result.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            tv_text_shop_list.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
            ct_result.visibility = VISIBLE
            ct_shop_list.visibility = GONE
        }

        tv_text_shop_list.setOnClickListener {
            tv_text_result.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
            tv_text_shop_list.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            ct_result.visibility = GONE
            ct_shop_list.visibility = VISIBLE
        }
    }

    private fun getDataPasing(doc: Document) {


        //로또 1등부터 마지막 등수까지의 전체
        doc.select("html body div section div div div div.group_content")[0].select("table tbody")[0].children()
            .forEachIndexed { index, element ->
                val _winLotto = winLotto(
                    element.select("td")[1].text(),
                    element.select("td")[2].text(),
                    element.select("td")[3].text()
                )
                winLottoData.add(_winLotto)
            }


        MyApp.prefs.setString("당첨판매점", gson.toJson(winLottoData))
    }


    suspend fun getData(): Boolean {
        Log.d("coroutine Dispatchers.IO start", "test") // 페이지 끝 여부 var isEnd = false
        var isEnd = false
        var contentData: Element

        try {
            val url = "https://dhlottery.co.kr/store.do?method=topStore&pageGubun=L645"
            val doc = Jsoup.connect(url).timeout(1000 * 10).get()  //타임아웃 10초
            getDataPasing(doc)


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


        return isEnd
    }

    private fun uiUpdate() {
        valueSetting()
    }

    suspend fun getNumber(): Boolean {
        Log.d("coroutine Dispatchers.IO start", "test") // 페이지 끝 여부 var isEnd = false
        var isEnd = false
        var contentData: Element

        try {
            val url = "https://dhlottery.co.kr/gameResult.do?method=byWin"
            val doc = Jsoup.connect(url).timeout(1000 * 10).get()  //타임아웃 10초
            getNumberPasing(doc)

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
        return isEnd
    }

    private fun getNumberPasing(doc: Document) {
        // 데이터 조회
        MyApp.prefs.setString(
            "회차",
            doc.select("html body div section div div div div h4 strong").text()
        )
        MyApp.prefs.setString(
            "년월일",
            doc.select("html body div section div div div div p")[0].text()
        )

        MyApp.prefs.setString(
            "1번호",
            doc.select("html body div section div div div div p")[1].select("span")[0].text()
        )
        MyApp.prefs.setString(
            "2번호",
            doc.select("html body div section div div div div p")[1].select("span")[1].text()
        )
        MyApp.prefs.setString(
            "3번호",
            doc.select("html body div section div div div div p")[1].select("span")[2].text()
        )
        MyApp.prefs.setString(
            "4번호",
            doc.select("html body div section div div div div p")[1].select("span")[3].text()
        )
        MyApp.prefs.setString(
            "5번호",
            doc.select("html body div section div div div div p")[1].select("span")[4].text()
        )
        MyApp.prefs.setString(
            "6번호",
            doc.select("html body div section div div div div p")[1].select("span")[5].text()
        )

        MyApp.prefs.setString(
            "보너스",
            doc.select("html body div section div div div div p")[2].select("span").text()
        )


        MyApp.prefs.setString(
            "1등_순위",
            doc.select("html body div section div div div table tbody tr td")[0].text()
        )
        MyApp.prefs.setString(
            "1등_총당첨금",
            doc.select("html body div section div div div table tbody tr td")[1].text()
        )
        MyApp.prefs.setString(
            "1등_당첨수",
            doc.select("html body div section div div div table tbody tr td")[2].text()
        )
        MyApp.prefs.setString(
            "1등_당첨금",
            doc.select("html body div section div div div table tbody tr td")[3].text()
        )

        MyApp.prefs.setString(
            "2등_순위",
            doc.select("html body div section div div div table tbody tr")[1].select("td")[0].text()
        )
        MyApp.prefs.setString(
            "2등_총당첨금",
            doc.select("html body div section div div div table tbody tr")[1].select("td")[1].text()
        )
        MyApp.prefs.setString(
            "2등_당첨수",
            doc.select("html body div section div div div table tbody tr")[1].select("td")[2].text()
        )
        MyApp.prefs.setString(
            "2등_당첨금",
            doc.select("html body div section div div div table tbody tr")[1].select("td")[3].text()
        )

        MyApp.prefs.setString(
            "3등_순위",
            doc.select("html body div section div div div table tbody tr")[2].select("td")[0].text()
        )
        MyApp.prefs.setString(
            "3등_총당첨금",
            doc.select("html body div section div div div table tbody tr")[2].select("td")[1].text()
        )
        MyApp.prefs.setString(
            "3등_당첨수",
            doc.select("html body div section div div div table tbody tr")[2].select("td")[2].text()
        )
        MyApp.prefs.setString(
            "3등_당첨금",
            doc.select("html body div section div div div table tbody tr")[2].select("td")[3].text()
        )

        MyApp.prefs.setString(
            "4등_순위",
            doc.select("html body div section div div div table tbody tr")[3].select("td")[0].text()
        )
        MyApp.prefs.setString(
            "4등_총당첨금",
            doc.select("html body div section div div div table tbody tr")[3].select("td")[1].text()
        )
        MyApp.prefs.setString(
            "4등_당첨수",
            doc.select("html body div section div div div table tbody tr")[3].select("td")[2].text()
        )
        MyApp.prefs.setString(
            "4등_당첨금",
            doc.select("html body div section div div div table tbody tr")[3].select("td")[3].text()
        )

        MyApp.prefs.setString(
            "5등_순위",
            doc.select("html body div section div div div table tbody tr")[4].select("td")[0].text()
        )
        MyApp.prefs.setString(
            "5등_총당첨금",
            doc.select("html body div section div div div table tbody tr")[4].select("td")[1].text()
        )
        MyApp.prefs.setString(
            "5등_당첨수",
            doc.select("html body div section div div div table tbody tr")[4].select("td")[2].text()
        )
        MyApp.prefs.setString(
            "5등_당첨금",
            doc.select("html body div section div div div table tbody tr")[4].select("td")[3].text()
        )
    }

    fun progressON() {
        progressDialog = AppCompatDialog(requireContext())
        progressDialog.setCancelable(false)
        progressDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setContentView(R.layout.progress_result)
        progressDialog.show()
        var img_loading_framge = progressDialog.findViewById<ImageView>(R.id.iv_frame_loading)
        var frameAnimation = img_loading_framge?.getBackground() as AnimationDrawable
        img_loading_framge?.post(object : Runnable {
            override fun run() {
                frameAnimation.start()
            }
        })

        var tv_progress_message = progressDialog.findViewById<TextView>(R.id.tv_progress_message)
        tv_progress_message?.text = "데이터 불러오는 중.."

    }

    fun progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss()
        }
    }

}
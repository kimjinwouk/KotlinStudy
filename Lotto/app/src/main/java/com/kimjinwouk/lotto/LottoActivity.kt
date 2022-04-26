package com.kimjinwouk.lotto

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible


class LottoActivity : BaseActivity() {

    private val m_btnRun: Button by lazy {
        findViewById(R.id.btn_Run)
    }

    private val numberTvList_1: List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.tv_game_1_1),
            findViewById<TextView>(R.id.tv_game_1_2),
            findViewById<TextView>(R.id.tv_game_1_3),
            findViewById<TextView>(R.id.tv_game_1_4),
            findViewById<TextView>(R.id.tv_game_1_5),
            findViewById<TextView>(R.id.tv_game_1_6)
        )
    }

    private val numberTvList_2: List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.tv_game_2_1),
            findViewById<TextView>(R.id.tv_game_2_2),
            findViewById<TextView>(R.id.tv_game_2_3),
            findViewById<TextView>(R.id.tv_game_2_4),
            findViewById<TextView>(R.id.tv_game_2_5),
            findViewById<TextView>(R.id.tv_game_2_6)
        )
    }

    private val numberTvList_3: List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.tv_game_3_1),
            findViewById<TextView>(R.id.tv_game_3_2),
            findViewById<TextView>(R.id.tv_game_3_3),
            findViewById<TextView>(R.id.tv_game_3_4),
            findViewById<TextView>(R.id.tv_game_3_5),
            findViewById<TextView>(R.id.tv_game_3_6)
        )
    }

    private val numberTvList_4: List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.tv_game_4_1),
            findViewById<TextView>(R.id.tv_game_4_2),
            findViewById<TextView>(R.id.tv_game_4_3),
            findViewById<TextView>(R.id.tv_game_4_4),
            findViewById<TextView>(R.id.tv_game_4_5),
            findViewById<TextView>(R.id.tv_game_4_6)
        )
    }

    private val numberTvList_5: List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.tv_game_5_1),
            findViewById<TextView>(R.id.tv_game_5_2),
            findViewById<TextView>(R.id.tv_game_5_3),
            findViewById<TextView>(R.id.tv_game_5_4),
            findViewById<TextView>(R.id.tv_game_5_5),
            findViewById<TextView>(R.id.tv_game_5_6)
        )
    }

    private var ArrayList = arrayListOf<List<TextView>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lotto)

        init();
        initListner();
    }


    private fun init() {
        ArrayList.add(numberTvList_1)
        ArrayList.add(numberTvList_2)
        ArrayList.add(numberTvList_3)
        ArrayList.add(numberTvList_4)
        ArrayList.add(numberTvList_5)
    }


    private fun initListner() {

        m_btnRun.setOnClickListener {
            runLottoNumber()
        }

    }


    private fun runLottoNumber() {

        ArrayList.forEachIndexed { index, listTv ->
            val list = getRandomNumber()
            list.forEachIndexed { index, number ->
                val textView = listTv[index]
                textView.text = number.toString()
                textView.isVisible = true
                setNumberBackgroun(number, textView)
            }
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


    private fun getRandomNumber(): List<Int> {
        val numberList = mutableListOf<Int>().apply {
            for (i in 1..45)
                this.add(i)
        }
        numberList.shuffle()
        val newList = numberList.subList(0, 6)
        return newList.sorted()
    }


}
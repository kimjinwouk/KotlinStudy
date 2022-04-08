package com.kimjinwouk.lotto

import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    private val resetButton: Button by lazy {
        findViewById(R.id.btn_Reset)
    }

    private val addButton: Button by lazy {
        findViewById(R.id.btn_Add)
    }

    private val runButton: Button by lazy {
        findViewById(R.id.btn_Run)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        numberPicker.minValue = 1
        numberPicker.maxValue = 45
        initRunButton()
        initAddButton()
        initResetButton()
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
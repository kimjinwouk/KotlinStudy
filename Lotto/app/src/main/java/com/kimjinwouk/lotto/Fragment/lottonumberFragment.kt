package com.kimjinwouk.lotto.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.kimjinwouk.lotto.R


class lottonumberFragment : Fragment() {


    private lateinit var numberTvList_1: List<TextView>
    private lateinit var numberTvList_2: List<TextView>
    private lateinit var numberTvList_3: List<TextView>
    private lateinit var numberTvList_4: List<TextView>
    private lateinit var numberTvList_5: List<TextView>
    private lateinit var m_btnRun: Button
    private var ArrayList = arrayListOf<List<TextView>>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_lottonumber, container, false)

        init(view);
        initListner();
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initListner()
    {
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
            in 1..10 -> ContextCompat.getDrawable(requireContext(), R.drawable.circle_yellow)
            in 11..20 -> ContextCompat.getDrawable(requireContext(), R.drawable.circle_blue)
            in 21..30 -> ContextCompat.getDrawable(requireContext(), R.drawable.circle_red)
            in 31..40 -> ContextCompat.getDrawable(requireContext(), R.drawable.circle_gray)
            else -> ContextCompat.getDrawable(requireContext(), R.drawable.circle_green)
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



    private fun init(view: View) {
        m_btnRun = view.findViewById(R.id.btn_Run)
        numberTvList_1 =
            listOf<TextView>(
                view.findViewById<TextView>(R.id.tv_game_1_1),
                view.findViewById<TextView>(R.id.tv_game_1_2),
                view.findViewById<TextView>(R.id.tv_game_1_3),
                view.findViewById<TextView>(R.id.tv_game_1_4),
                view.findViewById<TextView>(R.id.tv_game_1_5),
                view.findViewById<TextView>(R.id.tv_game_1_6)
            )
        numberTvList_2 =
            listOf<TextView>(
                view.findViewById<TextView>(R.id.tv_game_2_1),
                view.findViewById<TextView>(R.id.tv_game_2_2),
                view.findViewById<TextView>(R.id.tv_game_2_3),
                view.findViewById<TextView>(R.id.tv_game_2_4),
                view.findViewById<TextView>(R.id.tv_game_2_5),
                view.findViewById<TextView>(R.id.tv_game_2_6)
            )
        numberTvList_3 =
            listOf<TextView>(
                view.findViewById<TextView>(R.id.tv_game_3_1),
                view.findViewById<TextView>(R.id.tv_game_3_2),
                view.findViewById<TextView>(R.id.tv_game_3_3),
                view.findViewById<TextView>(R.id.tv_game_3_4),
                view.findViewById<TextView>(R.id.tv_game_3_5),
                view.findViewById<TextView>(R.id.tv_game_3_6)
            )
        numberTvList_4 =
            listOf<TextView>(
                view.findViewById<TextView>(R.id.tv_game_4_1),
                view.findViewById<TextView>(R.id.tv_game_4_2),
                view.findViewById<TextView>(R.id.tv_game_4_3),
                view.findViewById<TextView>(R.id.tv_game_4_4),
                view.findViewById<TextView>(R.id.tv_game_4_5),
                view.findViewById<TextView>(R.id.tv_game_4_6)
            )
        numberTvList_5 =
            listOf<TextView>(
                view.findViewById<TextView>(R.id.tv_game_5_1),
                view.findViewById<TextView>(R.id.tv_game_5_2),
                view.findViewById<TextView>(R.id.tv_game_5_3),
                view.findViewById<TextView>(R.id.tv_game_5_4),
                view.findViewById<TextView>(R.id.tv_game_5_5),
                view.findViewById<TextView>(R.id.tv_game_5_6)
            )

        ArrayList.add(numberTvList_1)
        ArrayList.add(numberTvList_2)
        ArrayList.add(numberTvList_3)
        ArrayList.add(numberTvList_4)
        ArrayList.add(numberTvList_5)



    }
}
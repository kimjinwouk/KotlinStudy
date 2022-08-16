package com.nfc.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.nfc.R
import com.nfc.data.Statistics
import com.nfc.databinding.FragmentStatisticsBinding
import com.nfc.util.TimeAxisValueFormat
import com.nfc.viewModel.nfcViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_statistics) {


    //뷰바인딩
    private lateinit var binding: FragmentStatisticsBinding

    // 뷰모델 생성
    private val viewModel by activityViewModels<nfcViewModel>()

    // Context
    private lateinit var _context: Context

    // SharedPreferences 주입
    @Inject
    lateinit var sharedPref: SharedPreferences


    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStatisticsBinding.bind(view)

        binding.apply {
            val xAxis = userStatsLineChart.xAxis

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM // x 축 데이터 위치 BOTTOM 으로
                textSize = 12f // 텍스트 크기지정 (float 형태로)
                setDrawGridLines(false) // 배경 그리드 라인
                granularity = 1f // x축 데이터 표시 간격
                axisMinimum = 0f // x축 데이터의 최소 표시값
                isGranularityEnabled = true // x축 간격을 제한하는 세분화 기능
                valueFormatter = TimeAxisValueFormat()

            }

            userStatsLineChart.apply {
                axisRight.isEnabled = false // y축의 오른쪽 데이터 비활성화
                axisLeft.axisMaximum = 50f // y축의 왼쪽 데이터 최대값은 50
                legend.apply {   //범례 세팅
                    textSize = 15f // 글자크기지정
                    verticalAlignment = Legend.LegendVerticalAlignment.TOP // 수직 -> 위초
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER // 수평 -> 가운데
                    orientation = Legend.LegendOrientation.HORIZONTAL // 범례와 차트 정렬 -> 수평
                    setDrawInside(false) // 차트안에 넣을것인가
                }
            }
        }

        /*
        * 데이터 불러오기
        * */
        viewModel.statistics()
        viewModel.objStatistics.observe(viewLifecycleOwner) {
            it?.let {
                var chartData = LineData()
                chartData.addDataSet(createSet(it))

                binding.userStatsLineChart.apply {
                    data = chartData
                    notifyDataSetChanged() // 라인차트 변경알림
                    moveViewToX(chartData.entryCount.toFloat()) // 계속 x축을 데이터의 오른쪽 끝으로 옮기기
                    setVisibleXRangeMaximum(7f) // x축 데이터 최대 표현 개수
                    setPinchZoom(false) //확대설정
                    isDoubleTapToZoomEnabled = false // 더블탭 확대설정
                    //setBackgroundColor(resources.getColor(R.color.black)) // 배경색
                    description.textSize = 25f //텍스트 사이즈
                    setExtraOffsets(16f, 16f, 16f, 16f) //차트 패딩 설정
                    description.text = ""
                    axisLeft.axisMaximum = yMaxmum(it)
                    axisLeft.axisMinimum = yMinimum(it)
                }

            }
        }

    }

    private fun yMinimum(list: List<Statistics>): Float {
        return 0f
    }

    private fun yMaxmum(list: List<Statistics>): Float {
        return list.maxOf { it.RiderCount.toInt() + 100}.toFloat()
    }

    private fun createSet(list: List<Statistics>): LineDataSet {
        var data = arrayListOf<Entry>()
        list.forEachIndexed { index, statistics ->
            data.add(Entry(statistics.Nid.toFloat(), statistics.RiderCount.toFloat()))
        }
        val set = LineDataSet(
            data, "사용 현황(기사수)"
        )
        set.apply {
            axisDependency = YAxis.AxisDependency.LEFT // Y값 데이터를 왼쪽으로. RIGHT 해도 안먹힌다..
            color = resources.getColor(R.color.purple_500) // 라인색상
            setCircleColor(resources.getColor(R.color.purple_500)) // 데이터 원형 색 지정
            valueTextSize = 15f // 값 글자 크기
            lineWidth = 1f // 라인 ㅜㄷ께
            circleRadius = 2.5f // 원 크기
            highLightColor = Color.BLACK // 하이라이트 컬러지정
            setDrawValues(true) //값 그리기
            isHighlightEnabled = false // 클릭했을때 십자가 표시


        }
        return set


    }

}
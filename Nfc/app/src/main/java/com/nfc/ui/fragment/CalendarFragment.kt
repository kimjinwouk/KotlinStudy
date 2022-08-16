package com.nfc.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import com.nfc.R
import com.nfc.adapter.calendarAdapter
import com.nfc.data.Profits
import com.nfc.databinding.CalendarDayBinding
import com.nfc.databinding.CalendarDayLegendBinding
import com.nfc.databinding.CalendarHeaderBinding
import com.nfc.databinding.FragmentCalendarBinding
import com.nfc.util.Util.Companion.daysOfWeekFromLocale
import com.nfc.util.Util.Companion.setTextColorRes
import com.nfc.viewModel.nfcViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment : Fragment(R.layout.fragment_calendar) {


    //뷰바인딩
    private lateinit var binding: FragmentCalendarBinding

    // 뷰모델 생성
    private val viewModel by activityViewModels<nfcViewModel>()

    // Context
    private lateinit var _context: Context

    // SharedPreferences 주입
    @Inject
    lateinit var sharedPref: SharedPreferences

    private var selectedDate: LocalDate? = null
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MM")
    private val calendarAdapter = calendarAdapter()

    private var tmp: Map<LocalDate, List<Profits>>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCalendarBinding.bind(view)

        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()

        binding.apply {

            exFiveRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter = calendarAdapter
                addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
            }

            exFiveCalendar.setup(
                currentMonth.minusMonths(10),
                currentMonth.plusMonths(10),
                daysOfWeek.first()
            )
            exFiveCalendar.scrollToMonth(currentMonth)
        }

        /*
              * exFiveCalendar 달력
              * DayViewContainer 날짜 한칸에 대한 정의
              * */
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            val binding = this@CalendarFragment.binding
                            binding.exFiveCalendar.notifyDateChanged(day.date)
                            oldDate?.let {
                                binding.exFiveCalendar.notifyDateChanged(it)
                            }
                            updateAdapterForDate(day.date)
                            //Toast.makeText(requireContext(), "{$day.date} 클릭", Toast.LENGTH_SHORT)
                            //    .show()


                        }
                    }
                }
            }
        }

        binding.exFiveCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding.exFiveDayText
                val imageView = container.binding.exFiveDayToday
                val layout = container.binding.exFiveDayLayout
                textView.text = day.date.dayOfMonth.toString()

                val flightTopView = container.binding.exFiveDayFlightTop
                val flightBottomView = container.binding.exFiveDayFlightBottom
                flightTopView.background = null
                flightBottomView.background = null
                imageView.background = null


                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.setTextColorRes(R.color.example_5_text_grey_light)
                    textView.setBackgroundResource(if (selectedDate == day.date) R.drawable.calendarview_selected_background else 0)
                    //오늘자 반전 표시
                    if (day.date == LocalDate.now()) {
                        textView.setBackgroundResource(R.drawable.calendarview_today_background)
                        textView.setTextColorRes(R.color.white)
                    }
                } else {
                    layout.background = null
                    textView.setTextColorRes(
                        when (day.date.dayOfWeek.ordinal) {
                            6 -> R.color.calendar_other_month_sunday_txt_color
                            5 -> R.color.calendar_other_month_saturday_txt_color
                            else -> R.color.calendar_other_month_normal_txt_color
                        }
                    )
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = (CalendarHeaderBinding.bind(view).legendLayout as CalendarDayLegendBinding).root
        }
        binding.exFiveCalendar.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map { it as TextView }
                        .forEachIndexed { index, tv ->
                            tv.text =
                                daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.KOREAN)
                                    .toUpperCase(Locale.ENGLISH)
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                        }
                    month.yearMonth
                }
            }
        }

        binding.exFiveCalendar.monthScrollListener = { month ->
            val title = "${month.yearMonth.year}.${monthTitleFormatter.format(month.yearMonth)} "
            binding.exFiveMonthYearText.text = title

            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                binding.exFiveCalendar.notifyDateChanged(it)
                updateAdapterForDate(null)
            }
        }

        binding.exFiveNextMonthImage.setOnClickListener {
            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.next)
                Toast.makeText(requireContext(), "다음달 갱신", Toast.LENGTH_SHORT)
                viewModel.profits("${it.yearMonth.year}${monthTitleFormatter.format(it.yearMonth.next)}")
            }
        }

        binding.exFivePreviousMonthImage.setOnClickListener {
            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.previous)
                Toast.makeText(requireContext(), "이전달 갱신", Toast.LENGTH_SHORT)
                viewModel.profits("${it.yearMonth.year}${monthTitleFormatter.format(it.yearMonth.previous)}")
            }
        }

        viewModel.profits(LocalDate.now().toString())

        viewModel.objProfits.observe(viewLifecycleOwner){
            it?.let{
                tmp = it.groupBy {
                        result -> LocalDate.parse(result.ODate, DateTimeFormatter.ISO_DATE);
                }
            }
        }
    }



    private fun updateAdapterForDate(date: LocalDate?) {
        calendarAdapter.Profits.clear()
        tmp?.let{
            calendarAdapter.Profits.addAll(it[date].orEmpty())
            calendarAdapter.notifyDataSetChanged()
        }
    }
}
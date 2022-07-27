package com.kimjinwouk.petwalk.ui.fragment

import a.jinkim.calculate.model.Walking
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kimjinwouk.petwalk.R
import com.kimjinwouk.petwalk.adapter.WalkListAdapter
import com.kimjinwouk.petwalk.databinding.CalendarDayBinding
import com.kimjinwouk.petwalk.databinding.CalendarHeaderBinding
import com.kimjinwouk.petwalk.databinding.FragmentWalkinglistBinding
import com.kimjinwouk.petwalk.util.Constants
import com.kimjinwouk.petwalk.util.PetWalkUtil.Companion.daysOfWeekFromLocale
import com.kimjinwouk.petwalk.util.PetWalkUtil.Companion.getColorCompat
import com.kimjinwouk.petwalk.util.PetWalkUtil.Companion.setTextColorRes
import com.kimjinwouk.petwalk.util.generateFlights
import com.kimjinwouk.petwalk.viewmodel.walkViewModel
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.observeOn
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@AndroidEntryPoint
class WalkingListFragment : Fragment(R.layout.fragment_walkinglist) {

    // 전역 변수로 바인딩 객체 선언
    private var _binding: FragmentWalkinglistBinding? = null

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = _binding!!

    // 뷰모델 생성
    private val viewModel by viewModels<walkViewModel>()

    private var selectedDate: LocalDate? = null
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val walkingAdapter = WalkListAdapter()

    data class Flight(
        val time: LocalDateTime,
        val departure: Airport,
        val destination: Airport,
        @ColorRes val color: Int
    ) {
        data class Airport(val city: String, val code: String)
    }


    private var tmp : Map<LocalDate, List<Walking>> ?= null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(Constants.TAG, "onVieCreate")
        _binding = FragmentWalkinglistBinding.bind(view)
        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()

        viewModel.walks.observe(requireActivity(),{
            tmp = it.groupBy { it.Date.toLocalDate() }
        })

        binding.apply {

            exFiveRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter = walkingAdapter
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
                            val binding = this@WalkingListFragment.binding
                            binding.exFiveCalendar.notifyDateChanged(day.date)
                            oldDate?.let { binding.exFiveCalendar.notifyDateChanged(it) }
                            updateAdapterForDate(day.date)
                            Toast.makeText(requireContext(), "{$day.date} 클릭", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }

        binding.exFiveCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.binding.exFiveDayText
                val layout = container.binding.exFiveDayLayout
                textView.text = day.date.dayOfMonth.toString()

                val flightTopView = container.binding.exFiveDayFlightTop
                val flightBottomView = container.binding.exFiveDayFlightBottom
                flightTopView.background = null
                flightBottomView.background = null

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.setTextColorRes(R.color.example_5_text_grey_light)
                    layout.setBackgroundResource(if (selectedDate == day.date) R.drawable.ic_baseline_run_circle_24 else 0)

                    val flights = tmp!![day.date]
                    if (flights != null) {
                        if (flights.count() == 1) {
                            flightBottomView.setBackgroundColor(R.color.black)
                        } else {
                            flightTopView.setBackgroundColor(R.color.black)
                            flightBottomView.setBackgroundColor(R.color.black)
                        }
                    }
                } else {
                    textView.setTextColorRes(R.color.example_5_text_grey)
                    layout.background = null
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
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
                            tv.setTextColorRes(R.color.example_5_text_grey)
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                        }
                    month.yearMonth
                }
            }
        }

        binding.exFiveCalendar.monthScrollListener = { month ->
            val title = "${monthTitleFormatter.format(month.yearMonth)} ${month.yearMonth.year}"
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
            }
        }

        binding.exFivePreviousMonthImage.setOnClickListener {
            binding.exFiveCalendar.findFirstVisibleMonth()?.let {
                binding.exFiveCalendar.smoothScrollToMonth(it.yearMonth.previous)
            }
        }


    }

    override fun onStart() {
        super.onStart()
        requireActivity().window.statusBarColor =
            requireContext().getColorCompat(R.color.example_5_toolbar_color)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.black)
    }

    private fun updateAdapterForDate(date: LocalDate?) {
        walkingAdapter.flights.clear()
        walkingAdapter.flights.addAll(tmp!![date].orEmpty())
        walkingAdapter.notifyDataSetChanged()
    }
}


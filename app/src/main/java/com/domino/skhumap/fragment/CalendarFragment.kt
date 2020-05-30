package com.domino.skhumap.fragment

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.arlib.floatingsearchview.util.Util.dpToPx
import com.domino.skhumap.R
import com.domino.skhumap.adapter.EventListAdapter
import com.domino.skhumap.dto.Event
import com.domino.skhumap.utils.*
import com.domino.skhumap.utils.setTextColorRes
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.yearMonth
import kotlinx.android.synthetic.main.calendar_day_legend.view.*
import kotlinx.android.synthetic.main.calendar_day.view.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class CalendarFragment : Fragment() {
    private val Context.inputMethodManager get() = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    private val events = mutableMapOf<LocalDate, List<Event>>()
    private val eventsAdapter = EventListAdapter {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.dialog_event_delete_confirmation)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteEvent(it)
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }
    private val inputDialog by lazy {
        val editText = AppCompatEditText(requireContext())
        val layout = FrameLayout(requireContext()).apply {
            // Setting the padding on the EditText only pads the input area
            // not the entire EditText so we wrap it in a FrameLayout.
            val padding = dpToPx(20)
            setPadding(padding, padding, padding, padding)
            addView(editText, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_event_input_title))
            .setView(layout)
            .setPositiveButton(R.string.save) { _, _ ->
                saveEvent(editText.text.toString())
                // Prepare EditText for reuse.
                editText.setText("")
            }
            .setNegativeButton(R.string.close, null)
            .create()
            .apply {
                setOnShowListener {
                    // Show the keyboard
                    editText.requestFocus()
                    context.inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
                setOnDismissListener {
                    // Hide the keyboard
                    context.inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                }
            }
    }

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    private var monthToWeek:Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(1)
        val endMonth = currentMonth.plusMonths(10)

        /* 날짜 컨테이터 클래스 */
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val textView = view.txt_calendar_day
            val dotView = view.img_calendar_day_dot
            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)
                    }
                }
            }
        }
        /* 캘린더 날짜 바인딩 */
        calendar_view.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                val dotView = container.dotView

                textView.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.makeVisible()
                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.calendar_white)
                            textView.setBackgroundResource(R.drawable.bg_calendar_today)
                            dotView.makeInVisible()
                        }
                        selectedDate -> {
                            textView.setTextColorRes(R.color.calendar_blue)
                            textView.setBackgroundResource(R.drawable.bg_calendar_selected)
                            dotView.makeInVisible()
                        }
                        else -> {
                            textView.setTextColorRes(
                                when(day.date.dayOfWeek.value){
                                    6 -> R.color.calendar_saturday
                                    7 -> R.color.calendar_sunday
                                    else->R.color.calendar_black
                                })
                            textView.background = null
                            dotView.isVisible = events[day.date].orEmpty().isNotEmpty()
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }
        /* 캘린더 스크롤에 따른 월 변경 바인딩 */
        calendar_view.monthScrollListener = {
            if (calendar_view.maxRowCount == 6) {
                txt_one_year.text = it.yearMonth.year.toString()
                txt_one_month.text = monthTitleFormatter.format(it.yearMonth)
                if(today.yearMonth.toString() == it.yearMonth.toString())
                    selectDate(today)
                else
                    selectDate(it.yearMonth.atDay(1))
            } else {
                val firstDate = it.weekDays.first().first().date
                val lastDate = it.weekDays.last().last().date
                if (firstDate.yearMonth == lastDate.yearMonth) {
                    txt_one_year.text = firstDate.yearMonth.year.toString()
                    txt_one_month.text = monthTitleFormatter.format(firstDate)
                } else {
                    txt_one_month.text =
                        "${monthTitleFormatter.format(firstDate)} - ${monthTitleFormatter.format(lastDate)}"
                    if (firstDate.year == lastDate.year) {
                        txt_one_year.text = firstDate.yearMonth.year.toString()
                    } else {
                        txt_one_year.text = "${firstDate.yearMonth.year} - ${lastDate.yearMonth.year}"
                    }
                }
                if(firstDate<=today && lastDate>=today)
                    selectDate(today)
                else
                    selectDate(firstDate)
            }
            btn_calendar_mode_toggle.isEnabled = true
        }
        /* 헤더(요일) 컨테이터 클래스 */
        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = view.legendLayout
        }
        /* 캘린더 헤더 바인딩 */
        calendar_view.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                        tv.text = daysOfWeek[index].name.first().toString()
                        tv.setTextColorRes(when(index){
                            0 -> R.color.calendar_sunday
                            6 -> R.color.calendar_saturday
                            else -> R.color.calendar_black
                        })
                    }
                }
            }
        }
        /* 월 모드와 주 모드 토글 버튼 리스너 */
        btn_calendar_mode_toggle.setOnClickListener {
            btn_calendar_mode_toggle.isEnabled = false
            monthToWeek = !monthToWeek

            val firstDate = calendar_view.findFirstVisibleDay()?.date ?: return@setOnClickListener
            val lastDate = calendar_view.findLastVisibleDay()?.date ?: return@setOnClickListener
            val currentVisibleMonth = calendar_view.findFirstVisibleMonth()?.yearMonth ?: return@setOnClickListener

            val oneWeekHeight = calendar_view.dayHeight
            val oneMonthHeight = oneWeekHeight * 6

            val oldHeight = if (monthToWeek) oneMonthHeight else oneWeekHeight
            val newHeight = if (monthToWeek) oneWeekHeight else oneMonthHeight

            val animator = ValueAnimator.ofInt(oldHeight, newHeight)
            animator.addUpdateListener { animator ->
                calendar_view.layoutParams = calendar_view.layoutParams.apply {
                    height = animator.animatedValue as Int
                }
            }
            animator.doOnStart {
                if (!monthToWeek) {
                    calendar_view.inDateStyle = InDateStyle.ALL_MONTHS
                    calendar_view.maxRowCount = 6
                    calendar_view.hasBoundaries = true
                    btn_calendar_mode_toggle.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
                }
            }
            animator.doOnEnd {
                if (monthToWeek) {
                    calendar_view.inDateStyle = InDateStyle.FIRST_MONTH
                    calendar_view.maxRowCount = 1
                    calendar_view.hasBoundaries = false
                    btn_calendar_mode_toggle.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
                }

                if (monthToWeek) {
                    if (today.yearMonth ==  currentVisibleMonth)
                        calendar_view.scrollToDate(today)
                    else
                        calendar_view.scrollToDate(firstDate)
                } else {
                    if (firstDate.yearMonth == lastDate.yearMonth)
                        calendar_view.scrollToMonth(firstDate.yearMonth)
                    else
                        calendar_view.scrollToMonth(minOf(firstDate.yearMonth.next, endMonth))
                }
            }
            animator.duration = 250
            animator.start()
        }
    }
    /* 이벤트 저장 */
    private fun saveEvent(text: String) {
        if (text.isBlank()) {
            Toast.makeText(requireContext(), R.string.empty_input_text, Toast.LENGTH_LONG).show()
        } else {
            selectedDate?.let {
                events[it] = events[it].orEmpty().plus(Event(UUID.randomUUID().toString(), text, it))
                updateAdapterForDate(it)
            }
        }
    }
    /* 캘린더 날짜 클릭 함수 */
    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            calendar_view.post {
                oldDate?.let { calendar_view.notifyDateChanged(it) }
                calendar_view.notifyDateChanged(date)
            }
            updateAdapterForDate(date)
        }
    }
    /* 이벤트 삭제 */
    private fun deleteEvent(event: Event) {
        val date = event.date
        events[date] = events[date].orEmpty().minus(event)
        updateAdapterForDate(date)
    }
    /* 어댑터 업데이트 */
    private fun updateAdapterForDate(date: LocalDate) {
        eventsAdapter.events.clear()
        eventsAdapter.events.addAll(events[date].orEmpty())
        eventsAdapter.notifyDataSetChanged()
        txt_selected_date.text = selectionFormatter.format(date)
    }
}

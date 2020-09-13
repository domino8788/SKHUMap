package com.domino.skhumap.fragment

import android.animation.ValueAnimator
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.domino.skhumap.R
import com.domino.skhumap.adapter.EventListAdapter
import com.domino.skhumap.dto.Schedule
import com.domino.skhumap.model.CalendarViewModel
import com.domino.skhumap.model.MainViewModel
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
import kotlinx.android.synthetic.main.dialog_input_event.view.*
import kotlinx.android.synthetic.main.dialog_input_event.view.check_every_week
import kotlinx.android.synthetic.main.dialog_input_event.view.edit_title
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

class CalendarFragment : Fragment() {
    private val Context.inputMethodManager get() = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    private val events = hashMapOf<LocalDate, MutableList<Schedule>>()
    private val weekEvents = hashMapOf<Int, MutableList<Schedule>>()
    private lateinit var calendarViewModel:CalendarViewModel
    private lateinit var mainViewModel:MainViewModel
    private val eventsAdapter = EventListAdapter {
        getInputDialog(it)
    }

    private fun termToString(startHourOfDay:Int, startMinute:Int, endHourOfDay:Int, endMinute: Int):String =
        "${timeToString(startHourOfDay)}:${timeToString(startMinute)} ~ ${timeToString(endHourOfDay)}:${timeToString(endMinute)}"

    private fun timeToString(time:Int) = if(time < 10) "0${time}" else time

    private fun getInputDialog(schedule: Schedule? = null):AlertDialog {
        val targetDate = schedule?.startDate?.toLocalDate()?:selectedDate
        var startHourOfDay:Int?=null
        var startMinute:Int?=null
        var endHourOfDay:Int?=null
        var endMinute:Int?=null
        var endDate:LocalDate?=null
        var yoilList:MutableList<String> = mutableListOf()

        val dialog = layoutInflater.inflate(R.layout.dialog_input_event, null).apply {
            fun disableView(){
                edit_title.isEnabled = false
                check_every_week.isEnabled = false
                btn_show_time_picker.isEnabled = false
                check_monday.isEnabled = false
                check_tuesday.isEnabled = false
                check_wednesday.isEnabled = false
                check_thursday.isEnabled = false
                check_friday.isEnabled = false
                check_saturday.isEnabled = false
                check_sunday.isEnabled = false
            }
            fun yoilCheck(yoil:String){
                when(yoil){
                    "월" -> {
                        check_monday.isChecked = true
                        if(targetDate!!.dayOfWeek.value.toDayOfWeek() == yoil)
                            check_monday.isEnabled = false
                    }
                    "화" -> {
                        check_tuesday.isChecked = true
                        if(targetDate!!.dayOfWeek.value.toDayOfWeek() == yoil)
                            check_tuesday.isEnabled = false
                    }
                    "수" -> {
                        check_wednesday.isChecked = true
                        if(targetDate!!.dayOfWeek.value.toDayOfWeek() == yoil)
                            check_wednesday.isEnabled = false
                    }
                    "목" -> {
                        check_thursday.isChecked = true
                        if(targetDate!!.dayOfWeek.value.toDayOfWeek() == yoil)
                            check_thursday.isEnabled = false
                    }
                    "금" -> {
                        check_friday.isChecked = true
                        if(targetDate!!.dayOfWeek.value.toDayOfWeek() == yoil)
                            check_friday.isEnabled = false
                    }
                    "토" -> {
                        check_saturday.isChecked = true
                        if(targetDate!!.dayOfWeek.value.toDayOfWeek() == yoil)
                            check_saturday.isEnabled = false
                    }
                    "일" -> {
                        check_sunday.isChecked = true
                        if(targetDate!!.dayOfWeek.value.toDayOfWeek() == yoil)
                            check_sunday.isEnabled = false
                    }
                }

            }

            btn_show_time_picker.setOnClickListener {
                context.inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
                TimePickerDialog(requireActivity(), TimePickerDialog.OnTimeSetListener { view, sHourOfDay, sMinute ->
                    TimePickerDialog(requireActivity(), TimePickerDialog.OnTimeSetListener { view, eHourOfDay, eMinute ->
                        if(sHourOfDay < eHourOfDay || ((sHourOfDay == eHourOfDay) && (sMinute<eMinute))){
                            startHourOfDay = sHourOfDay
                            startMinute = sMinute
                            endHourOfDay = eHourOfDay
                            endMinute = eMinute
                            txt_time.text = "시간 : ${termToString(startHourOfDay!!, startMinute!!, endHourOfDay!!, endMinute!!)}"
                        } else {
                            mainViewModel.toastLiveData.postValue("마감 시간은 시작 시간 보다 나중이어야 합니다.")
                        }
                    }, sHourOfDay, sMinute, false).apply {
                        setMessage("시작 시간 : ${timeToString(sHourOfDay)}:${timeToString(sMinute)}\n마감 시간 선택")
                        show()
                    }
                }, 0, 0, false).apply {
                    setMessage("시작 시간 선택")
                    show()
                }
            }
            btn_show_date_picker.setOnClickListener {
                context.inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
                DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    endDate = LocalDate.of(year, month+1, dayOfMonth)
                    txt_date.text = "기간 : $targetDate ~ $endDate"
                }, selectedDate!!.year, targetDate!!.monthValue-1, targetDate.dayOfMonth).apply {
                    datePicker.minDate = targetDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                    setMessage("시작 날짜 : $targetDate\n마감 날짜를 선택하세요.")
                    show()
                }
            }
            check_every_week.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked) {
                    legend_list.visibility = View.VISIBLE
                    txt_date.visibility = View.VISIBLE
                    btn_show_date_picker.visibility = View.VISIBLE
                }
                else{
                    legend_list.visibility = View.GONE
                    txt_date.visibility = View.GONE
                    btn_show_date_picker.visibility = View.GONE
                }
            }
            check_monday.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked)
                    yoilList.add("월")
                else
                    yoilList.remove("월")
            }
            check_tuesday.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked)
                    yoilList.add("화")
                else
                    yoilList.remove("화")
            }
            check_wednesday.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked)
                    yoilList.add("수")
                else
                    yoilList.remove("수")
            }
            check_thursday.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked)
                    yoilList.add("목")
                else
                    yoilList.remove("목")
            }
            check_friday.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked)
                    yoilList.add("금")
                else
                    yoilList.remove("금")
            }
            check_saturday.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked)
                    yoilList.add("토")
                else
                    yoilList.remove("토")
            }
            check_sunday.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked)
                    yoilList.add("일")
                else
                    yoilList.remove("일")
            }

            schedule?.let {
                edit_title.setText(schedule.name)
                edit_event_info.setText(schedule.info)
                schedule.frTm.split(":").let {
                    startHourOfDay = it[0].toInt()
                    startMinute = it[1].toInt()
                }
                schedule.toTm.split(":").let {
                    endHourOfDay = it[0].toInt()
                    endMinute = it[1].toInt()
                }
                txt_time.text="시간 : ${termToString(startHourOfDay!!, startMinute!!, endHourOfDay!!, endMinute!!)}"
                schedule.everyWeek.let { everyWeek->
                    check_every_week.isChecked = everyWeek
                    if(everyWeek) {
                        endDate = schedule.endDate?.toLocalDate()
                        txt_date.text = "${targetDate}~${endDate.toString()}"
                    }
                    schedule.yoil?.forEach {yoil -> yoilCheck(yoil)}
                }
                when(schedule.type){
                    Schedule.TYPE_STUDENT_SCHEDULE, Schedule.TYPE_EDIT_STUDENT_SCHEDULE -> {
                        disableView()
                    }
                    /* 만료된 당일 일정의 수정을 비활성화 */
                    Schedule.TYPE_PERSONAL -> {
                        if(!schedule.everyWeek && schedule.startDate!!.toLocalDate().isBefore(today)) {
                            disableView()
                            btn_show_date_picker.isEnabled = false
                            edit_event_info.isEnabled = false
                        }
                    }
                }
            }?: yoilCheck(targetDate!!.dayOfWeek.value.toDayOfWeek())
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_event_input_title))
            .setView(dialog)
            .apply {
                if(schedule != null && (schedule.type != Schedule.TYPE_STUDENT_SCHEDULE)) {
                    this.setPositiveButton(R.string.delete) { _, listener ->
                        deleteEvent(schedule)
                    }
                }
            }
            .setOnDismissListener {  }
            .setNeutralButton(R.string.save){ dialogInterface: DialogInterface, i: Int ->
            }
            .setNegativeButton(R.string.close, null)
            .create()
            .apply {
                setOnDismissListener {
                    context.inputMethodManager.hideSoftInputFromWindow(dialog.windowToken, 0)
                }
                show()
                getButton(AlertDialog.BUTTON_NEUTRAL).apply {
                    schedule?.let {
                        if(!it.everyWeek && it.startDate!!.toLocalDate().isBefore(today)) {
                            isEnabled = false
                        }
                    } }.setOnClickListener {
                    dialog.run {
                        if (edit_title.text.trim().isBlank())
                            mainViewModel.toastLiveData.postValue("일정 제목을 입력하세요.")
                        else if (startHourOfDay == null || startMinute == null || endHourOfDay == null || endMinute == null)
                            mainViewModel.toastLiveData.postValue("시간을 입력 하세요.")
                        else if (check_every_week.isChecked && yoilList.isEmpty())
                            mainViewModel.toastLiveData.postValue("매주 반복 할 요일을 선택하세요.")
                        else if (check_every_week.isChecked && endDate == null)
                            mainViewModel.toastLiveData.postValue("반복 할 기간을 선택하세요.")
                        else {
                            /* 매주반복 일정에서 당일 일정으로 변경 시 기존 요일 정보를 비우고 당일 정보만 넣는다. */
                            if(!check_every_week.isChecked) {
                                yoilList.clear()
                                yoilList.add(targetDate!!.dayOfWeek.value.toDayOfWeek())
                            }
                            /* 수정 시 기존 일정을 삭제 */
                            schedule?.let {
                                deleteEvent(it, false)
                            }
                            /* 새 일정을 저장 */
                            saveEvent(Schedule(schedule?.let {
                                        if (schedule.type == Schedule.TYPE_STUDENT_SCHEDULE)
                                            Schedule.TYPE_EDIT_STUDENT_SCHEDULE
                                        else
                                            schedule.type
                                    } ?: Schedule.TYPE_PERSONAL,
                                    edit_title.text.toString().trim(),
                                    edit_event_info.text.toString().trim(),
                                    yoilList,
                                /* 매주 반복 일정에서 당일 일정으로 변경했을때 현재 선택한 날짜를 startDate로 지정 */
                                        if((schedule?.everyWeek == true) && !check_every_week.isChecked)
                                            selectedDate!!.toTimestamp()
                                        else targetDate!!.toTimestamp() ,
                                    endDate?.let { it.toTimestamp() } ?: null,
                                    check_every_week.isChecked,
                                    "${if (startHourOfDay!! < 10) "0${startHourOfDay}" else startHourOfDay}:00",
                                    "${if (endHourOfDay!! < 10) "0${endHourOfDay}" else endHourOfDay}:00",
                                    "${timeToString(startHourOfDay!!)}:${timeToString(startMinute!!)}",
                                    "${timeToString(endHourOfDay!!)}:${timeToString(endMinute!!)}"
                                ).apply { schedule?.let { this.id = it.id } })
                            this@apply.dismiss()
                        }
                    }
                }
            }
    }

    private var selectedDate: LocalDate? = null
    private val today by lazy { LocalDate.now() }
    private val selectionFormatter = DateTimeFormatter.ofPattern("yyyy년 MMMM d일")
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    private var monthToWeek:Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        calendarViewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java].apply {
            eventsLiveData.observe(requireActivity(), Observer { eventPair ->
                events.putAll(eventPair.first)
                weekEvents.putAll(eventPair.second)
                calendar_view.notifyCalendarChanged()
                updateAdapterForDate(today)
            })
            mainViewModel.requestHttp(this.loadSchedule())
        }
        list_event.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        list_event.adapter = eventsAdapter
        list_event.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(1)
        val endMonth = currentMonth.plusMonths(10)

        /* week mode로 초기화 */
        calendar_view.run {
            setup(startMonth, endMonth, daysOfWeek.first())
            inDateStyle = InDateStyle.FIRST_MONTH
            maxRowCount = 1
            hasBoundaries = false
            scrollToDate(today)
        }

        if (savedInstanceState == null) {
            calendar_view.post {
                // Show today's events initially.
                selectDate(today)
            }
        }
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
                            dotView.isVisible = (events[day.date].orEmpty().isNotEmpty() ||
                                    (weekEvents[day.date.dayOfWeek.value].orEmpty().isNotEmpty()
                                            && (weekEvents[day.date.dayOfWeek.value]?.any {
                                            schedule ->
                                        (day.date.isAfter(schedule.startDate!!.toLocalDate()) || day.date.isEqual(schedule.startDate!!.toLocalDate()))
                                                && (day.date.isBefore(schedule.endDate!!.toLocalDate()) || day.date.isEqual(schedule.endDate!!.toLocalDate()))
                                        //schedule.startDate!!.toLocalDate().toString() <= day.date.toString() && schedule.endDate!!.toLocalDate().toString() >= day.date.toString()
                                    }!!)))
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
                    if (firstDate.yearMonth == lastDate.yearMonth) {
                        calendar_view.scrollToMonth(firstDate.yearMonth)
                        selectDate(today)
                    }
                    else {
                        calendar_view.scrollToMonth(minOf(firstDate.yearMonth.next, endMonth))
                        selectDate(lastDate.minusDays(lastDate.dayOfMonth-1L))
                    }
                }
            }
            animator.duration = 250
            animator.start()
        }
        /* 이벤트 추가 버튼 리스너 */
        btn_add_event.setOnClickListener {
            if(today.isBefore(selectedDate) || today.isEqual(selectedDate))
                getInputDialog()
        }
    }
    /* 이벤트 저장 */
    private fun saveEvent(event: Schedule) {
        val targetDate = event.startDate!!.toLocalDate()
        /* 매주 반복되는 일정일 때 */
        if (event.everyWeek) {
            event.yoil!!.forEach { yoil -> weekEvents[yoil.yoilToNumber()]!!.add(event) }
            calendar_view.notifyCalendarChanged()
        }
        /* 당일 일정일 때 */
        else {
            events[targetDate]?.add(event) ?: let { _ ->
                events[targetDate] = mutableListOf(event)
            }
        }
        calendarViewModel.insertSchedule(event)
        updateAdapterForDate(selectedDate!!)
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
    private fun deleteEvent(event: Schedule, needUpdate:Boolean = true) {
        val targetDate = event.startDate!!.toLocalDate()
        /* 매주 반복 일정일 때 */
        if(event.everyWeek){
            event.yoil?.forEach { yoil -> weekEvents[yoil.yoilToNumber()]!!.remove(event) }
            calendar_view.notifyCalendarChanged()
        }
        else {
            events[targetDate]?.remove(event)
        }
        if(needUpdate) {
            if(event.type == Schedule.TYPE_EDIT_STUDENT_SCHEDULE) {
                event.yoil?.forEach { yoil ->
                    weekEvents[yoil.yoilToNumber()]!!.add(calendarViewModel.lectureList.find { lecture -> (event.name == lecture.gwamogKorNm) && (event.adjustFrTm == lecture.adjustFrTm) && (yoil == lecture.yoilNm) }!!.toSchedule)
                }
            }
            calendarViewModel.deleteSchedule(event)
            updateAdapterForDate(selectedDate!!)
        }
    }
    /* 어댑터 업데이트 */
    private fun updateAdapterForDate(date: LocalDate) {
        eventsAdapter.events.clear()
        eventsAdapter.events.addAll(events[date].orEmpty().sortedBy { schedule -> schedule.frTm })
        eventsAdapter.events.addAll(weekEvents[date.dayOfWeek.value].orEmpty().filter { schedule ->
            (date.isAfter(schedule.startDate!!.toLocalDate()) || date.isEqual(schedule.startDate!!.toLocalDate()))
                    && (date.isBefore(schedule.endDate!!.toLocalDate()) || date.isEqual(schedule.endDate!!.toLocalDate()))
            //schedule.startDate!!.toLocalDate().toString() <= day.date.toString() && schedule.endDate!!.toLocalDate().toString() >= day.date.toString()
        })
        eventsAdapter.events.sortBy { schedule -> schedule.frTm }
        eventsAdapter.notifyDataSetChanged()
        txt_selected_date.text = selectionFormatter.format(date)
    }
}

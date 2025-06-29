package com.domino.skhumap.model

import android.app.Application
import android.content.Context
import android.webkit.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.domino.skhumap.dto.Schedule
import com.domino.skhumap.repository.FirestoreHelper
import com.domino.skhumap.repository.RetrofitHelper
import com.domino.skhumap.repository.RetrofitService
import com.domino.skhumap.utils.toLocalDate
import com.domino.skhumap.utils.yoilToNumber
import com.domino.skhumap.vo.Haggi
import com.domino.skhumap.vo.Lecture
import com.domino.skhumap.vo.ScheduleResponse
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.threeten.bp.LocalDate
import java.net.URLEncoder
import kotlin.Exception

class CalendarViewModel(val app: Application) : AndroidViewModel(app) {
    var lectureList: MutableList<Lecture> = mutableListOf()
    private var scheduleList: MutableList<Schedule> = mutableListOf()
    private val today = LocalDate.now()

    var eventMap: HashMap<LocalDate, MutableList<Schedule>> = hashMapOf()
    var weekEventMap: HashMap<Int, MutableList<Schedule>> = hashMapOf(
        1 to mutableListOf(),
        2 to mutableListOf(),
        3 to mutableListOf(),
        4 to mutableListOf(),
        5 to mutableListOf(),
        6 to mutableListOf(),
        7 to mutableListOf()
    )

    val eventsLiveData: MutableLiveData<Pair<HashMap<LocalDate, MutableList<Schedule>>, HashMap<Int, MutableList<Schedule>>>> by lazy { MutableLiveData<Pair<HashMap<LocalDate, MutableList<Schedule>>, HashMap<Int, MutableList<Schedule>>>>() }

    val toastLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    var currentHaggi:Haggi? = null

    fun loadSchedule():WebViewClient =app.getSharedPreferences("login_info", Context.MODE_PRIVATE).run {
            return WebClient(getString("id", "")!!, getString("password", "")!!, Action.GET_STUDENT_SCHEDULE)
        }

    private suspend fun setSchedule(list:MutableList<Lecture>) {
        lectureList = list
        list.forEach { lecture ->
            weekEventMap[lecture.yoilNm.yoilToNumber()]!!.add(lecture.toSchedule)
        }
        queryScheduleList()
    }

    private fun isExpired(schedule: Schedule):Boolean =
        if(schedule.everyWeek) {
            schedule.endDate!!.toLocalDate().plusMonths(1).isBefore(today)
        } else {
            schedule.startDate!!.toLocalDate().plusMonths(1).isBefore(today)
        }

    private suspend fun queryScheduleList() {
        GlobalScope.launch {
            scheduleList = FirestoreHelper.calendarReference.orderBy("startDate").get().await().toObjects(Schedule::class.java)
            scheduleList.forEach {schedule ->
                /* 만료된 일정일 때 */
                if(isExpired(schedule)){
                    deleteSchedule(schedule)
                }else{
                    /* 개인 일정일 때 */
                    if(schedule.type == Schedule.TYPE_PERSONAL) {
                        /* 매주 진행하는 일정일 때 */
                        if(schedule.everyWeek) {
                            schedule.yoil!!.forEach { yoil -> weekEventMap[yoil.yoilToNumber()]!!.add(schedule) }
                        }
                        /* 당일만 진행하는 일정일 때때*/
                        else {
                            eventMap[schedule.startDate!!.toLocalDate()]?.add(schedule)?:let { eventMap[schedule.startDate!!.toLocalDate()] = mutableListOf(schedule) }
                        }
                    }
                    /* 시간표 수정일 때 */
                    else {
                        schedule.yoil!!.forEach { yoil ->
                            weekEventMap[yoil.yoilToNumber()]!!.run {
                                set(indexOfFirst { beforeSchedule: Schedule -> beforeSchedule.name == schedule.name && beforeSchedule.adjustFrTm == beforeSchedule.adjustFrTm} , schedule)
                            }
                        }
                    }
                }
            }
            eventsLiveData.postValue(eventMap to weekEventMap)
        }
    }

    fun insertSchedule(schedule: Schedule) {
        GlobalScope.launch {
            if(schedule.id == "")
                schedule.id = FirestoreHelper.calendarReference.add(schedule).await().id
            else
                FirestoreHelper.calendarReference.document(schedule.id).set(schedule)
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        FirestoreHelper.calendarReference.document(schedule.id).delete()
    }

    enum class Status {
        FINISH, GET_COOKIE, SELECT_ACTION, SCHEDULE_SEQUENCE
    }

    enum class Action {
        GET_STUDENT_SCHEDULE
    }

    private inner class WebClient(private val id: String, private val password:String, private val action: Action) : WebViewClient() {
        private val cookieManager: CookieManager = CookieManager.getInstance()
        private var status = Status.GET_COOKIE

        init {
            cookieManager.setAcceptCookie(true)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
            toastLiveData.postValue( "서버의 상태가 불안정 합니다. 잠시 후 다시 시도해 주세요.")
            status = Status.FINISH
        }

        override fun onPageFinished(view: WebView, url: String) {
            when (status) {
                Status.GET_COOKIE -> {
                    val url = "http://cas.skhu.ac.kr/SSO/AuthenticateLogin"
                    val post =
                        "ID=" + URLEncoder.encode(id, "UTF-8"
                        ) + "&PW=" + URLEncoder.encode(password, "UTF-8")
                    view.postUrl(url, post.toByteArray())
                    status = Status.SELECT_ACTION
                }
                Status.SELECT_ACTION -> {
                    when (action) {
                        Action.GET_STUDENT_SCHEDULE -> {
                            status = Status.SCHEDULE_SEQUENCE
                            view.loadUrl("http://sam.skhu.ac.kr")
                        }
                    }
                }
                Status.SCHEDULE_SEQUENCE -> {
                    val networkService = RetrofitHelper.studentScheduleRetrofit.create(RetrofitService::class.java)
                    view.evaluateJavascript("document.getElementsByTagName(\"body\")[0].attributes[\"ncg-request-verification-token\"].value") {
                        var result:ScheduleResponse? = null
                        GlobalScope.launch {
                            var retry = false
                            do{
                                try {
                                    currentHaggi = networkService.getYyHaggi(
                                        it.replace("\"", ""),
                                        cookieManager.getCookie(url)
                                    ).execute().body()!!.haggi
                                    result = networkService.getStudentScheduleList(
                                        Haggi(currentHaggi!!.yy, currentHaggi!!.haggi, currentHaggi!!.haggiNm),
                                        it.replace("\"", ""),
                                        cookieManager.getCookie(url)
                                    ).execute().body()!!
                                    when(result!!.sts) {
                                        /*
                                        0 : 정상 sts
                                        409 : 너무 빠르게 재조회 했을 때 오는 sts
                                        */
                                        0, 409 -> { retry = false }
                                    }
                                }catch (e:Exception) {
                                    when(e){
                                        is JsonSyntaxException -> {
                                            retry = !retry
                                            if(!retry)
                                                toastLiveData.postValue("토큰이 유효하지 않습니다.")
                                        }
                                        else -> {
                                            toastLiveData.postValue("인터넷을 연결해주세요.")
                                        }
                                    }
                                }finally {
                                    result?.schedules?.let { setSchedule(it.toMutableList()) }
                                }
                            }while(retry)
                        }
                    }
                    status = Status.FINISH
                }
                Status.FINISH -> {
                    // 완료 상태 - 아무것도 하지 않음
                }
            }
        }
    }
}
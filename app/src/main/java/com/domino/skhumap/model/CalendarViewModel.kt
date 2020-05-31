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
import com.domino.skhumap.vo.Haggi
import com.domino.skhumap.vo.Lecture
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InvalidObjectException
import java.net.URLEncoder
import kotlin.Exception

class CalendarViewModel(val app: Application) : AndroidViewModel(app) {
    lateinit var lectureList: MutableList<Lecture>
    lateinit var scheduleList: MutableList<Schedule>
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

    fun loadStudentSchedule():WebViewClient =app.getSharedPreferences("login_info", Context.MODE_PRIVATE).run {
            return WebClient(getString("id", "")!!, getString("password", "")!!, Action.GET_STUDENT_SCHEDULE)
        }

    fun setLectureList(list:MutableList<Lecture>) {
        lectureList = list
        studentScheduleLiveData.postValue(lectureList)
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
                        GlobalScope.launch {
                            try {
                                currentHaggi = networkService.getYyHaggi(
                                    it.replace("\"", ""),
                                    cookieManager.getCookie(url)
                                ).execute().body()!!.haggi
                                val studentSchedule = networkService.getStudentScheduleList(
                                    Haggi(currentHaggi!!.yy, currentHaggi!!.haggi, currentHaggi!!.haggiNm),
                                    it.replace("\"", ""),
                                    cookieManager.getCookie(url)
                                ).execute().body()
                                studentSchedule?.run {
                                    if(msg != null) {
                                        setLectureList(lectureList)
                                    } else {
                                        throw InvalidObjectException("토큰이 유효하지 않습니다.")
                                    }
                                }
                            }catch (e:Exception) {
                                when(e){
                                    is InvalidObjectException -> { toastLiveData.postValue(e.message) }
                                    else -> { toastLiveData.postValue("인터넷을 연결해주세요.") }
                                }
                            }
                        }
                    }
                    status = Status.FINISH
                }
            }
        }
    }
}
package com.domino.skhumap.model

import android.app.Application
import android.content.Context
import android.webkit.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.domino.skhumap.Facility
import com.domino.skhumap.repository.FirestoreHelper
import com.domino.skhumap.dto.SearchableFacility
import com.domino.skhumap.repository.RetrofitHelper
import com.domino.skhumap.repository.RetrofitService
import com.domino.skhumap.vo.ScheduleResponse
import com.domino.skhumap.vo.Haggi
import com.domino.skhumap.vo.Lecture
import com.domino.skhumap.vo.TargetFacility
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.URLEncoder
import kotlin.math.abs

class MapViewModel(val app: Application) : AndroidViewModel(app) {
    private var facilities:MutableList<Facility> = mutableListOf()
    val facilityLiveData:MutableLiveData<Facility> by lazy { MutableLiveData<Facility>() }
    val previousFacilitiesLiveData:MutableLiveData<MutableList<Facility>> by lazy { MutableLiveData<MutableList<Facility>>() }
    val floorListLiveData: MutableLiveData<MutableList<Pair<String, Int>>> by lazy { MutableLiveData<MutableList<Pair<String, Int>>>() }
    val selectedFloorLiveData:MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val markMapLivdeData:MutableLiveData<SearchableFacility> by lazy { MutableLiveData<SearchableFacility>() }
    val selectedFacilityInfoLiveData:MutableLiveData<List<Lecture>> by lazy { MutableLiveData<List<Lecture>>() }
    private var mapListener: ListenerRegistration? = null
    val selectedFacilityLiveData: MutableLiveData<SearchableFacility> by lazy { MutableLiveData<SearchableFacility>() }

    var selectedDepartment:Facility? = null
    set(facility) {
        field = facility
        facility?.let {
            floorListLiveData.value = getFloorList((it.info!!["minFloor"] as Long).toInt(), (it.info!!["maxFloor"] as Long).toInt())
            setSelectedFloor(1)
        }?: setSelectedFloor(null)
    }


    fun setSelectedFloor(floor:Int?) {
        selectedFloorLiveData.value = floor
        floor?.let{
            query(FirestoreHelper.departmentReference(selectedDepartment!!.id, selectedFloorLiveData!!.value!!))
        }?: query(FirestoreHelper.campusReference)
    }

    private fun query(target: CollectionReference) {
        mapListener?.remove()
        previousFacilitiesLiveData.value = facilities
        facilities = mutableListOf()
        mapListener = target.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            try {
                for (change in querySnapshot?.documentChanges!!) {
                    change.run {
                        when (type) {
                            DocumentChange.Type.ADDED -> {
                                facilities.add(newIndex, document.toObject(Facility::class.java).apply { facilityLiveData.value = this })
                            }
                            DocumentChange.Type.MODIFIED -> {
                                facilities[oldIndex].marker?.map = null
                                facilities[newIndex] = document.toObject(Facility::class.java)
                            }
                            DocumentChange.Type.REMOVED -> {
                                facilities[oldIndex].removeMarker()
                                facilities.removeAt(oldIndex)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                mapListener?.remove()
            }
        }
    }

    private fun getFloorList(minFloor: Int, maxFloor: Int): MutableList<Pair<String, Int>> =
        mutableListOf<Pair<String, Int>>().apply {
            (minFloor..maxFloor).forEach { if(it!=0) add(getFloorName(it).toUpperCase() to it) }
        }

    fun getSelectedDepartmentId() = selectedDepartment!!.id
    fun getFloorName(floorNumber: Int): String = if (floorNumber > 0) "f${floorNumber}" else "b${abs(floorNumber)}"
    fun getSelectedFloorNumber() = selectedFloorLiveData.value
    fun pickerValueToFloorNumber(pickerValue:Int) = floorListLiveData.value!![pickerValue]!!.second
    fun getCurrentSelectToSearchableFacility(facility: Facility?) = SearchableFacility(selectedDepartment!!, selectedFloorLiveData.value!!, facility!!)

    val toastLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    enum class Status {
        FINISH, GET_COOKIE, SELECT_ACTION, FACILITY_SCHEDULE_SEQUENCE
    }

    enum class Action {
        GET_FACILITY_SCHEDULE
    }

    fun getSelectedFacilityInfo(facilityId:String):WebViewClient {
        app.getSharedPreferences("login_info", Context.MODE_PRIVATE).run {
            return WebClient(
                getString("id", "")!!,
                getString("password", "")!!,
                Action.GET_FACILITY_SCHEDULE,
                facilityId
            )
        }
    }

    private inner class WebClient(private val id: String, private val password:String, private val action: Action, private val facilityId:String) : WebViewClient() {
        private val cookieManager: CookieManager = CookieManager.getInstance()
        private var status = Status.GET_COOKIE
        var currentHaggi: Haggi? = null

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
                        Action.GET_FACILITY_SCHEDULE -> {
                            status = Status.FACILITY_SCHEDULE_SEQUENCE
                            view.loadUrl("http://sam.skhu.ac.kr")
                        }
                    }
                }
                Status.FACILITY_SCHEDULE_SEQUENCE -> {
                    val networkService = RetrofitHelper.studentScheduleRetrofit.create(
                        RetrofitService::class.java)
                    view.evaluateJavascript("document.getElementsByTagName(\"body\")[0].attributes[\"ncg-request-verification-token\"].value") {
                        var result: ScheduleResponse? = null
                        GlobalScope.launch {
                            var retry = false
                            do{
                                try {
                                    currentHaggi = networkService.getYyHaggi(
                                        it.replace("\"", ""),
                                        cookieManager.getCookie(url)
                                    ).execute().body()!!.haggi
                                    result = networkService.getFacilityScheduleList(
                                        TargetFacility(currentHaggi!!.yy, currentHaggi!!.haggi, currentHaggi!!.haggiNm, facilityId),
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
                                    result?.schedules?.let { selectedFacilityInfoLiveData.postValue(it) }
                                }
                            }while(retry)
                        }
                    }
                    status = Status.FINISH
                }
            }
        }
    }
}
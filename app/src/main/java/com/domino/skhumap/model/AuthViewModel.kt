package com.domino.skhumap.model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.webkit.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.domino.skhumap.db.FirestoreHelper
import com.domino.skhumap.vo.DateVO
import com.domino.skhumap.vo.StudentScheduleVO
import com.google.firebase.auth.*
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.net.URLEncoder

class AuthViewModel(val app: Application) : AndroidViewModel(app) {
    val idLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val nameLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val passwordLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val toastLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    /* 이름, 비밀번호 */
    val callResetPassword: MutableLiveData<Pair<String, String>> by lazy { MutableLiveData<Pair<String, String>>() }

    private val auth = FirebaseAuth.getInstance().apply {
        addAuthStateListener {state ->
            user = state.currentUser
        }
    }
    private var user:FirebaseUser? = auth.currentUser

    fun getEmail(id:String, name:String) = "${id}@${name}.com"

    fun login(id:String, password: String):WebViewClient?{
        val connectivityManager = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isNetworkConnected:Boolean = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isNetworkConnected = connectivityManager.activeNetwork?.let { false } ?:let { true }
        } else {
            isNetworkConnected = connectivityManager.activeNetworkInfo?.let { false } ?:let { true }
        }

        if (id.isEmpty() || password.isEmpty()) {
            toastLiveData.postValue("올바른 입력 값이 아닙니다!")
            return null
        }
        else if(isNetworkConnected){
            toastLiveData.postValue("인터넷을 연결해주세요.")
            return null
        }

        return WebClient(id, password, Action.LOGIN)
    }

    fun queryStudentSchedule(): WebViewClient {
        app.getSharedPreferences("login_info", Context.MODE_PRIVATE).run {
            return WebClient(getString("id", "")!!, getString("password", "")!!, Action.STUDENT_SCHEDULE)
        }
    }

    val isLogin:Boolean
    get() = user != null

    fun logout() {
        CookieManager.getInstance().removeAllCookie()
        app.getSharedPreferences("login_info", Context.MODE_PRIVATE)!!.edit().clear().commit()
        auth.signOut()
    }

    fun loadLoginInfo() {
        app.getSharedPreferences("login_info", Context.MODE_PRIVATE).run {
            idLiveData.postValue(getString("id", ""))
            passwordLiveData.postValue(getString("password", ""))
            nameLiveData.postValue(getString("name",""))
        }
    }

    private fun setLoginInfo(id:String, password:String, name:String) {
        app.getSharedPreferences("login_info", Context.MODE_PRIVATE).edit().run {
            putString("id", id)
            putString("password", password)
            putString("name", name)
            commit()
        }
    }

    private fun loginSuccess(id: String, password: String, name: String){
        setLoginInfo(id, password, name)
        FirestoreHelper.userReference = FirestoreHelper.db.document("users/${id}")
        toastLiveData.postValue("로그인 성공. $id $name 으로 로그인 되셨습니다.")
        loadLoginInfo()
    }

    fun resetPassword(id:String, name:String, previousPassword:String, newPassword:String){
        /* 입력한 password로 로그인 시도 */
        auth.signInWithEmailAndPassword(getEmail(id, name), previousPassword).addOnCompleteListener { task ->
            /* 로그인 성공 */
            if(task.isSuccessful){
                /* 비밀번호 갱신 */
                user?.updatePassword(newPassword)!!.addOnCompleteListener { task ->
                    /* 갱신 성공 */
                    if(task.isSuccessful) {
                        loginSuccess(id, newPassword, name)
                    }
                    /* 갱신 실패 */
                    else {
                        callResetPassword.postValue(name to newPassword)
                        toastLiveData.postValue("인증에 실패했습니다. 관리자에게 문의하세요.")
                    }
                }
            }
            /* 로그인 실패 */
            else {
                /* 인증 재시도 */
                callResetPassword.postValue(name to newPassword)
                toastLiveData.postValue("로그인 실패. 비밀번호를 다시 한번 확인하세요.")
            }
        }
    }

    private fun firebaseAuth(id: String, password: String, name: String) {
        auth.signInWithEmailAndPassword(getEmail(id, name), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginSuccess(id, password, name)
                } else {
                    when(task.exception){
                        /* 비밀번호 변경 감지 */
                        is FirebaseAuthInvalidCredentialsException -> {
                            toastLiveData.postValue("비밀번호 변경이 감지 되었습니다. 갱신을 위해 이전 비밀번호를 입력해주세요.")
                            callResetPassword.postValue(name to password)
                        }
                        /* 신규가입 */
                        is FirebaseAuthInvalidUserException -> {
                            auth.createUserWithEmailAndPassword("${id}@${name}.com", password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        user?.updateProfile(
                                            UserProfileChangeRequest.Builder().setDisplayName(name).build()
                                        )
                                        loginSuccess(id, password, name)
                                    } else {
                                        toastLiveData.postValue("인증에 실패했습니다. 관리자에게 문의하세요.")
                                    }
                                }
                        }
                    }
                }
            }
    }



    private enum class Status {
        FINISH, GET_COOKIE, SELECT_ACTION, LOGIN_SEQUENCE, GET_STUDENT_SCHEDULE_SEQUENCE
    }

    private enum class Action {
        LOGIN, STUDENT_SCHEDULE
    }

    private inner class WebClient(private val id: String, private val password:String, private val action:Action) : WebViewClient() {
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
                        "ID=" + URLEncoder.encode(
                            id, "UTF-8"
                        ) + "&PW=" + URLEncoder.encode(password, "UTF-8")
                    view.postUrl(url, post.toByteArray())
                    status = Status.SELECT_ACTION
                }
                Status.SELECT_ACTION -> {
                    when (action) {
                        Action.LOGIN -> {
                            status = Status.LOGIN_SEQUENCE
                            view.loadUrl("http://sam.skhu.ac.kr")
                        }
                        Action.STUDENT_SCHEDULE -> {
                            status = Status.GET_STUDENT_SCHEDULE_SEQUENCE
                            view.loadUrl("http://sam.skhu.ac.kr/SSE/SSEAD/SSEAD03")
                        }
                    }
                }
                Status.LOGIN_SEQUENCE -> {
                    view.evaluateJavascript("document.getElementsByClassName('btn btn-sm')[0].innerText") {
                        if (it == "null") {
                            cookieManager.removeAllCookie()
                            toastLiveData.postValue("로그인 실패. 아이디와 비밀번호를 다시 한번 확인하세요.")
                        } else {
                            val name = it.trim().split(" ", "(", ")")[4]
                            firebaseAuth(id, password, name)
                        }
                    }
                    status = Status.FINISH
                }
                Status.GET_STUDENT_SCHEDULE_SEQUENCE -> {
                    val networkService =
                        RetrofitHelper.getSchedule().create(RetrofitService::class.java)
                    view.evaluateJavascript("document.getElementsByTagName(\"body\")[0].attributes[\"ncg-request-verification-token\"].value") {
                        val call = networkService.getStudentSchedule(
                            it.replace("\"",""), DateVO("2020", "Z0101",  "1학기"),
                            cookieManager.getCookie(url)
                        )
                        call.enqueue(object : Callback<StudentScheduleVO> {
                            override fun onFailure(
                                call: Call<StudentScheduleVO>,
                                t: Throwable
                            ) {}

                            override fun onResponse(
                                call: Call<StudentScheduleVO>,
                                response: Response<StudentScheduleVO>
                            ) {
                                response.body()
                            }

                        })
                    }
                    status = Status.FINISH
                }
            }
        }
    }
}

private class RetrofitHelper {
    companion object {
        fun getSchedule(): Retrofit {
            val gson = GsonBuilder().setLenient().create()
            val httpClient = OkHttpClient.Builder().apply {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                interceptors().add(interceptor)
            }.build()

            return Retrofit.Builder()
                .baseUrl("http://sam.skhu.ac.kr/SSE/SSEAD/")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
    }
}

interface RetrofitService {
    @Headers(
        "content-type: application/json;charset=UTF-8",
        "X-Requested-With: XMLHttpRequest"
    )
    @POST("SSEAD03_GetList")
    fun getStudentSchedule(@Header("RequestVerificationToken") token:String, @Body date:DateVO, @Header("Cookie")cookie:String): Call<StudentScheduleVO>
}
package com.domino.skhumap.model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.webkit.*
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.net.URLEncoder

class AuthViewModel(val app: Application) : AndroidViewModel(app) {
    val idLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val nameLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val passwordLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val toastLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val auth = FirebaseAuth.getInstance()
    private var user:FirebaseUser? = auth.currentUser
    private val idSuffix = "@skhu.ac.kr"

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

        this.idLiveData.postValue(id)
        this.passwordLiveData.postValue(password)

        return WebClient(id, password, Action.LOGIN)
    }

    val isLogin:Boolean
    get() = user != null

    fun logout() {
        CookieManager.getInstance().removeAllCookie()
        app.getSharedPreferences("login_info", Context.MODE_PRIVATE)!!.edit().clear().commit()
        auth.signOut()
        loadLoginInfo()
    }

    fun loadLoginInfo() {
        user = auth.currentUser
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
        toastLiveData.postValue("로그인 성공. $id $name 으로 로그인 되셨습니다.")
        loadLoginInfo()
    }

    private fun firebaseAuth(id: String, password: String, name: String) {
        user ?: let {
            auth.signInWithEmailAndPassword("${id}${idSuffix}", password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        loginSuccess(id, password, name)
                    } else {
                        auth.createUserWithEmailAndPassword("${id}${idSuffix}", password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    loginSuccess(id, password, name)
                                } else {
                                    toastLiveData.postValue("인증에 실패했습니다. 관리자에게 문의하세요.")
                                }
                            }
                    }
                }
        }
    }

    private enum class Status {
        FINISH, GET_COOKIE, SELECT_ACTION, LOGIN_SEQUENCE
    }

    private enum class Action {
        LOGIN
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
                    Handler().postDelayed({
                        val url = "http://cas.skhu.ac.kr/SSO/AuthenticateLogin"
                        val post =
                            "ID=" + URLEncoder.encode(id, "UTF-8"
                        ) + "&PW=" + URLEncoder.encode(password, "UTF-8")
                        view.postUrl(url, post.toByteArray())
                    }, 1000)
                    status = Status.SELECT_ACTION
                }
                Status.SELECT_ACTION -> {
                    when (action) {
                        Action.LOGIN -> {
                            status = Status.LOGIN_SEQUENCE
                            Handler().postDelayed({ view.loadUrl("http://sam.skhu.ac.kr") }, 1000)
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
            }
        }
    }
}
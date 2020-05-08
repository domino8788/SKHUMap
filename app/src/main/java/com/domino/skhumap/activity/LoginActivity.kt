package com.domino.skhumap.activity

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.domino.skhumap.R
import kotlinx.android.synthetic.main.activity_login.*
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.Toast
import com.domino.skhumap.contract.Code
import java.net.URLEncoder

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(login_tool_bar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }
        login_btn.setOnClickListener {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var isNetworkConnected:Boolean = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isNetworkConnected = connectivityManager.activeNetwork?.let { false } ?:let { true }
            } else {
                isNetworkConnected = connectivityManager.activeNetworkInfo?.let { false } ?:let { true }
            }

            if (login_id.text!!.isEmpty() || login_password.text!!.isEmpty()) {
                Toast.makeText(this, "올바른 입력 값이 아닙니다!", Toast.LENGTH_SHORT).show()
            }
            else if(isNetworkConnected){
                Toast.makeText(this, "인터넷을 연결해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                login_btn.isEnabled = false
                login_progress_bar.visibility = View.VISIBLE

                login_web_view.run {
                    settings.run {
                        javaScriptEnabled = true
                        setAppCacheEnabled(true)
                        domStorageEnabled = true
                    }
                    webViewClient = WebClient()
                    loadUrl("http://sam.skhu.ac.kr")
                }
            }
        }
    }

    private inner class WebClient : WebViewClient() {
        val cookieManager: CookieManager = CookieManager.getInstance()

        init {
            cookieManager.setAcceptCookie(true)
        }

        var status = 0

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
            Toast.makeText(this@LoginActivity, "서버의 상태가 불안정 합니다. 잠시 후 다시 시도해 주세요.",Toast.LENGTH_SHORT)
        }

        override fun onPageFinished(view: WebView, url: String) {
            when (status) {
                0 -> {
                    Handler().postDelayed({
                        val url = "http://cas.skhu.ac.kr/SSO/AuthenticateLogin"
                        val post = "ID=" + URLEncoder.encode(
                            login_id.text.toString(), "UTF-8"
                        ) + "&PW=" + URLEncoder.encode(login_password.text.toString(), "UTF-8")
                        view.postUrl(url, post.toByteArray())
                    }, 1000)
                    status = 1
                }
                1 -> {
                    Handler().postDelayed({ view.loadUrl("http://sam.skhu.ac.kr") }, 1000)
                    status = 2
                }
                2 -> {
                    view!!.evaluateJavascript("document.getElementsByClassName('btn btn-sm')[0].innerText") {
                        login_btn.isEnabled = true
                        login_progress_bar.visibility = View.GONE
                        Log.d("WebView", it)
                        if (it == "null") {
                            Toast.makeText(
                                applicationContext,
                                "로그인 실패. 아이디와 비밀번호를 다시 한번 확인하세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val result = it.trim().split(" ", "(", ")")
                            val studentNumber = result[2]
                            val name = result[4]
                            Toast.makeText(
                                applicationContext,
                                "로그인 성공. $studentNumber $name 으로 로그인 되셨습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            getSharedPreferences("login_info", Context.MODE_PRIVATE).edit().run {
                                putString("id", login_id.text.toString())
                                putString("password", login_password.text.toString())
                                putString("name", name)
                                commit()
                            }
                            setResult(Code.RESULT_REQUEST_MY_PAGE_RENEWAL)
                            finish()
                        }
                        status = 0
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
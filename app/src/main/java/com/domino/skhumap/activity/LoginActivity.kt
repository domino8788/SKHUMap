package com.domino.skhumap.activity

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
import androidx.annotation.RequiresApi
import java.net.URLEncoder

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(login_tool_bar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }
        login_btn.setOnClickListener {
            login_btn.isEnabled = false
            login_progress_bar.visibility = View.VISIBLE
            login_web_view.run {
                settings.run {
                    javaScriptEnabled = true
                    setAppCacheEnabled(true)
                    domStorageEnabled = true
                }
                webViewClient = WebClient()
                loadUrl( "http://sam.skhu.ac.kr")
            }
        }
    }

    private inner class WebClient : WebViewClient() {
        val cookieManager = CookieManager.getInstance()

        init {
            cookieManager.setAcceptCookie(true)
            cookieManager.removeAllCookie()
        }

        var status = 0

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun onPageFinished(view: WebView, url: String) {
            if(status==0) {
                Handler().postDelayed({
                    val url = "http://cas.skhu.ac.kr/SSO/AuthenticateLogin"
                    val post = "ID=" + URLEncoder.encode(
                        login_id.text.toString(), "UTF-8"
                    ) + "&PW=" + URLEncoder.encode(login_password.text.toString(), "UTF-8")
                    view.postUrl(url, post.toByteArray())
                }, 1000)
                status=1
                return
            } else if(status==1) {
                Handler().postDelayed({ view.loadUrl("http://sam.skhu.ac.kr") }, 1000)
                status = 2
                return
            } else if(status==2) {
                view!!.evaluateJavascript("document.getElementsByClassName('btn btn-sm')[0].innerText") {
                    login_btn.isEnabled = true
                    login_progress_bar.visibility = View.GONE
                    Log.d("WebView", it)
                    if(it == "null"){
                        Toast.makeText(applicationContext, "로그인 실패. 아이디와 비밀번호를 다시 한번 확인하세요.", Toast.LENGTH_SHORT).show()
                    } else {
                        val result = it.trim().split(" ", "(",")")
                        val studentNumber = result[2]
                        val name = result[4]
                        Toast.makeText(applicationContext, "로그인 성공. $studentNumber $name 으로 로그인 되셨습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                status = 0
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
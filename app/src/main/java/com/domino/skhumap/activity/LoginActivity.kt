package com.domino.skhumap.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.domino.skhumap.R
import kotlinx.android.synthetic.main.activity_login.*
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.domino.skhumap.model.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java].apply {
            idLiveData.observe(this@LoginActivity, Observer { id ->
                login_id.setText(id)
            })
            passwordLiveData.observe(this@LoginActivity, Observer { password ->
                login_password.setText(password)
            })
            toastLiveData.observe(this@LoginActivity, Observer { notice ->
                login_btn.isEnabled = true
                login_id.isEnabled = true
                login_password.isEnabled = true
                login_progress_bar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, notice, Toast.LENGTH_SHORT).show()
            })
            nameLiveData.observe(this@LoginActivity, Observer {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            })
            login_web_view.run {
                settings.run {
                    javaScriptEnabled = true
                    setAppCacheEnabled(true)
                    domStorageEnabled = true
                }
            }
            login_btn.setOnClickListener {
                login_btn.isEnabled = false
                login_id.isEnabled = false
                login_password.isEnabled = false
                login_progress_bar.visibility = View.VISIBLE
                login(login_id.text.toString(), login_password.text.toString())?.let {
                    login_web_view.run {
                        webViewClient = it
                        loadUrl("http://sam.skhu.ac.kr")
                    }
                }
            }
            if(isLogin) {
                app.getSharedPreferences("login_info", Context.MODE_PRIVATE).run {
                    login_id.setText(getString("id", ""))
                    login_password.setText(getString("password", ""))
                    login_btn.performClick()
                }
            }
        }
    }
}
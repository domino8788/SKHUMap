package com.domino.skhumap.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
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
        setSupportActionBar(login_tool_bar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java].apply {
            toastLiveData.observe(this@LoginActivity, Observer { notice ->
                login_btn.isEnabled = true
                login_progress_bar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, notice, Toast.LENGTH_SHORT).show()
            })
            nameLiveData.observe(this@LoginActivity, Observer {
                setResult(com.domino.skhumap.contract.Code.RESULT_REQUEST_MY_PAGE_RENEWAL)
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
                login_progress_bar.visibility = View.VISIBLE
                login(login_id.text.toString(), login_password.text.toString())?.let {
                    login_web_view.run {
                        webViewClient = it
                        loadUrl("http://sam.skhu.ac.kr")
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
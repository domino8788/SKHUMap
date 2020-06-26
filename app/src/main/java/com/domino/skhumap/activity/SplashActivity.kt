package com.domino.skhumap.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.domino.skhumap.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val txtAnim = AnimationUtils.loadAnimation(this, R.anim.appear)
        splash_txt.startAnimation(txtAnim)
        txtAnim.setAnimationListener(object:Animation.AnimationListener{
            override fun onAnimationEnd(animation: Animation?) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationStart(animation: Animation?) {}
        })
    }
}
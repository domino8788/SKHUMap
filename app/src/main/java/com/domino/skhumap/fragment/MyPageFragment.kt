package com.domino.skhumap.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.domino.skhumap.R
import com.domino.skhumap.activity.LoginActivity
import kotlinx.android.synthetic.main.fragment_my_page.*

class MyPageFragment(): Fragment() {

    companion object{
        val instance = MyPageFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        my_page_btn_login.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        }
    }

}
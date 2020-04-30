package com.domino.skhumap.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.domino.skhumap.R
import com.domino.skhumap.activity.LoginActivity
import com.domino.skhumap.adapter.MyPageMenuAdapter
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

    fun initLoginInfo(){
        if(isLogin){
            my_page_btn_login.run {
                visibility=View.GONE
                setOnClickListener(null)
            }
            my_page_logged_in.visibility=View.VISIBLE
            context!!.getSharedPreferences("login_info", Context.MODE_PRIVATE).run {
                    my_page_name.text =  getString("name", "")
                    my_page_student_number.text = getString("id", "")
                }
            my_page_menu_list.visibility = View.VISIBLE
            my_page_menu_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            my_page_menu_list.adapter = MyPageMenuAdapter()
        }else {
            my_page_btn_login.run {
                visibility = View.VISIBLE
                setOnClickListener { startActivity(Intent(context, LoginActivity::class.java)) }
            }
            my_page_logged_in.visibility = View.INVISIBLE
        }
    }

    val isLogin:Boolean
    get() = (context?.getSharedPreferences("login_info", Context.MODE_PRIVATE)?.getString("name", "") != "")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLoginInfo()
    }

}
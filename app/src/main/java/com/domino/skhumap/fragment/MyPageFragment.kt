package com.domino.skhumap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.domino.skhumap.R
import com.domino.skhumap.model.AuthViewModel
import kotlinx.android.synthetic.main.fragment_my_page.*

class MyPageFragment(): Fragment() {

    private lateinit var authViewModel:AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

    private fun initLoginInfo(){
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java].apply {
            idLiveData.observe(requireActivity(), Observer { id ->
                my_page_student_number.text = id
            })
            nameLiveData.observe(requireActivity(), Observer { name ->
                my_page_name.text = name
            })
            loadLoginInfo()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLoginInfo()
    }

}
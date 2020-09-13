package com.domino.skhumap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.domino.skhumap.BuildConfig
import com.domino.skhumap.R
import com.domino.skhumap.model.AuthViewModel
import kotlinx.android.synthetic.main.fragment_policy.view.*

class PolicyFragment():Fragment() {
    private lateinit var authViewModel: AuthViewModel

    companion object {
        val TAG = "PolicyFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_policy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        (activity as AppCompatActivity).run {
            setHasOptionsMenu(true)
            setSupportActionBar(view.fragment_policy_tool_bar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                title = "개인정보처리방침 및 이용약관"
            }
        }
        view.policy_version.text = "버전 정보 : ${BuildConfig.VERSION_NAME}"
        view.policy_sign_in_time.text = "${authViewModel.signInTime}에 동의하셨습니다."
    }
}
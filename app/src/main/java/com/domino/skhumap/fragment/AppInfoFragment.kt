package com.domino.skhumap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.domino.skhumap.BuildConfig
import com.domino.skhumap.R
import kotlinx.android.synthetic.main.fragment_app_info.*
import kotlinx.android.synthetic.main.fragment_app_info.view.*

class AppInfoFragment():Fragment() {
    companion object {
        val TAG = "AppInfoFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).run {
            setHasOptionsMenu(true)
            setSupportActionBar(fragment_app_info_tool_bar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                title = "앱 정보"
            }
        }
        view.app_info_version.text = "버전 정보 : ${BuildConfig.VERSION_NAME}"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                requireActivity().onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
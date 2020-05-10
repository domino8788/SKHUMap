package com.domino.skhumap.fragment

import android.content.Context
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.domino.skhumap.R

class MyPagePreferenceFragment:PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.my_page_preference)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when(preference?.key){
            "logout" -> {
                android.webkit.CookieManager.getInstance().removeAllCookie()
                context?.getSharedPreferences("login_info", Context.MODE_PRIVATE)!!.edit().clear().commit()
                (parentFragment as MyPageFragment).initLoginInfo()
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}
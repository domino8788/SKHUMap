package com.domino.skhumap.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.domino.skhumap.R
import com.domino.skhumap.model.AuthViewModel

class MyPagePreferenceFragment:PreferenceFragmentCompat(){

    private lateinit var authViewModel: AuthViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.my_page_preference)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when(preference?.key){
            "logout" -> {
                authViewModel.logout()
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}
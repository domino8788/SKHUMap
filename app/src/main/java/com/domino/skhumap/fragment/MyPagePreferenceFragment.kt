package com.domino.skhumap.fragment

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.domino.skhumap.R
import com.domino.skhumap.activity.LoginActivity
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
                startActivity(Intent(requireActivity(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                })
                requireActivity().finish()
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}
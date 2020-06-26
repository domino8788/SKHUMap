package com.domino.skhumap.fragment

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.domino.skhumap.R
import com.domino.skhumap.activity.LoginActivity
import com.domino.skhumap.model.AuthViewModel
import com.domino.skhumap.model.MainViewModel
import com.domino.skhumap.view.MultipleLevelBottomSheetView

class MyPagePreferenceFragment:PreferenceFragmentCompat(){

    private lateinit var authViewModel: AuthViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.my_page_preference)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
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
            "info" -> {
                val fm = requireActivity().supportFragmentManager
                if(fm.findFragmentByTag(AppInfoFragment.TAG) == null) {
                    val fragment = AppInfoFragment()
                    mainViewModel.setBottomSheetState(MultipleLevelBottomSheetView.State.HIDDEN)
                    fm.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_up,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.slide_out_down)
                        .add(R.id.main_layout, fragment, AppInfoFragment.TAG)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}
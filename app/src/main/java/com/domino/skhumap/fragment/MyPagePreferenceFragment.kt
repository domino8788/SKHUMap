package com.domino.skhumap.fragment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
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
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finish()
            }
            "signout"->{
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("회원탈퇴 하시겠습니까?\n회원탈퇴 시 모든 유저 데이터가 삭제 됩니다.")
                    .setPositiveButton("회원탈퇴") { _, _ ->
                        authViewModel.signOut()
                        authViewModel.logout()
                        startActivity(Intent(requireActivity(), LoginActivity::class.java))
                        requireActivity().finish()
                    }
                    .setNegativeButton("취소") { _, _ -> }
                builder.create().show()
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
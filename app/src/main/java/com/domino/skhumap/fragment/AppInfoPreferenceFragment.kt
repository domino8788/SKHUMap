package com.domino.skhumap.fragment

import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.domino.skhumap.R

class AppInfoPreferenceFragment:PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.app_info_preference)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.overScrollMode = ListView.OVER_SCROLL_NEVER
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when(preference!!.key) {
            "policy" -> {
                val fm = requireActivity().supportFragmentManager
                if(fm.findFragmentByTag(PolicyFragment.TAG) == null){
                    fm.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_up,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.slide_out_down)
                        .add(R.id.main_layout, PolicyFragment(), PolicyFragment.TAG)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

}
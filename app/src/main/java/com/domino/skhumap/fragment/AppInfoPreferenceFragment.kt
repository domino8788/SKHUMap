package com.domino.skhumap.fragment

import android.os.Bundle
import android.view.View
import android.widget.ListView
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

}
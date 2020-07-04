package com.domino.skhumap.fragment

import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.preference.PreferenceFragmentCompat
import com.domino.skhumap.R

class PolicyPreferenceFragment:PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.policy_preference)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.overScrollMode = ListView.OVER_SCROLL_NEVER
    }

}
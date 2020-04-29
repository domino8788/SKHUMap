package com.domino.skhumap.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.domino.skhumap.fragment.FacilityFragment

class MenuAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
    val fragments = ArrayList<Fragment>()

    init {
        fragments.add(FacilityFragment.instance)
        fragments.add(Fragment())
        fragments.add(Fragment())
        fragments.add(Fragment())

    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

}
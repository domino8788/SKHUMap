package com.domino.skhumap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.domino.skhumap.R
import com.domino.skhumap.adapter.FavoritesAdapter
import com.domino.skhumap.dto.SearchableFacility
import kotlinx.android.synthetic.main.fragment_facility.*

class FacilityFragment : Fragment {

    private constructor():super()

    val searchableFacilityList:MutableList<SearchableFacility> = mutableListOf()

    companion object{
        private val facilityFragment = FacilityFragment()
        val instance = facilityFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_facility, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list_facility.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        list_facility.adapter = FavoritesAdapter(searchableFacilityList)
    }
}

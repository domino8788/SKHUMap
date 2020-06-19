package com.domino.skhumap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.domino.skhumap.R
import com.domino.skhumap.dto.SearchableFacility
import com.domino.skhumap.model.MainViewModel
import com.domino.skhumap.model.MapViewModel
import com.domino.skhumap.view.MultipleLevelBottomSheetView
import kotlinx.android.synthetic.main.fragment_facility_info.view.*

class FacilityInfoFragment(private val facility: SearchableFacility) : Fragment() {
    private lateinit var mapViewModel: MapViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_facility_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java].apply {
            selectedFacilityInfoLiveData.observe(requireActivity(), Observer { lectureList ->
                mainViewModel.setBottomSheetState(MultipleLevelBottomSheetView.State.HIDDEN)
                view.fragment_facility_info_timetable.load(lectureList)
            })
            selectedFacilityLiveData.observe(requireActivity(), Observer { searchableFacility ->
                mainViewModel.requestHttp(getSelectedFacilityInfo(searchableFacility.facility.id))
            })
            selectedFacilityLiveData.postValue(facility)
        }
    }
}

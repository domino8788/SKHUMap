package com.domino.skhumap.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.domino.skhumap.R
import com.domino.skhumap.dto.SearchableFacility
import com.domino.skhumap.model.FavoritesViewModel
import com.domino.skhumap.model.MainViewModel
import com.domino.skhumap.model.MapViewModel
import kotlinx.android.synthetic.main.fragment_facility_info.*
import kotlinx.android.synthetic.main.fragment_facility_info.view.*

class FacilityInfoFragment(private val facility: SearchableFacility) : Fragment() {
    companion object {
        const val TAG = "FacilityInfoFragment"
    }
    private lateinit var mapViewModel: MapViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var favoritesViewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_facility_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).run {
            setHasOptionsMenu(true)
            setSupportActionBar(fragment_facility_info_toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                title = "${facility.department?.name!![0]!!.plus(" "+facility.floorNumber.toString().plus("층 "))?:""}${facility.facility.id}"
            }
        }
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java].apply {
            selectedFacilityInfoLiveData.observe(viewLifecycleOwner, Observer { lectureList ->
                view.fragment_facility_info_timetable.load(lectureList)
            })
            selectedFacilityLiveData.observe(viewLifecycleOwner, Observer { searchableFacility ->
                mainViewModel.requestHttp(getSelectedFacilityInfo(searchableFacility.facility.id))
            })
            selectedFacilityLiveData.postValue(facility)
        }
        favoritesViewModel = ViewModelProvider(requireActivity())[FavoritesViewModel::class.java]
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_facility_info, menu)
        menu.findItem(R.id.fragment_facility_info_is_favorites).icon =
            if(favoritesViewModel.isExists(facility)) resources.getDrawable(R.drawable.ic_favorite_black_24dp)
            else resources.getDrawable(R.drawable.ic_favorite_border_black_24dp)
        super.onCreateOptionsMenu(menu, inflater)
    }
}

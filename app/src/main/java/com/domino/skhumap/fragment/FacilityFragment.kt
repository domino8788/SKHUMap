package com.domino.skhumap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.domino.skhumap.R
import com.domino.skhumap.adapter.FavoritesListAdapter
import com.domino.skhumap.model.FavoritesViewModel
import com.domino.skhumap.model.MainViewModel
import com.domino.skhumap.model.MapViewModel
import com.domino.skhumap.view.MultipleLevelBottomSheetView
import kotlinx.android.synthetic.main.fragment_facility.view.*

class FacilityFragment() : Fragment() {
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var mapViewModel : MapViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_facility, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapViewModel = ViewModelProvider(requireActivity())[MapViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        favoritesViewModel = ViewModelProvider(requireActivity())[FavoritesViewModel::class.java].apply {
            view.favorites_view.adapter = FavoritesListAdapter(favorites){favorites->
                mapViewModel.markMapLiveData.value = favorites
            }
            favoritesLiveData.observe(requireActivity(), Observer {
                view.favorites_view.notifyDataSetChanged()
            })
            view.favorites_view.setOnEditButtonClickListener { list->
                val fm = requireActivity().supportFragmentManager
                if(fm.findFragmentByTag(EditFavoritesFragment.TAG) == null) {
                    val fragment = EditFavoritesFragment(list)
                    mainViewModel.setBottomSheetState(MultipleLevelBottomSheetView.State.HIDDEN)
                    fm.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_up,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.slide_out_down)
                        .add(R.id.main_layout, fragment, EditFavoritesFragment.TAG)
                        .addToBackStack(null)
                        .commit()
                }
            }
            query()
        }
    }
}

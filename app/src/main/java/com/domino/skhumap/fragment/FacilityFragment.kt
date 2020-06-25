package com.domino.skhumap.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.domino.skhumap.R
import com.domino.skhumap.activity.EditFavoritesActivity
import com.domino.skhumap.adapter.FavoritesListAdapter
import com.domino.skhumap.contract.Code
import com.domino.skhumap.model.FavoritesViewModel
import com.domino.skhumap.model.MapViewModel
import kotlinx.android.synthetic.main.fragment_facility.*

class FacilityFragment() : Fragment() {

    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var mapViewModel : MapViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_facility, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        favoritesViewModel = ViewModelProvider(requireActivity())[FavoritesViewModel::class.java]
        mapViewModel = ViewModelProvider(requireActivity())[MapViewModel::class.java]

        favoritesViewModel.run {
            list_facility.run {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = FavoritesListAdapter(favoritesViewModel.favoritesLiveData.value!!, mapViewModel)

                val radius = resources.getDimensionPixelSize(R.dimen.radius);
                val dotsHeight = resources.getDimensionPixelSize(R.dimen.dots_height);
                val color = resources.getColor(R.color.colorAccent);
                addItemDecoration(FavoritesListAdapter.DotsIndicatorDecoration(radius, radius * 4, dotsHeight, color, color))
                PagerSnapHelper().attachToRecyclerView(this)
            }
            favoritesLiveData.observe(requireActivity(), Observer {
                list_facility?.adapter?.notifyDataSetChanged()
                list_facility?.smoothScrollToPosition((it.size-1)/4)
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

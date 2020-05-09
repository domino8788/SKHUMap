package com.domino.skhumap.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.domino.skhumap.R
import com.domino.skhumap.activity.EditFavoritesActivity
import com.domino.skhumap.adapter.FavoritesListAdapter
import com.domino.skhumap.contract.Code
import com.domino.skhumap.dto.SearchableFacility
import kotlinx.android.synthetic.main.fragment_facility.*

class FacilityFragment() : Fragment() {

    val searchableFacilityList:MutableList<SearchableFacility> = mutableListOf()

    companion object{
        lateinit var instance:FacilityFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_facility, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list_facility.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        list_facility.adapter = FavoritesListAdapter(searchableFacilityList)

        val radius = resources.getDimensionPixelSize(R.dimen.radius);
        val dotsHeight = resources.getDimensionPixelSize(R.dimen.dots_height);
        val color = resources.getColor(R.color.colorAccent);
        list_facility.addItemDecoration(FavoritesListAdapter.DotsIndicatorDecoration(radius, radius * 4, dotsHeight, color, color))
        PagerSnapHelper().attachToRecyclerView(list_facility)

        btn_edit.setOnClickListener { startActivityForResult(Intent(context, EditFavoritesActivity::class.java), Code.REQUEST_EDIT_FAVORITES) }
    }
}

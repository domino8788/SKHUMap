package com.domino.skhumap.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.domino.skhumap.Interface.ItemTouchHelperAdapter
import com.domino.skhumap.R
import com.domino.skhumap.adapter.EditFacilityItemTouchHelperCallback
import com.domino.skhumap.adapter.EditFacilityListAdapter
import com.domino.skhumap.dto.SearchableFacility
import com.domino.skhumap.model.FavoritesViewModel
import kotlinx.android.synthetic.main.fragment_edit_favorites.*

class EditFavoritesFragment(val list:ArrayList<SearchableFacility>) : Fragment()  {
    companion object {
        const val TAG = "EditFavoritesFragment"
    }
    private lateinit var favoritesViewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        favoritesViewModel = ViewModelProvider(requireActivity())[FavoritesViewModel::class.java]

        (activity as AppCompatActivity).run {
            setHasOptionsMenu(true)
            setSupportActionBar(fragment_edit_favorites_tool_bar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                title = ""
            }
        }

        fragment_edit_facility_list.run {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = EditFacilityListAdapter(list).apply {
                touchHelper = ItemTouchHelper(EditFacilityItemTouchHelperCallback(this as ItemTouchHelperAdapter)).apply { attachToRecyclerView(fragment_edit_facility_list) }
            }
        }

        fragment_edit_btn_delete.setOnClickListener {
            (fragment_edit_facility_list.adapter as EditFacilityListAdapter).run {
                for(i in checkedList.size-1 downTo 0)
                    if(checkedList[i])
                        onItemDismiss(i)
            }
            fragment_edit_check_box_all_select.isChecked = false
        }

        fragment_edit_check_box_all_select.setOnClickListener {
            (fragment_edit_facility_list.adapter as EditFacilityListAdapter).run {
                checkedList.forEachIndexed { index, b -> checkedList[index] = fragment_edit_check_box_all_select.isChecked }
                notifyDataSetChanged()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                requireActivity().onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        favoritesViewModel.updateAll()
        super.onDestroyView()
    }
}
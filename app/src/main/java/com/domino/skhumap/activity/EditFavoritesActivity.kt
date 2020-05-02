package com.domino.skhumap.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.domino.skhumap.Interface.ItemTouchHelperAdapter
import com.domino.skhumap.R
import com.domino.skhumap.adapter.EditFacilityItemTouchHelperCallback
import com.domino.skhumap.adapter.EditFacilityListAdapter
import com.domino.skhumap.fragment.FacilityFragment
import kotlinx.android.synthetic.main.activity_edit_favorites.*
import kotlinx.android.synthetic.main.fragment_facility.*

class EditFavoritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_favorites)

        setSupportActionBar(edit_tool_bar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }
        edit_facility_list.run {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = EditFacilityListAdapter(FacilityFragment.instance.searchableFacilityList).apply {
                touchHelper = ItemTouchHelper(EditFacilityItemTouchHelperCallback(this as ItemTouchHelperAdapter)).apply { attachToRecyclerView(edit_facility_list) }
            }
        }

        edit_btn_delete.setOnClickListener {
            (edit_facility_list.adapter as EditFacilityListAdapter).run {
                for(i in checkedList.size-1 downTo 0)
                    if(checkedList[i])
                        onItemDismiss(i)
            }
        }

        edit_check_box_all_select.setOnCheckedChangeListener { buttonView, isChecked ->
            (edit_facility_list.adapter as EditFacilityListAdapter).run {
                checkedList.forEachIndexed { index, b -> checkedList[index] = isChecked }
                notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        FacilityFragment.instance.list_facility.adapter?.notifyDataSetChanged()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

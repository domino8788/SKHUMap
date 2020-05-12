package com.domino.skhumap.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.domino.skhumap.Interface.ItemTouchHelperAdapter
import com.domino.skhumap.R
import com.domino.skhumap.adapter.EditFacilityItemTouchHelperCallback
import com.domino.skhumap.adapter.EditFacilityListAdapter
import com.domino.skhumap.contract.Code
import com.domino.skhumap.dto.SearchableFacility
import kotlinx.android.synthetic.main.activity_edit_favorites.*

class EditFavoritesActivity : AppCompatActivity() {

    lateinit var favorites:ArrayList<SearchableFacility>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_favorites)

        favorites = intent.getParcelableArrayListExtra("favorites")

        setSupportActionBar(edit_tool_bar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }

        edit_facility_list.run {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = EditFacilityListAdapter(favorites).apply {
                touchHelper = ItemTouchHelper(EditFacilityItemTouchHelperCallback(this as ItemTouchHelperAdapter)).apply { attachToRecyclerView(edit_facility_list) }
            }
        }

        edit_btn_delete.setOnClickListener {
            (edit_facility_list.adapter as EditFacilityListAdapter).run {
                for(i in checkedList.size-1 downTo 0)
                    if(checkedList[i])
                        onItemDismiss(i)
            }
            edit_check_box_all_select.isChecked = false
        }

        edit_check_box_all_select.setOnClickListener {
            (edit_facility_list.adapter as EditFacilityListAdapter).run {
                checkedList.forEachIndexed { index, b -> checkedList[index] = edit_check_box_all_select.isChecked }
                notifyDataSetChanged()
            }
        }

        setResult(Code.RESULT_REQUEST_FAVORITES_RENEWAL, Intent().apply {
            putParcelableArrayListExtra("favorites", favorites)
        })
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

package com.domino.skhumap.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

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



        edit_check_box_all_select.setOnCheckedChangeListener { buttonView, isChecked ->
            (edit_facility_list.adapter as EditFacilityListAdapter).run {
                isSelectAll = isChecked
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

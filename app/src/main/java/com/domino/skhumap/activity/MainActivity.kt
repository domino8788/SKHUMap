package com.domino.skhumap.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.domino.skhumap.R
import com.domino.skhumap.adapter.MenuAdapter
import com.domino.skhumap.db.FirestoreHelper
import com.domino.skhumap.view.MultipleLevelBottomSheetView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_menu_tab.view.*

class MainActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    lateinit var context: Context

    init {
        instance = this
    }

    companion object {
        private var instance: AppCompatActivity? = null
        val context: Context
            get() {
                return instance!!.applicationContext
            }
        val appCompatActivity: AppCompatActivity
            get() {
                return instance!!
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirestoreHelper.init(this)
        main_bottom_sheet.initView()
        main_view_pager.run {
            adapter = MenuAdapter(supportFragmentManager, 0)
            setOnTouchListener { v, event ->  true }
        }
        main_tab_layout.let{
            it.setupWithViewPager(main_view_pager)
            it.addOnTabSelectedListener(this)
            initTab()
        }
    }

    fun initTab(){
        val titles = resources.getStringArray(R.array.menu)
        val icons = arrayOf(R.drawable.ic_location_on_black_24dp, R.drawable.ic_chat_black_24dp, R.drawable.ic_calendar_black_24dp ,R.drawable.ic_account_circle_black_24dp)

        main_tab_layout.run {
            for(i in  0..tabCount) {
                getTabAt(i)?.customView = View.inflate(context, R.layout.item_menu_tab, null).apply {
                    this.item_menu_icon.setImageResource(icons[i])
                    this.item_menu_title.text = titles[i]
                }
            }
            getTabAt(1)?.select()
            getTabAt(0)?.select()

        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
        tab.apply {
            (customView as LinearLayout).run {
                item_menu_icon.colorFilter = null
                item_menu_title.setTextColor(resources.getColor(android.R.color.secondary_text_light))
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        main_view_pager.currentItem = tab.position
        tab.apply {
            customView!!.run {
                item_menu_icon.setColorFilter(resources.getColor(R.color.colorAccent))
                item_menu_title.setTextColor(resources.getColor(R.color.colorAccent))
            }
        }
        main_bottom_sheet.level = MultipleLevelBottomSheetView.STATE.HALF_EXPANDED
    }


}

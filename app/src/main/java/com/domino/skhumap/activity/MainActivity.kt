package com.domino.skhumap.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.domino.skhumap.R
import com.domino.skhumap.adapter.MenuAdapter
import com.domino.skhumap.contract.Code
import com.domino.skhumap.db.FirestoreHelper
import com.domino.skhumap.model.AuthViewModel
import com.domino.skhumap.model.FavoritesViewModel
import com.domino.skhumap.model.MapViewModel
import com.domino.skhumap.view.MultipleLevelBottomSheetView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_menu_tab.view.*

class MainActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    private var doubleBackToExitPressedOnce = false
    private lateinit var favoritesViewModel: FavoritesViewModel
    private lateinit var mapViewModel: MapViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        FirestoreHelper.init(this)
        main_bottom_sheet.initView()
        main_view_pager.run {
            adapter = MenuAdapter(supportFragmentManager, 0)
        }
        main_tab_layout.let{
            it.setupWithViewPager(main_view_pager)
            it.addOnTabSelectedListener(this)
            initTab()
        }
        favoritesViewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
    }

    private fun initTab(){
        val titles = resources.getStringArray(R.array.menu)
        val icons = arrayOf(R.drawable.ic_location_on_black_24dp, R.drawable.ic_calendar_black_24dp ,R.drawable.ic_account_circle_black_24dp)

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
        tab.apply {
            customView!!.run {
                item_menu_icon.setColorFilter(resources.getColor(R.color.colorAccent))
                item_menu_title.setTextColor(resources.getColor(R.color.colorAccent))
            }
        }
        main_bottom_sheet.level = MultipleLevelBottomSheetView.State.HALF_EXPANDED
    }

    override fun onBackPressed() {
            if ( mapViewModel.selectedDepartment!= null) {
                mapViewModel.selectedDepartment = null
            } else {
                if (doubleBackToExitPressedOnce) {
                    return super.onBackPressed()
                }
                doubleBackToExitPressedOnce = true
                Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        (main_view_pager.adapter as MenuAdapter).run {
            when(resultCode){
                Code.RESULT_REQUEST_FAVORITES_RENEWAL -> {
                    data?.let { favoritesViewModel.updateAll(it.getParcelableArrayListExtra("favorites")) }
                }
                Code.RESULT_REQUEST_MY_PAGE_RENEWAL -> authViewModel.loadLoginInfo()
            }
            true
        }
    }
}

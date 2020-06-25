package com.domino.skhumap.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.domino.skhumap.R
import com.domino.skhumap.adapter.FavoritesListAdapter
import com.domino.skhumap.dto.SearchableFacility
import kotlinx.android.synthetic.main.view_favorites.view.*

class FavoritesView(@get:JvmName("getContext_")val context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    constructor(context: Context):this(context, null)
    constructor(context:Context, attrs:AttributeSet?):this(context, attrs, 0)
    private val view:View
    var adapter:FavoritesListAdapter? = null
        set(adapter) {
            favoritesList = adapter!!.list
            view.list_facility.adapter = adapter
        }
    private lateinit var favoritesList:ArrayList<SearchableFacility>
    init {
        view = View.inflate(context, R.layout.view_favorites, this).also {view ->
            view.favorites_progress_bar.visibility = View.VISIBLE
            view.btn_edit.isEnabled = false
            view.list_facility.run {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val radius = resources.getDimensionPixelSize(R.dimen.radius);
                val dotsHeight = resources.getDimensionPixelSize(R.dimen.dots_height);
                val color = resources.getColor(R.color.colorAccent);
                addItemDecoration(FavoritesListAdapter.DotsIndicatorDecoration(radius, radius * 4, dotsHeight, color, color))
                PagerSnapHelper().attachToRecyclerView(this)
            }
        }
    }
    fun setOnEditButtonClickListener(onclick:(ArrayList<SearchableFacility>)->Unit){
        btn_edit.setOnClickListener{ onclick(favoritesList) }
    }
    fun notifyDataSetChanged() {
        list_facility?.run {
            view.favorites_progress_bar.visibility = View.GONE
            view.btn_edit.isEnabled = true
            adapter?.notifyDataSetChanged()
            smoothScrollToPosition((favoritesList.size - 1) / 4)
        }
    }
}
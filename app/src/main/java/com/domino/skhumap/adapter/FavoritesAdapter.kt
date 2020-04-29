package com.domino.skhumap.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.domino.skhumap.R
import com.domino.skhumap.dto.SearchableFacility
import com.domino.skhumap.manager.MapManager
import kotlinx.android.synthetic.main.item_favorites_list.view.*
import kotlinx.android.synthetic.main.item_favorites_list_item.view.*

class FavoritesAdapter(private val list: MutableList<SearchableFacility>) :
    RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    inner class FavoritesViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_favorites_list, parent, false)
    ) {
        val favorites1 = itemView.favorites1 as LinearLayout
        val favorites2 = itemView.favorites2 as LinearLayout
        val favorites3 = itemView.favorites3 as LinearLayout
        val favorites4 = itemView.favorites4 as LinearLayout
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.run {
            listOf(favorites1, favorites2, favorites3, favorites4).forEachIndexed { index, view ->
                val listIndex =  position * 4 + index
                if(listIndex < list.size) {
                    val searchableFacility = list[position * 4 + index]
                    view.run {
                        item_favorites_icon.setImageResource(searchableFacility.facility.resourceId)
                        item_favorites_title.text = searchableFacility.facility.id
                        setOnClickListener { MapManager.markMap(searchableFacility) }
                    }
                } else{
                    view.run {
                        item_favorites_icon.setImageResource(0)
                        item_favorites_title.text = ""
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder =
        FavoritesViewHolder(parent)

    override fun getItemCount(): Int {
        return if(list.size!=0) (list.size-1)/4+1 else 0
    }
}


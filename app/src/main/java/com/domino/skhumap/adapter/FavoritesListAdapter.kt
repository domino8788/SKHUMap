package com.domino.skhumap.adapter

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.domino.skhumap.R
import com.domino.skhumap.dto.SearchableFacility
import com.domino.skhumap.model.MapViewModel
import kotlinx.android.synthetic.main.item_favorites_list.view.*
import kotlinx.android.synthetic.main.item_favorites_list_item.view.*

class FavoritesListAdapter(private val list: MutableList<SearchableFacility>, private val mapViewModel: MapViewModel) :
    RecyclerView.Adapter<FavoritesListAdapter.FavoritesViewHolder>() {

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
                        item_favorites_icon.setImageResource(searchableFacility.facility!!.resourceId)
                        item_favorites_title.text = searchableFacility.facility!!.id
                        setOnClickListener { mapViewModel.markMapLivdeData.value = searchableFacility  }
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

    class DotsIndicatorDecoration(private val radius: Int, private val padding: Int, indicatorHeight: Int, @ColorInt colorInactive: Int, @ColorInt colorActive: Int
    ) :
        ItemDecoration() {
        private val indicatorHeight: Int
        private val indicatorItemPadding: Int
        private val inactivePaint = Paint()
        private val activePaint = Paint()

        override fun onDrawOver(c: Canvas,parent: RecyclerView,state: RecyclerView.State) {
            super.onDrawOver(c, parent, state)

            val adapter = parent.adapter ?: return
            val itemCount = adapter.itemCount

            val totalLength = radius * 2 * itemCount.toFloat()
            val paddingBetweenItems = Math.max(0, itemCount - 1) * indicatorItemPadding.toFloat()
            val indicatorTotalWidth = totalLength + paddingBetweenItems
            val indicatorStartX = (parent.width - indicatorTotalWidth) / 2f

            val indicatorPosY = parent.height - indicatorHeight / 2f
            drawInactiveDots(c, indicatorStartX, indicatorPosY, itemCount)
            val activePosition: Int = if (parent.layoutManager is GridLayoutManager) {
                (parent.layoutManager as GridLayoutManager?)!!.findFirstVisibleItemPosition()
            } else if (parent.layoutManager is LinearLayoutManager) {
                (parent.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
            } else {
                return
            }
            if (activePosition == RecyclerView.NO_POSITION) {
                return
            }

            val activeChild = parent.layoutManager!!.findViewByPosition(activePosition) ?: return
            drawActiveDot(c, indicatorStartX, indicatorPosY, activePosition)
        }

        private fun drawInactiveDots(
            c: Canvas,
            indicatorStartX: Float,
            indicatorPosY: Float,
            itemCount: Int
        ) {
            val itemWidth = radius * 2 + indicatorItemPadding.toFloat()
            var start = indicatorStartX + radius
            for (i in 0 until itemCount) {
                c.drawCircle(start, indicatorPosY, radius.toFloat(), inactivePaint)
                start += itemWidth
            }
        }

        private fun drawActiveDot(
            c: Canvas, indicatorStartX: Float, indicatorPosY: Float,
            highlightPosition: Int
        ) {
            val itemWidth = radius * 2 + indicatorItemPadding.toFloat()
            val highlightStart =
                indicatorStartX + radius + itemWidth * highlightPosition
            c.drawCircle(highlightStart, indicatorPosY, radius.toFloat(), activePaint)
        }

        override fun getItemOffsets(outRect: Rect,view: View,parent: RecyclerView,state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.bottom = indicatorHeight
        }

        init {
            val strokeWidth = Resources.getSystem().displayMetrics.density * 1
            this.indicatorHeight = indicatorHeight
            inactivePaint.run {
                strokeCap = Paint.Cap.ROUND
                this.strokeWidth = strokeWidth
                style = Paint.Style.STROKE
                isAntiAlias = true
                color = colorInactive
            }
            activePaint.run {
                strokeCap = Paint.Cap.ROUND
                this.strokeWidth = strokeWidth
                style = Paint.Style.FILL
                isAntiAlias = true
                color = colorActive
            }
            indicatorItemPadding = padding
        }
    }
}


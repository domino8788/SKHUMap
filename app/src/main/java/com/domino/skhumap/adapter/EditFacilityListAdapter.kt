package com.domino.skhumap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.domino.skhumap.R
import com.domino.skhumap.dto.SearchableFacility
import kotlinx.android.synthetic.main.item_favorites_edit_list_item.view.*

class EditFacilityListAdapter(private val list: MutableList<SearchableFacility>): RecyclerView.Adapter<EditFacilityListAdapter.EditFacilityViewHolder>()  {

    val checkedList = mutableListOf<Int>()
    var isSelectAll:Boolean? = null

    inner class EditFacilityViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_favorites_edit_list_item, parent, false)) {
        val check_box_select = itemView.edit_item_check_box
        val img_icon = itemView.edit_item_img_icon
        val txt_title = itemView.edit_item_txt_title
        val btn_move = itemView.edit_item_btn_move
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditFacilityListAdapter.EditFacilityViewHolder = EditFacilityViewHolder(parent)

    override fun getItemCount(): Int = if(list.size==0) 1 else list.size

    override fun onBindViewHolder(holder: EditFacilityListAdapter.EditFacilityViewHolder, position: Int) {
        if(list.size == 0 ){
            holder.run {
                txt_title.text = "즐겨찾기가 없습니다."
                check_box_select.visibility = View.GONE
                btn_move.visibility = View.GONE
            }
        } else {
            holder.run {
                txt_title.text = list[position].facility.id
                img_icon.setImageResource(list[position].facility.resourceId)


                check_box_select.run {
                    isSelectAll?.let { isChecked = it }
                    setOnCheckedChangeListener { buttonView, isChecked ->
                        if(isChecked) {
                            checkedList.add(position)
                        } else {
                            checkedList.remove(position)
                        }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = if(list.size == 0) 0 else 1


}

class EditFacilityItemTouchHelperCallback(private val mAdapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (viewHolder.itemViewType == 1) {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)
        }
        return 0
    }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean =
        if (viewHolder.itemViewType == 1) {
            mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
            true
        } else
            false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (viewHolder.itemViewType == 1)
            mAdapter.onItemDismiss(viewHolder.adapterPosition)
    }

}

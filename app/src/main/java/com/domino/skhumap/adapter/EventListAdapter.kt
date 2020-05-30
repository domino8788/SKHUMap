package com.domino.skhumap.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.domino.skhumap.R
import com.domino.skhumap.dto.Event
import com.domino.skhumap.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_event_list.view.*

class EventListAdapter(val onClick: (Event) -> Unit) : RecyclerView.Adapter<EventListAdapter.EventViewHolder>() {

    val events = mutableListOf<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(parent.inflate(R.layout.item_event_list))
    }

    override fun onBindViewHolder(viewHolder: EventViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    inner class EventViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
            itemView.setOnClickListener {
                onClick(events[adapterPosition])
            }
        }

        fun bind(event: Event) {
            containerView.txt_event.text = event.text
        }
    }

}
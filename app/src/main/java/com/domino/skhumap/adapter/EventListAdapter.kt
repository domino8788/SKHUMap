package com.domino.skhumap.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.domino.skhumap.R
import com.domino.skhumap.dto.Schedule
import com.domino.skhumap.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_event_list.view.*

class EventListAdapter(val onClick: (Schedule) -> Unit) : RecyclerView.Adapter<EventListAdapter.EventViewHolder>() {

    val events = mutableListOf<Schedule>()

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

        fun bind(event: Schedule) {
            containerView.run {
                txt_event_title.text = event.name
                txt_event_info.text = event.info
                txt_event_time.text = "${event.frTm} ~ ${event.toTm}"
            }
        }
    }

}
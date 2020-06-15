package com.domino.skhumap.dto;

import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

data class Sticker(val view:ArrayList<TextView> = arrayListOf(), val schedules: ArrayList<TimetableSchedule> = arrayListOf()):Serializable {

    fun addTextView(v:TextView){
        view.add(v);
    }

    fun addSchedule(schedule:TimetableSchedule){
        schedules.add(schedule);
    }

}

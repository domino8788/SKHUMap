package com.domino.skhumap.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.domino.skhumap.dto.Schedule
import com.domino.skhumap.vo.Lecture

class CalendarViewModel(val app: Application) : AndroidViewModel(app) {
    private lateinit var lectureList: MutableList<Lecture>
    private lateinit var scheduleList: MutableList<Schedule>
    val studentScheduleLiveData: MutableLiveData<List<Lecture>> by lazy { MutableLiveData<List<Lecture>>() }
    val personalScheduleLiveData: MutableLiveData<List<Schedule>> by lazy { MutableLiveData<List<Schedule>>() }
}
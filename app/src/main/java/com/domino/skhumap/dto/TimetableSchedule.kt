package com.domino.skhumap.dto

import com.domino.skhumap.utils.yoilToNumber
import com.domino.skhumap.vo.Lecture

class TimetableSchedule(private vararg val schedules: Lecture) {
    val startTime:Time
    val endTime:Time
    var day:Int= 0
    init {
        day = schedules[0].yoilNm.yoilToNumber()-1
        startTime = schedules[0].frTm.split(":").let { Time(it[0].toInt(), it[1].toInt()) }
        endTime = schedules[0].toTm.split(":").let { Time(it[0].toInt(), it[1].toInt()) }
    }

    override fun toString(): String = schedules[0].run { "$gwamogKorNm\n${if(schedules.size==1) "" else "${schedules.size}개 학과 공동 개설 과목-"}$gyosuNm\n$frTm~$toTm" }

}
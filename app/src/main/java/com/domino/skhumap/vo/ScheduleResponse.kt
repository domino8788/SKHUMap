package com.domino.skhumap.vo

import com.google.gson.annotations.SerializedName

data class ScheduleResponse(
    @SerializedName("DAT")
    val schedules:List<Lecture>,
    @SerializedName("MSG")
    val msg:String?,
    @SerializedName("STS")
    val sts:Int)
package com.domino.skhumap.vo

import com.domino.skhumap.dto.Schedule
import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName
import java.util.*

data class Lecture (
    @SerializedName("Yy")
    val yy: String,
    @SerializedName("Haggi")
    val haggi: String,

    @SerializedName("GwamogCd")
    val gwamogCd: String,

    @SerializedName("Bunban")
    val bunban: String,

    @SerializedName("GwamogKorNm")
    val gwamogKorNm: String,

    @SerializedName("YoilNm")
    val yoilNm: String,

    @SerializedName("FrTm")
    val frTm: String,

    @SerializedName("ToTm")
    val toTm: String,

    @SerializedName("AdjustFrTm")
    val adjustFrTm: String,

    @SerializedName("AdjustToTm")
    val adjustToTm: String,

    @SerializedName("Times")
    val times: String,

    @SerializedName("Bigo")
    val bigo: String,

    @SerializedName("Bigo2")
    val bigo2: String,

    @SerializedName("GyosuSabeon")
    val gyosuSabeon: String,

    @SerializedName("GyosuNm")
    val gyosuNm: String,

    @SerializedName("HosilCd")
    val hosilCd: String,

    @SerializedName("RoomName")
    val roomName: String
){
     val toSchedule:Schedule
    get() {
        val calendar = Calendar.getInstance()
        var startDate:Timestamp? = null
        var endDate:Timestamp? = null
        when(haggi){
            "Z0101" -> {
                startDate = Timestamp(Date(calendar.get(Calendar.YEAR)-1900, 3, 1))
                endDate = Timestamp(Date(calendar.get(Calendar.YEAR)-1900, 6, 30))
            }
            "Z0102" -> {
                startDate = Timestamp(Date(calendar.get(Calendar.YEAR)-1900, 9, 1))
                endDate = Timestamp(Date(calendar.get(Calendar.YEAR)-1900, 12, 31))
            }
            "Z0103" -> {
                startDate = Timestamp(Date(calendar.get(Calendar.YEAR)-1900, 7, 1))
                endDate = Timestamp(Date(calendar.get(Calendar.YEAR)-1900, 7, 14))
            }
            "Z0104" -> {
                startDate = Timestamp(Date(calendar.get(Calendar.YEAR)-1900, 1, 1))
                endDate = Timestamp(Date(calendar.get(Calendar.YEAR)-1900, 1, 15))
            }
        }
        return Schedule(Schedule.TYPE_EDIT_STUDENT_SCHEDULE, gwamogKorNm, "$gyosuNm\n$roomName", mutableListOf(yoilNm),
            startDate, endDate, true, adjustFrTm, adjustToTm, frTm, toTm)
    }
}
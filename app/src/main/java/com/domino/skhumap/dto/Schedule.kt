package com.domino.skhumap.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Schedule(
    val type:Long=0,
    var name:String="",
    var info:String? = null,
    var yoil:MutableList<String>? = null,
    var startDate:Timestamp? = null,
    var endDate:Timestamp? = null,
    var everyWeek:Boolean = false,
    var adjustFrTm:String = "",
    var adjustToTm:String = "",
    var frTm:String = "",
    var toTm:String = ""
):Serializable{
    companion object{
        const val TYPE_PERSONAL:Long = 0L
        const val TYPE_EDIT_STUDENT_SCHEDULE:Long = 1L
        const val TYPE_STUDENT_SCHEDULE:Long = 2L
    }
    @DocumentId var id:String = ""
}
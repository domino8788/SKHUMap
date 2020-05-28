package com.domino.skhumap.dto

import com.google.firebase.firestore.DocumentId

data class Schedule(
    @DocumentId val id:String,
    val type:Long=0,
    var name:String="",
    var info:HashMap<String, String>? = null,
    var yoil:MutableList<String>? = null,
    var everyWeek:Boolean = false,
    var adjustFrTm:String = "",
    var adjustToTm:String = "",
    var frTm:String = "",
    var toTm:String = ""
    )
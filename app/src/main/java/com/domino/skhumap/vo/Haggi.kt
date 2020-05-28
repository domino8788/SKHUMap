package com.domino.skhumap.vo

import com.google.gson.annotations.SerializedName

data class Haggi(
    @SerializedName("Yy")
    val yy:String,
    @SerializedName("Haggi")
    val haggi:String,
    @SerializedName("HaggiNm")
    val haggiNm:String
)
package com.domino.skhumap.vo

import com.google.gson.annotations.SerializedName

data class CurrentHaggiResponse(
    @SerializedName("DAT")
    val haggi:Haggi,
    @SerializedName("MSG")
    val msg:String?,
    @SerializedName("STS")
    val sts:Int)
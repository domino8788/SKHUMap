package com.domino.skhumap.vo

import com.google.gson.annotations.SerializedName


data class DATVO (
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
)
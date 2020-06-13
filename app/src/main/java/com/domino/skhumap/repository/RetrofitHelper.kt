package com.domino.skhumap.repository

import com.domino.skhumap.vo.CurrentHaggiResponse
import com.domino.skhumap.vo.Haggi
import com.domino.skhumap.vo.ScheduleResponse
import com.domino.skhumap.vo.TargetFacility
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object RetrofitHelper {
    private val gson = GsonBuilder().setLenient().create()
    private val httpClient = OkHttpClient.Builder().apply {
        interceptors().add(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    }.build()

    val studentScheduleRetrofit: Retrofit
    get() = Retrofit.Builder()
        .baseUrl("http://sam.skhu.ac.kr")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

interface RetrofitService {
    @Headers(
        "content-type: application/json;charset=UTF-8",
        "X-Requested-With: XMLHttpRequest"
    )
    @POST("/SSE/SSEAD/SSEAD03_GetList")
    fun getStudentScheduleList(
        @Body haggi: Haggi,
        @Header("RequestVerificationToken") token: String,
        @Header("Cookie") cookie: String
    ): Call<ScheduleResponse>

    @Headers(
        "content-type: application/json;charset=UTF-8",
        "X-Requested-With: XMLHttpRequest"
    )
    @POST("/SSE/SSEAD/GetYyHaggi")
    fun getYyHaggi(
        @Header("RequestVerificationToken") token: String,
        @Header("Cookie") cookie: String
    ): Call<CurrentHaggiResponse>

    @Headers(
        "content-type: application/json;charset=UTF-8",
        "X-Requested-With: XMLHttpRequest"
    )
    @POST("/SSE/SSEAD/SSEAD01_GetList")
    fun getFacilityScheduleList(
        @Body targetFacility: TargetFacility,
        @Header("RequestVerificationToken") token: String,
        @Header("Cookie") cookie: String
    ): Call<ScheduleResponse>
}
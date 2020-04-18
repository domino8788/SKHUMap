package com.domino.skhumap

import com.naver.maps.map.NaverMap

object MapManager {
    enum class Type(val id:Int, val icon:Int){
        DEPARTMENT(0, R.drawable.ic_apartment_black_24dp)
    }
    lateinit var naverMap: NaverMap

}
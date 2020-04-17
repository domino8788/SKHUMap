package com.domino.skhumap

import com.domino.skhumap.MapManager.naverMap
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker

data class Department(val id:String="", val name:String="", val location:GeoPoint?=null, val type:DocumentReference?=null,
                      val marker:Marker?=Marker())
{
    fun addMarker(){
        marker?.apply {
            captionText = "$id  $name"
            position = LatLng(location!!.latitude, location!!.longitude)
            map = naverMap }
    }

    fun removeMarker(){
        marker?.map = null
    }
}
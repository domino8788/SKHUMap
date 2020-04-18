package com.domino.skhumap

import com.domino.skhumap.MapManager.naverMap
import com.google.firebase.firestore.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

@IgnoreExtraProperties
data class Department(
    @DocumentId val id:String="",
    @PropertyName("name") val name:String="",
    @PropertyName("location") val location:GeoPoint?=null,
    @PropertyName("type") val type:Int=0,
    @PropertyName("info") val info:HashMap<String, Any>?=null)
{
    val marker:Marker=Marker()
    @Exclude
    get

    fun addMarker(){
        marker.apply {
            captionText = "$id  $name"
            position = LatLng(location!!.latitude, location!!.longitude)
            icon = OverlayImage.fromResource(
                when(type){
                    MapManager.Type.DEPARTMENT.id -> MapManager.Type.DEPARTMENT.icon
                    else -> MapManager.Type.DEPARTMENT.icon
                }
            )
            map = naverMap
            isHideCollidedSymbols = true
        }
    }

    fun removeMarker(){
        marker?.map = null
    }
}
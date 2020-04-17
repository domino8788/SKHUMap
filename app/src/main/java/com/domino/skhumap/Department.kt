package com.domino.skhumap

import com.domino.skhumap.MapManager.naverMap
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker

data class Department(val id:String, val name:String, val location:GeoPoint)
{
}
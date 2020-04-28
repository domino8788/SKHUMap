package com.domino.skhumap.manager

import com.domino.skhumap.Facility
import com.domino.skhumap.R
import com.domino.skhumap.db.FirestoreHelper
import com.google.firebase.firestore.CollectionReference
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.GroundOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlin.math.abs

object MapManager {
    lateinit var naverMap: NaverMap
    private val campusGroudOverlay by lazy { GroundOverlay() }
    var facilities: MutableList<Facility>? = null

    var selectedDepartment: Facility? = null
        set(department) {
            field = department
        }

    var selectedFloor: Int? = null
        set(floor) {
            field = floor
        }

    fun getFloorName(floorNumber: Int): String =
        "${if (floorNumber > 0) "f${floorNumber}" else "b${abs(floorNumber)}"}"


    var mapMode: MODE = MODE.CAMPUS
        set(mode) {
            when (mode) {
                MODE.CAMPUS -> {
                    /* 지도 기본 설정 */
                    naverMap.run {
                        extent = LatLngBounds(
                            LatLng(37.486033, 126.823969),
                            LatLng(37.489835, 126.827264)
                        )
                        minZoom = 16.5
                        mapType = NaverMap.MapType.Basic
                        uiSettings.run {
                            isCompassEnabled = false
                            isZoomControlEnabled = false
                            isScaleBarEnabled = false
                            isRotateGesturesEnabled = false
                            isTiltGesturesEnabled = false
                        }
                        cameraPosition =
                            CameraPosition(LatLng(37.487600, 126.825643), 16.5, 0.0, 67.5)
                    }
                    /* 캠퍼스 지도 오버레이 */
                    campusGroudOverlay.run {
                        image = OverlayImage.fromResource(R.drawable.campus)
                        bounds = LatLngBounds(
                            LatLng(37.486427880037326, 126.82376783058442),
                            LatLng(37.48854961512034, 126.82754956984687)
                        )
                        alpha = 0.45f
                        map = naverMap
                    }
                    /* 마커 표시 */
                    DisplayMarker(FirestoreHelper.campusReference)
                }
                MODE.INDOOR -> {

                }
            }
            field = mode
        }

    enum class MODE(val id: Int) {
        CAMPUS(0),
        INDOOR(1)
    }

    fun DisplayMarker(target: CollectionReference) {
        removeMarkers()
        FirestoreHelper.queryPullDriven(target, ::makeMarkers)
    }

    fun makeMarkers(facilities:MutableList<Facility>) {
        this.facilities = facilities
            facilities.forEach { facility ->
                facility.run {
                    marker = Marker().apply {

                    captionText = "$id  $name"
                    position = LatLng(location!!.latitude, location!!.longitude)
                    icon = OverlayImage.fromResource(
                        when (type) {
                            Facility.TYPE.DEPARTMENT.id -> Facility.TYPE.DEPARTMENT.icon
                            else -> Facility.TYPE.DEPARTMENT.icon
                        }
                    )
                    isHideCollidedSymbols = true
                    map = naverMap
                }
            }
        }
    }

    fun removeMarkers() {
        facilities?.let{
            for(facility in it){
                facility.marker!!.map = null
            }
        }
    }
}

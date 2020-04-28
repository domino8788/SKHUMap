package com.domino.skhumap.manager

import android.graphics.Color
import android.view.View
import com.domino.skhumap.Facility
import com.domino.skhumap.R
import com.domino.skhumap.activity.MainActivity
import com.domino.skhumap.db.FirestoreHelper
import com.google.firebase.firestore.CollectionReference
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.GroundOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs

object MapManager {
    lateinit var naverMap: NaverMap
    private val indoorBenchMarkSouthWest = LatLng(37.487033, 126.823269)
    private val indoorBenchMarkNorthEast = LatLng(37.48970638628553, 126.82803403021207)
    private val groundOverlay by lazy { GroundOverlay() }
    val facilities: MutableList<Facility> = mutableListOf()
    lateinit var floorList: MutableList<Pair<String, Int>>

    val resourceId: Int
        get() = MainActivity.context.resources.getIdentifier(
            "d${selectedDepartment!!.id}_${getFloorName(selectedFloor!!)}",
            "drawable",
            MainActivity.context.packageName
        )

    enum class MODE(val id: Int) {
        CAMPUS(0),
        INDOOR(1)
    }

    fun init(){
        naverMap.run {
            extent = LatLngBounds(
                LatLng(37.486033, 126.823969),
                LatLng(37.489835, 126.827264)
            )
            minZoom = 16.5
            uiSettings.run {
                isCompassEnabled = false
                isZoomControlEnabled = false
                isScaleBarEnabled = false
                isRotateGesturesEnabled = false
                isTiltGesturesEnabled = false
            }
            backgroundColor = Color.WHITE
        }
        mapMode = MapManager.MODE.CAMPUS
    }

    var mapMode: MODE = MODE.CAMPUS
        set(mode) {
            when (mode) {
                MODE.CAMPUS -> {
                    /* 지도 기본 설정 */
                    naverMap.run {
                        mapType = NaverMap.MapType.Basic
                        cameraPosition =
                            CameraPosition(LatLng(37.487600, 126.825643), 16.5, 0.0, 67.5)
                    }
                    /* 캠퍼스 지도 오버레이 */
                    groundOverlay.run {
                        image = OverlayImage.fromResource(R.drawable.campus)
                        bounds = LatLngBounds(
                            LatLng(37.486427880037326, 126.82376783058442),
                            LatLng(37.48854961512034, 126.82754956984687)
                        )
                        alpha = 0.45f
                    }
                    /* 마커 표시 */
                    DisplayMarker(FirestoreHelper.campusReference)
                }
                MODE.INDOOR -> {
                    /* 지도 기본 설정 */
                    naverMap.run {
                        mapType = NaverMap.MapType.None
                        naverMap.cameraPosition =
                            CameraPosition(LatLng(37.487600, 126.825643), 16.5)
                    }
                    /* 실내 지도 오버레이 */
                    groundOverlay.apply {
                        image = OverlayImage.fromResource(resourceId)
                        bounds = LatLngBounds(
                            indoorBenchMarkSouthWest,
                            indoorBenchMarkNorthEast
                        )
                    }
                    /* 마커 표시 */
                    DisplayMarker(
                        FirestoreHelper.departmentReference(
                            selectedDepartment!!.id,
                            selectedFloor!!
                        )
                    )
                }
            }
            groundOverlay.map = naverMap
            field = mode
        }

    fun levelPickerRenew(facility: Facility) {
        MainActivity.appCompatActivity.indoor_level_picker.run {
            value = 0
            val minFloor = (facility.info!!["minFloor"] as Long).toInt()
            val maxFloor = (facility.info!!["maxFloor"] as Long).toInt()

            floorList = getFloorList(minFloor, maxFloor)
            minValue = 0
            value = abs(minFloor) + if (minFloor > 0) -1 else 0

            if (floorList.size> maxValue){
                displayedValues =  floorList.map { it -> it.first }.toTypedArray()
                maxValue = floorList.size - 1
            }else{
                maxValue = floorList.size - 1
                displayedValues =  floorList.map { it -> it.first }.toTypedArray()
            }
            visibility = View.VISIBLE
        }
    }


    var selectedDepartment: Facility? = null
        set(department) {
            field = department
            department?.let {
                levelPickerRenew(it)
//                naverMap.cameraPosition = CameraPosition(
//                    LatLng(it.location!!.latitude, it.location!!.longitude), defaultZoom, 0.0, defaultCampusImageBearing
//                )
                selectedFloor = 1
            }
        }

    var selectedFloor: Int? = null
        set(floor) {
            field = floor
            if (floor == null) {
                MainActivity.appCompatActivity.indoor_level_picker.visibility = View.GONE
                mapMode = MODE.CAMPUS
            } else
                mapMode = MODE.INDOOR
        }

    fun getFloorName(floorNumber: Int): String =
        "${if (floorNumber > 0) "f${floorNumber}" else "b${abs(floorNumber)}"}"

    fun getFloorList(minFloor: Int, maxFloor: Int): MutableList<Pair<String, Int>> =
        mutableListOf<Pair<String, Int>>().apply {
            (minFloor..maxFloor).forEach { add(getFloorName(it) to it) }
        }


    fun DisplayMarker(target: CollectionReference) {
        removeMarkers()
        FirestoreHelper.realTimeUpdate(target, ::makeMarkers)
    }

    fun makeMarkers(facilities: MutableList<Facility>) {
        this.facilities.addAll(facilities)
        facilities.forEach { facility ->
            facility.run {
                marker = Marker().apply {
                    captionText = "$id  $name"
                    position = LatLng(location!!.latitude, location!!.longitude)
                    icon = OverlayImage.fromResource(
                        Facility.TYPE.values().find{ TYPE -> TYPE.id == type }?.let { it.icon }?:R.drawable.ic_meeting_room_24px
                    )
                    setOnClickListener {
                        if (mapMode == MODE.CAMPUS)
                            selectedDepartment = facility
                        false
                    }
                    isHideCollidedSymbols = true
                    map = naverMap
                }
            }
        }
    }

    fun removeMarkers() {
        facilities.run {
            for (facility in this) {
                facility.marker!!.map = null
            }
            clear()
        }
    }
}

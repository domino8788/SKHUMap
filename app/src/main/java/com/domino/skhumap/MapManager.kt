package com.domino.skhumap.manager

import android.content.Intent
import android.view.View
import com.domino.skhumap.AddCampusDataActivity
import com.domino.skhumap.Facility
import com.domino.skhumap.MainActivity
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
import kotlinx.android.synthetic.main.fragment_map.*
import kotlin.math.abs

object MapManager {
    lateinit var naverMap: NaverMap
    private val indoorBenchMark = LatLng(37.487033, 126.823269)
    private val defaultZoom = 18.5
    private val defaultCampusImageBearing = 67.5
    lateinit var floorList: MutableList<Pair<String, Int>>

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

    fun levelPickerRenew(facility: Facility) {
        MainActivity.appCompatActivity.indoor_level_picker.run {
            value = 0
            val minFloor = (facility.info!!["minFloor"] as Long).toInt()
            val maxFloor = (facility.info!!["maxFloor"] as Long).toInt()

            floorList = mutableListOf<Pair<String, Int>>().apply {
                for (floorNumber in minFloor..maxFloor) {
                    if (floorNumber < 0)
                        this.add("B${abs(floorNumber)}" to floorNumber)
                    else if (floorNumber > 0)
                        this.add("F${abs(floorNumber)}" to floorNumber)
                }
            }
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

    var selectedFloor: Int? = null
        set(floor) {
            field = floor
            if (floor == null) {
                MainActivity.appCompatActivity.indoor_level_picker.visibility = View.GONE
                mapMode = MODE.CAMPUS
            } else
                mapMode = MODE.INDOOR
        }

    private val campusGroudOverlay by lazy {
        GroundOverlay().apply {
            image = OverlayImage.fromResource(R.drawable.campus)
            bounds = LatLngBounds(
                LatLng(37.486427880037326, 126.82376783058442),
                LatLng(37.48854961512034, 126.82754956984687)
            )
            alpha = 0.45f
        }
    }
    private val indoorGroundOverlay by lazy {
        GroundOverlay()
    }

    val resourceId: Int
        get() {
            return MainActivity.context.resources.getIdentifier(
                "d${selectedDepartment!!.id}_${if (selectedFloor!! > 0) "f${selectedFloor}" else "b${abs(
                    selectedFloor!!
                )}"}", "drawable", MainActivity.context.packageName
            )
        }

    val facilities = mutableListOf<Facility>()

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
                    campusGroudOverlay.map = naverMap
                    indoorGroundOverlay.map = null
                    /* 마커 표시 */
                    DisplayMarker(FirestoreHelper.campusReference)
                }
                MODE.INDOOR -> {
                    /* 지도 기본 설정 */
                    naverMap.run {
                        naverMap.cameraPosition =
                            CameraPosition(LatLng(37.487600, 126.825643), 16.5)
                        mapType = NaverMap.MapType.None
                    }
                    /* 실내 지도 오버레이 */
                    campusGroudOverlay.map = null
                    indoorGroundOverlay.apply {
                        image = OverlayImage.fromResource(resourceId)
                        bounds = LatLngBounds(
                            indoorBenchMark,
                            indoorBenchMark.offset(
                                image.getIntrinsicHeight(MainActivity.context).toDouble() / 10,
                                image.getIntrinsicWidth(MainActivity.context).toDouble() / 10
                            )
                        )
                        map = naverMap
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
            field = mode
        }

    enum class MODE(val id: Int) {
        CAMPUS(0),
        INDOOR(1)
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
                        when (type) {
                            Facility.TYPE.DEPARTMENT.id -> Facility.TYPE.DEPARTMENT.icon
                            else -> Facility.TYPE.DEPARTMENT.icon
                        }
                    )
                    setOnClickListener {
                        if (mapMode == MODE.CAMPUS) {
                            selectedDepartment = facility
                        } else {
                            MainActivity.appCompatActivity.startActivity(
                                Intent(
                                    MainActivity.context,
                                    AddCampusDataActivity::class.java
                                ).also { it.putExtra("selected", facility) })
                        }
                        false
                    }
                    isHideCollidedSymbols = true
                    map = naverMap
                }
            }
        }
    }

    fun removeMarkers() {
        facilities?.let {
            for (facility in it) {
                facility.marker!!.map = null
            }
        }
        facilities.clear()
    }
}

package com.domino.skhumap.manager

import android.graphics.Color
import android.view.View
import com.domino.skhumap.Facility
import com.domino.skhumap.R
import com.domino.skhumap.activity.MainActivity
import com.domino.skhumap.db.FirestoreHelper
import com.domino.skhumap.dto.SearchableFacility
import com.domino.skhumap.fragment.FacilityFragment
import com.google.firebase.firestore.CollectionReference
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.GroundOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_facility.*
import kotlin.math.abs

object MapManager {
    lateinit var naverMap: NaverMap
    private val indoorBenchMarkSouthWest = LatLng(37.487033, 126.823269)
    private val indoorBenchMarkNorthEast = LatLng(37.48970638628553, 126.82803403021207)
    private val groundOverlay by lazy { GroundOverlay() }
    private const val defaultZoom = 18.5
    private const val defaultCampusImageBearing = 67.5

    val facilities: MutableList<Facility> = mutableListOf()
    var floorList: MutableList<Pair<String, Int>> = mutableListOf()

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
                        bounds = LatLngBounds(indoorBenchMarkSouthWest, indoorBenchMarkNorthEast)
                    }
                    /* 마커 표시 */
                    DisplayMarker(
                        FirestoreHelper.departmentReference(selectedDepartment!!.id, selectedFloor!!)
                    )
                }
            }
            groundOverlay.map = naverMap
            field = mode
        }

    private fun levelPickerRenew(facility: Facility) {
        MainActivity.appCompatActivity.indoor_level_picker.run {
            val minFloor = (facility.info!!["minFloor"] as Long).toInt()
            val maxFloor = (facility.info!!["maxFloor"] as Long).toInt()

            floorList = getFloorList(minFloor, maxFloor)
            minValue = 0

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
        set(departmentId) {
            field = departmentId
            departmentId?.let {
                levelPickerRenew(selectedDepartment!!)
                selectedFloor = 1
            }
        }

    var selectedFloor: Int? = null
        set(floor) {
            field = floor
            if (floor == null) {
                MainActivity.appCompatActivity.indoor_level_picker.visibility = View.GONE
                mapMode = MODE.CAMPUS
            } else {
                MainActivity.appCompatActivity.indoor_level_picker.value = floorList.indexOfFirst { floor -> floor.second == field }
                mapMode = MODE.INDOOR
            }
        }

    fun back(){
        selectedDepartment = null
        selectedFloor = null
    }

    fun markMap(searchableFacility: SearchableFacility){
        back()
        selectedDepartment = searchableFacility.department
        selectedFloor = searchableFacility.floorNumber
        naverMap.cameraPosition = CameraPosition(searchableFacility.facility.latLng, defaultZoom, 0.0, if(mapMode == MODE.CAMPUS) defaultCampusImageBearing else 0.0)
    }

    fun getFloorName(floorNumber: Int): String = if (floorNumber > 0) "f${floorNumber}" else "b${abs(floorNumber)}"


    fun getFloorList(minFloor: Int, maxFloor: Int): MutableList<Pair<String, Int>> =
        mutableListOf<Pair<String, Int>>().apply {
            (minFloor..maxFloor).forEach { if(it!=0) add(getFloorName(it) to it) }
        }


    fun DisplayMarker(target: CollectionReference) {
        removeMarkers()
        FirestoreHelper.realTimeUpdate(target, ::makeMarkers)
    }

    fun makeMarkers(facility: Facility) {
        facility.run {
            facilities.add(this)
            marker = Marker().apply {
                captionText = "$id"
                subCaptionText = "${name.joinToString("\n","", "")}"
                position = LatLng(location!!.latitude, location!!.longitude)
                icon = OverlayImage.fromResource(resourceId)
                setOnClickListener {
                    if (mapMode == MODE.CAMPUS)
                        selectedDepartment = facility
                    else {
                        FacilityFragment.instance.run {
                            searchableFacilityList.add(SearchableFacility(selectedDepartment!!, selectedFloor!!, facility))
                            this?.list_facility?.let {
                                it.adapter?.notifyItemChanged((searchableFacilityList.size-1)/4)
                                list_facility.smoothScrollToPosition((searchableFacilityList.size-1)/4)
                            }
                        }
                    }
                    false
                }
                isHideCollidedSymbols = true
                map = naverMap
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

package com.domino.skhumap.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.domino.skhumap.Facility
import com.domino.skhumap.R
import com.domino.skhumap.model.FavoritesViewModel
import com.domino.skhumap.model.MainViewModel
import com.domino.skhumap.model.MapViewModel
import com.domino.skhumap.model.SearchViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.GroundOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.android.synthetic.main.fragment_map.*

class NaverMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap
    private val indoorBenchMarkSouthWest = LatLng(37.487033, 126.823269)
    private val indoorBenchMarkNorthEast = LatLng(37.48970638628553, 126.82803403021207)
    private val groundOverlay by lazy { GroundOverlay() }
    private val defaultZoom = 18.5
    private val defaultCampusImageBearing = 67.5
    private lateinit var mapViewModel:MapViewModel
    private lateinit var searchViewModel:SearchViewModel

    val resourceId: Int
        get() = resources.getIdentifier(
            "d${mapViewModel.getSelectedDepartmentId()}_${mapViewModel.getFloorName(mapViewModel.getSelectedFloorNumber()!!)}",
            "drawable",
            context?.packageName
        )

    enum class Mode(val id: Int) {
        CAMPUS(0),
        INDOOR(1)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (childFragmentManager.findFragmentById(R.id.map) as MapFragment?)?.getMapAsync(this)
    }

    @UiThread
    override fun onMapReady(nMap: NaverMap) {
        naverMap = nMap
        init()
    }

    private fun init(){
        mapViewModel = ViewModelProvider(requireActivity())[MapViewModel::class.java].apply {

            facilitiesLiveData.observe(requireActivity(), Observer {
                it.forEach { facility-> makeMarker(facility) }
            })

            selectedFloorLiveData.observe(requireActivity(), Observer {
                removeMarkers()
                it?.let {
                    indoor_level_picker.value = floorListLiveData.value!!.indexOfFirst { floor -> floor.second == it }
                    mapMode = Mode.INDOOR
                }?:let {
                    indoor_level_picker.visibility = View.GONE
                    mapMode = Mode.CAMPUS
                }
            })

            floorListLiveData.observe(requireActivity(),Observer { floorList ->
                indoor_level_picker.run {
                    if (floorList.size> maxValue){
                        displayedValues =  floorList.map { it -> it.first }.toTypedArray()
                        maxValue = floorList.size - 1
                    }else{
                        maxValue = floorList.size - 1
                        displayedValues =  floorList.map { it -> it.first }.toTypedArray()
                    }
                    visibility = View.VISIBLE
                    value = floorList.indexOfFirst { floor -> floor.second == 1 }
                }
            })

            markMapLivdeData.observe(requireActivity(), Observer { searchableFacility ->
                mapViewModel.run {
                    selectedDepartment = searchableFacility.department
                    setSelectedFloor(searchableFacility.floorNumber)
                }
                naverMap.cameraPosition = CameraPosition(searchableFacility.facility.latLng, defaultZoom, 0.0, if(mapMode == Mode.CAMPUS) defaultCampusImageBearing else 0.0)
            })
        }

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
        indoor_level_picker.run {
            setOnValueChangedListener { picker, oldVal, newVal ->
                mapViewModel.setSelectedFloor(mapViewModel.pickerValueToFloorNumber(newVal))
            }
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
        }

        searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java].apply {
            floating_search_view.run {
                setOnQueryChangeListener { oldQuery, newQuery ->
                    searchViewModel.queryText(newQuery)
                }
            }
        }

        mapViewModel.selectedDepartment = null
    }

    var mapMode: Mode = Mode.CAMPUS
        set(mode) {
            when (mode) {
                Mode.CAMPUS -> {
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
                }
                Mode.INDOOR -> {
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
                }
            }
            groundOverlay.map = naverMap
            field = mode
        }

    private fun makeMarker(facility: Facility) {
        facility.run {
            marker = Marker().apply {
                captionText = "$id"
                subCaptionText = "${name.joinToString("\n","", "")}"
                position = LatLng(location!!.latitude, location!!.longitude)
                icon = OverlayImage.fromResource(resourceId)
                setOnClickListener {
                    if (mapMode == Mode.CAMPUS) {
                        mapViewModel.selectedDepartment = facility
                    }
                    else {
                        val favoritesViewModel = activity?.run {
                            ViewModelProvider(this)[FavoritesViewModel::class.java]
                        }?:throw Exception("FavoritesViewModel not exists")
                        favoritesViewModel.insert(mapViewModel.getCurrentSelectToSearchableFacility(facility))
                    }
                    false
                }
                isHideCollidedSymbols = true
                map = naverMap
            }
        }
    }

    private fun removeMarkers() {
        mapViewModel.facilities.run {
            for (facility in this) {
                facility.marker?.map = null
            }
            clear()
        }
    }
}
package com.domino.skhumap.manager

import com.domino.skhumap.R
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.GroundOverlay
import com.naver.maps.map.overlay.OverlayImage

object MapManager {
    lateinit var naverMap: NaverMap
    private val campusGroudOverlay by lazy { GroundOverlay() }

    var mapMode: MODE = MODE.CAMPUS
        set(mode) {
            when (mode) {
                MODE.CAMPUS -> {
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
                    campusGroudOverlay.run {
                        image = OverlayImage.fromResource(R.drawable.campus)
                        bounds = LatLngBounds(
                            LatLng(37.486427880037326, 126.82376783058442),
                            LatLng(37.48854961512034, 126.82754956984687)
                        )
                        map = naverMap
                        alpha = 0.45f
                    }
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


}
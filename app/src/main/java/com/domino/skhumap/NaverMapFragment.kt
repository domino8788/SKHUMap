package com.domino.skhumap

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.GroundOverlay
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.android.synthetic.main.fragment_map.*
import java.lang.Exception

class NaverMapFragment : Fragment(), OnMapReadyCallback, NaverMap.OnMapClickListener {
    lateinit var naverMap:NaverMap
    val campusOverlay = GroundOverlay()
    var toggleStart = true
    var toggleEnd = false
    var firstOrSecond = true
    lateinit var southWestLatLng:LatLng
    lateinit var northEastLatLng:LatLng

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment?
        mapFragment?.getMapAsync(this)

        txt_current_touch_latlng
    }

    @UiThread
    override fun onMapReady(nMap: NaverMap) {
        naverMap = nMap
        naverMap.extent = LatLngBounds(LatLng(37.486033, 126.823969), LatLng(37.489835, 126.827264))
        naverMap.minZoom = 16.5

        naverMap.setOnMapClickListener(this)

    }
    fun makeImage(latLng1: LatLng, latLng2: LatLng) {
        try {
            campusOverlay.map = null
            campusOverlay.image = OverlayImage.fromResource(R.drawable.map_img2)
            campusOverlay.bounds = LatLngBounds(latLng1!!, latLng2!!)
            campusOverlay.alpha = 0.5f
            campusOverlay.map = naverMap
            txt_swne.text = "ll 1 : ${latLng1.toString()}    ll 2 : ${latLng2.toString()}"
            txt_count.text = "finish"
        } catch (e: Exception) {
            txt_count.text = "over!!"
        } finally {

        }
    }

    @UiThread
    override fun onMapClick(point: PointF, latlng: LatLng) {
        txt_current_touch_latlng.text = latlng.toString()
        if (toggleEnd) {
            makeImage(southWestLatLng, northEastLatLng)
            toggleEnd = false
        } else {
            if (firstOrSecond) {
                campusOverlay.map = null
                southWestLatLng = latlng
                txt_count.text = "1"
                firstOrSecond = false
            } else {
                firstOrSecond = true
                northEastLatLng = latlng
                toggleEnd = true
                txt_count.text = "2"
            }
        }
    }

}
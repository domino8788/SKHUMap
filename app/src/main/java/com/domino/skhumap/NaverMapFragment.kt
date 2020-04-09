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
    lateinit var naverMap: NaverMap
    val campusOverlay = GroundOverlay()
    var toggleStart = true
    var select = 0
    var distance = 0.000020
    var magnification = 12.5f
    lateinit var southWestLatLng: LatLng
    lateinit var northEastLatLng: LatLng

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

        ll_group.setOnCheckedChangeListener { group, checkedId ->
            select = when (checkedId) {
                R.id.ll1_latitude -> 0
                R.id.ll1_longtitude -> 1
                else -> 0
            }
        }
        switch_on_off.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleStart = isChecked
        }
        btn_up.setOnClickListener {
            makeImage(
                campusOverlay.bounds.run {
                    when (select) {
                        0 -> LatLng(southWest.latitude + distance, southWest.longitude)
                        1 -> LatLng(southWest.latitude, southWest.longitude + distance)
                        else -> LatLng(southWest.latitude + distance, southWest.longitude)
                    }
                },magnification
            )
        }
        btn_down.setOnClickListener {
            makeImage(
                campusOverlay.bounds.run {
                    when (select) {
                        0 -> LatLng(southWest.latitude - distance, southWest.longitude)
                        1 -> LatLng(southWest.latitude, southWest.longitude - distance)
                        else -> LatLng(southWest.latitude - distance, southWest.longitude)
                    }
                }, magnification
            )
        }
        btn_magnification_up.setOnClickListener {
            btn_magnification_up.text = "mag_up ${magnification}"
            magnification+=0.05f
            makeImage(campusOverlay.bounds.southWest, magnification)
        }
        btn_magnification_down.setOnClickListener {
            btn_magnification_down.text = "mag_down ${magnification}"
            magnification-=0.05f
            makeImage(campusOverlay.bounds.southWest, magnification)
        }
    }

    @UiThread
    override fun onMapReady(nMap: NaverMap) {
        naverMap = nMap
        naverMap.extent = LatLngBounds(LatLng(37.486033, 126.823969), LatLng(37.489835, 126.827264))
        naverMap.minZoom = 16.5

        naverMap.setOnMapClickListener(this)

    }

    fun makeImage(southWestLatLng: LatLng, magnification:Float) {
        campusOverlay.map = null
        campusOverlay.image = OverlayImage.fromResource(R.drawable.campus)

        northEastLatLng = southWestLatLng.offset(
            campusOverlay.image.getIntrinsicHeight(context!!).toDouble()/magnification,
            campusOverlay.image.getIntrinsicWidth(context!!).toDouble()/magnification
            )

        campusOverlay.bounds = LatLngBounds(southWestLatLng, northEastLatLng)
        campusOverlay.alpha = 0.5f
        campusOverlay.map = naverMap
        txt_sw.text = "ll 1 : ${southWestLatLng.toString()}    ll 2 : ${northEastLatLng.toString()}"
    }

    @UiThread
    override fun onMapClick(point: PointF, latlng: LatLng) {
        if(toggleStart) {
            txt_sw.text = latlng.toString()
            southWestLatLng = latlng

            makeImage(southWestLatLng, magnification)
        }
    }

}
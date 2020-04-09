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
    var toggleEnd = false
    var firstOrSecond = true
    var select = 0
    var distance = 0.000020
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
                R.id.ll2_latitude -> 2
                R.id.ll2_longtitude -> 3
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
            },
                campusOverlay.bounds.run {
                    when (select) {
                        2 -> LatLng(northEast.latitude + distance, northEast.longitude)
                        3 -> LatLng(northEast.latitude, northEast.longitude + distance)
                        else -> LatLng(northEast.latitude + distance, northEast.longitude)
                    }
                }
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
                },
                campusOverlay.bounds.run {
                    when (select) {
                        2 -> LatLng(northEast.latitude - distance, northEast.longitude)
                        3 -> LatLng(northEast.latitude, northEast.longitude - distance)
                        else -> LatLng(
                            northEast.latitude - distance,
                            northEast.longitude
                        )
                    }
                }
            )
        }
    }

    @UiThread
    override fun onMapReady(nMap: NaverMap) {
        naverMap = nMap
        naverMap.extent = LatLngBounds(LatLng(37.486033, 126.823969), LatLng(37.489835, 126.827264))
        naverMap.minZoom = 16.5

        naverMap.setOnMapClickListener(this)

    }

    fun makeImage(southWestLatLng: LatLng, northEastLatLng: LatLng) {
        try {
            campusOverlay.map = null
            campusOverlay.image = OverlayImage.fromResource(R.drawable.map_img2)
            campusOverlay.bounds = LatLngBounds(southWestLatLng!!, northEastLatLng!!)
            campusOverlay.alpha = 0.5f
            campusOverlay.map = naverMap
            txt_swne.text =
                "ll 1 : ${southWestLatLng.toString()}    ll 2 : ${northEastLatLng.toString()}"
            txt_count.text = "finish"
        } catch (e: Exception) {
            txt_count.text = "over!!"
        } finally {

        }
    }

    @UiThread
    override fun onMapClick(point: PointF, latlng: LatLng) {
        txt_current_touch_latlng.text = latlng.toString()
        if (!toggleStart)
            return
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
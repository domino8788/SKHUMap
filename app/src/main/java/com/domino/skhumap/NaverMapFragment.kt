package com.domino.skhumap

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
import kotlinx.android.synthetic.main.fragment_map.*

class NaverMapFragment : Fragment(), OnMapReadyCallback, NaverMap.OnMapClickListener {
    lateinit var naverMap:NaverMap

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

    @UiThread
    override fun onMapClick(point: PointF, latlng: LatLng) {
        txt_current_touch_latlng.text = latlng.toString()
    }

}
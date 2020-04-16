package com.domino.skhumap

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.UiThread
import com.google.firebase.FirebaseApp
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.GroundOverlay
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.android.synthetic.main.fragment_map.*

const val REQUEST_CODE = 1000

class NaverMapFragment : Fragment(), OnMapReadyCallback, NaverMap.OnMapClickListener {

    lateinit var naverMap:NaverMap
    val campusGroudOverlay by lazy { GroundOverlay() }
    val markers by lazy { arrayListOf<Marker>() }
    val currentTouchMarker by lazy { Marker() }

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
        btn_open_add_data_activity.setOnClickListener {
            startActivity(Intent(context, AddCampusDataActivity::class.java).apply {
                putExtra("location", currentTouchMarker.position)
            })
        }
    }



    @UiThread
    override fun onMapReady(nMap: NaverMap) {
        this.naverMap = nMap
        naverMap.run {
            extent = LatLngBounds(LatLng(37.486033, 126.823969), LatLng(37.489835, 126.827264))
            minZoom = 16.5
            mapType = NaverMap.MapType.Terrain
            uiSettings.run {
                isCompassEnabled = false
                isZoomControlEnabled = false
                isScaleBarEnabled = false
                isRotateGesturesEnabled = false
                isTiltGesturesEnabled = false
            }
            cameraPosition = CameraPosition(LatLng(37.487600, 126.825643), 16.5, 0.0, 67.5)
        }
        campusGroudOverlay.run {
            image = OverlayImage.fromResource(R.drawable.campus)
            bounds = LatLngBounds(LatLng(37.486427880037326, 126.82376783058442), LatLng(37.48854961512034, 126.82754956984687))
            map = nMap
        }

        naverMap.setOnMapClickListener(this)
    }

    override fun onMapClick(point: PointF, latlng: LatLng) {
        currentTouchMarker.apply {
            position = latlng
            map = naverMap
        }
    }

}
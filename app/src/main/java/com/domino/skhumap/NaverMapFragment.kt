package com.domino.skhumap

import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.domino.skhumap.manager.MapManager
import com.domino.skhumap.manager.MapManager.naverMap
import com.google.firebase.firestore.FirebaseFirestore
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.GroundOverlay
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.fragment_map.*

class NaverMapFragment : Fragment(), OnMapReadyCallback, NaverMap.OnMapClickListener {

    val db by lazy { FirebaseFirestore.getInstance() }
    val facilities by lazy { arrayListOf<Facility>() }
    val campusGroudOverlay by lazy { GroundOverlay() }
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
        MapManager.run {
            naverMap = nMap.apply {
                backgroundColor = Color.WHITE
                onMapClickListener = this@NaverMapFragment
            }
            mapMode = MapManager.MODE.CAMPUS
        }
    }

    override fun onMapClick(point: PointF, latlng: LatLng) {
        currentTouchMarker.apply {
            position = latlng
            map = naverMap
        }
    }

}
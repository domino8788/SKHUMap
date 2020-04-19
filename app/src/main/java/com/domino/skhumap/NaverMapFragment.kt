package com.domino.skhumap

import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.domino.skhumap.MapManager.naverMap
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
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


    fun realTimeMarkerUpdate() {
        db.collection(MapManager.collectionName).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            for(change in querySnapshot?.documentChanges!!){
                change.run {
                    when (type) {
                        DocumentChange.Type.ADDED -> {
                            facilities.add(newIndex, document.toDto(Facility::class.java))
                        }
                        DocumentChange.Type.MODIFIED -> {
                            facilities[oldIndex].removeMarker()
                            facilities[newIndex] = document.toDto(Facility::class.java)
                        }
                        DocumentChange.Type.REMOVED -> {
                            facilities[oldIndex].removeMarker()
                            facilities.removeAt(oldIndex)
                        }
                    }
                }
            }
        }
    }

    private fun <T> QueryDocumentSnapshot.toDto(valueType:Class<T>): T =
        when(valueType) {
            Facility::class.java -> this.toObject(valueType).apply {
                addMarker()?.setOnClickListener {
                    this@NaverMapFragment.startActivity(Intent(context, AddCampusDataActivity::class.java).also {
                        it.putExtra("selected", this)
                    })
                    false
                }
            } as T
            else -> this.toObject(valueType)
        }

    @UiThread
    override fun onMapReady(nMap: NaverMap) {
        naverMap = nMap
        naverMap.run {
            extent = LatLngBounds(LatLng(37.486033, 126.823969), LatLng(37.489835, 126.827264))
            minZoom = 16.5
            mapType = NaverMap.MapType.Basic
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
            alpha = 0.45f
            map = nMap
        }
        realTimeMarkerUpdate()
        naverMap.onMapClickListener = this
    }

    override fun onMapClick(point: PointF, latlng: LatLng) {
        currentTouchMarker.apply {
            position = latlng
            map = naverMap
        }
    }

}
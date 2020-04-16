package com.domino.skhumap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.naver.maps.geometry.LatLng
import kotlinx.android.synthetic.main.activity_add_campus_data.*
import kotlinx.android.synthetic.main.fragment_map.*

class AddCampusDataActivity : AppCompatActivity() {

    lateinit var location : LatLng
    val db by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_campus_data)
        location = intent.extras!!.get("location") as LatLng

        btn_data_push.setOnClickListener {
            db.collection("department").document(txt_id.text.toString())
                .set(hashMapOf(
                    "name" to txt_name,
                    "location" to GeoPoint(location.latitude, location.longitude)
                ))
        }
    }


}

package com.domino.skhumap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
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
                .set(Department(txt_id.text.toString() ,
                    txt_name.text.toString(), GeoPoint(location.latitude, location.longitude),
                    MapManager.Type.valueOf(spinner_type.selectedItem.toString()).id,
                    hashMapOf()))
                .addOnSuccessListener {
                    Log.d("Firebase : ", "DocumentSnapshot successfully written!")
                    Toast.makeText(this, "문서 추가 성공", Toast.LENGTH_SHORT)
                }
                .addOnFailureListener {
                        e -> Log.w("Firebase : ", "Error writing document", e)
                    Toast.makeText(this, "문서 추가 실패", Toast.LENGTH_SHORT)
                }
            finish()
        }
        spinner_type.run {
            val list = MapManager.Type.values().map { it.toString() }
            adapter = ArrayAdapter(this@AddCampusDataActivity, android.R.layout.simple_spinner_dropdown_item, list)
        }
    }


}

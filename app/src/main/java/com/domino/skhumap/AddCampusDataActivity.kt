package com.domino.skhumap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.naver.maps.geometry.LatLng
import kotlinx.android.synthetic.main.activity_add_campus_data.*

class AddCampusDataActivity : AppCompatActivity() {

    var location : LatLng? = null
    var selectedData: Facility? = null
    val db by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_campus_data)
        location = intent.extras?.get("location") as? LatLng
        (intent.extras?.get("selected") as? Facility)?.apply {
            setValue(this)
            txt_id.isEnabled = false
            /* 수정 */
            btn_data_push.setOnClickListener {
                update(id)
                finish()
            }

            /* 삭제 버튼과 삭제  */
            layout.addView(Button(applicationContext).also {
                it.text = "delete"
                it.setOnClickListener {
                    delete(id)
                    finish()
                }
            })
        }?:
            /* 추가 */
        btn_data_push.setOnClickListener {
            insert()
            finish()
        }

        spinner_type.run {
            val list = MapManager.Type.values().map { it.toString() }
            adapter = ArrayAdapter(this@AddCampusDataActivity, android.R.layout.simple_spinner_dropdown_item, list)
        }
    }

    private fun insert(){
        db.collection(MapManager.collectionName).document(txt_id.text.toString())
            .set(
                Facility(
                    txt_id.text.toString() ,
                    txt_name.text.toString(), GeoPoint(location!!.latitude, location!!.longitude),
                    MapManager.Type.valueOf(spinner_type.selectedItem.toString()).id,
                    hashMapOf()
                )
            )
            .addOnSuccessListener {
                Log.d("Firebase : ", "DocumentSnapshot successfully written!")
                Toast.makeText(this, "문서 추가 성공", Toast.LENGTH_SHORT)
            }
            .addOnFailureListener {
                    e -> Log.w("Firebase : ", "Error writing document", e)
                Toast.makeText(this, "문서 추가 실패", Toast.LENGTH_SHORT)
            }
    }

    private fun update(id:String){
        db.collection(MapManager.collectionName).document(id)
            .update(
                    mapOf("name" to txt_name.text.toString(),
                    "type" to MapManager.Type.valueOf(spinner_type.selectedItem.toString()).id)
            )
    }

    private fun delete(id:String){
        db.collection(MapManager.collectionName).document(id).delete()
    }

    private fun setValue(facility: Facility){
        facility.run {
            txt_id.setText(id)
            txt_name.setText(name)
            spinner_type.setSelection(type)
        }
    }
}

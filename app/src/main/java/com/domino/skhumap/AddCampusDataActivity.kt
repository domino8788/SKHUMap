package com.domino.skhumap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.domino.skhumap.db.FirestoreHelper
import com.domino.skhumap.manager.MapManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.naver.maps.geometry.LatLng
import kotlinx.android.synthetic.main.activity_add_campus_data.*
import kotlinx.android.synthetic.main.activity_add_campus_data.layout

class AddCampusDataActivity : AppCompatActivity() {

    var location: LatLng? = null
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
                FirestoreHelper.update(
                    MapManager.selectedFloor?.let{
                        FirestoreHelper.departmentReference(MapManager.selectedDepartment!!.id, MapManager.selectedFloor!!)
                    }?:FirestoreHelper.campusReference, id, viewToMap())
                finish()
            }

            /* 삭제 버튼과 삭제  */
            if(MapManager.mapMode != MapManager.MODE.CAMPUS){
            layout.addView(Button(applicationContext).also {
                it.text = "delete"
                it.setOnClickListener {
                    FirestoreHelper.delete(FirestoreHelper.departmentReference(MapManager.selectedDepartment!!.id, MapManager.selectedFloor!!), id)
                    finish()
                }
            })
            }
        } ?:
        /* 추가 */
        btn_data_push.setOnClickListener {
            FirestoreHelper.insert(
                MapManager.selectedFloor?.let{
                    FirestoreHelper.departmentReference(MapManager.selectedDepartment!!.id, MapManager.selectedFloor!!)
                }?:FirestoreHelper.campusReference, viewToDTO())
            finish()
        }

        spinner_type.run {
            val list = Facility.TYPE.values().map { it.toString() }
            adapter = ArrayAdapter(
                this@AddCampusDataActivity,
                android.R.layout.simple_spinner_dropdown_item,
                list
            )
            onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    facility_min_floor.visibility = View.GONE
                    facility_max_floor.visibility = View.GONE
                    when(position){
                        Facility.TYPE.DEPARTMENT.id -> {
                            facility_min_floor.visibility = View.VISIBLE
                            facility_max_floor.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    facility_min_floor.visibility = View.VISIBLE
                    facility_max_floor.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun viewToDTO():Facility = Facility(
            txt_id.text.toString(),
            txt_name.text.toString().split(','),
        GeoPoint(location!!.latitude, location!!.longitude),
            Facility.TYPE.valueOf(spinner_type.selectedItem.toString()).id,
            when (spinner_type.selectedItemPosition) {
                Facility.TYPE.DEPARTMENT.id -> hashMapOf<String, Any>(
                    "minFloor" to txt_min_floor.text.toString().toInt(),
                    "maxFloor" to txt_max_floor.text.toString().toInt()
                )
                else -> hashMapOf()
            }
        )

    private fun viewToMap():Map<String, Any> = mapOf(
        "name" to txt_name.text.toString().split(','),
        "type" to Facility.TYPE.valueOf(spinner_type.selectedItem.toString()).id,
        "info" to when (spinner_type.selectedItemPosition) {
            Facility.TYPE.DEPARTMENT.id -> hashMapOf(
                "minFloor" to txt_min_floor.text.toString().toInt(),
                "maxFloor" to txt_max_floor.text.toString().toInt()
            )
            else -> hashMapOf()
        }
    )

    private fun setValue(facility: Facility) {
        facility.run {
            txt_id.setText(id)
            txt_name.setText(name!!.joinToString(",","",""))
            when(type){
                Facility.TYPE.DEPARTMENT.id -> {
                    txt_min_floor.setText((info?.get("minFloor") as? Long)?.toString())
                    txt_max_floor.setText((info?.get("maxFloor") as? Long)?.toString())
                }
            }
            spinner_type.setSelection(type)
        }
    }
}

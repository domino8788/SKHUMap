package com.domino.skhumap.db

import android.content.Context
import com.domino.skhumap.Facility
import com.domino.skhumap.manager.MapManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.naver.maps.map.overlay.Marker

object FirestoreHelper {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private const val COLLECTION_FACILITIES = "facilities"

    val campusReference by lazy { db.collection(COLLECTION_FACILITIES) }
    fun departmentReference(departmentId:String, floor:Int): CollectionReference = campusReference.document(departmentId).collection(floor.toString())

    fun queryPullDriven(queryTarget: CollectionReference, callback: ((facilities:MutableList<Facility>) -> Unit)?) {
        queryTarget.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                MapManager.run {
                    task.result!!.toObjects(Facility::class.java).also { (callback)?.let { func -> func(it) } }
                }
            }
        }
    }

    fun init(context:Context){
        FirebaseApp.initializeApp(context)
    }

}
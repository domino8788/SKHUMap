package com.domino.skhumap.db

import android.content.Context
import com.domino.skhumap.Facility
import com.domino.skhumap.manager.MapManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.naver.maps.map.overlay.Marker

object FirestoreHelper {
    private var realTimeUpdateListener: ListenerRegistration? = null

    fun realTimeUpdate(queryTarget: CollectionReference, callback: ((facilities:MutableList<Facility>) -> Unit)?) {
        realTimeUpdateListener?.remove()
        realTimeUpdateListener = queryTarget.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            for(change in querySnapshot?.documentChanges!!){
                change.run {
                    when (type) {
                        DocumentChange.Type.ADDED -> {
                            MapManager.facilities.add(newIndex, document.toObject(Facility::class.java).also { (callback)?.let { func -> func(
                                mutableListOf(it)) } })
                        }
                        DocumentChange.Type.MODIFIED -> {
                            MapManager.facilities[oldIndex].marker?.map = null
                            MapManager.facilities[newIndex] = document.toObject(Facility::class.java).also { (callback)?.let { func -> func(
                                mutableListOf(it)) } }
                        }
                        DocumentChange.Type.REMOVED -> {
                            MapManager.facilities[oldIndex].removeMarker()
                            MapManager.facilities.removeAt(oldIndex)
                        }
                    }
                }
            }
        }
    }

    private val db by lazy { FirebaseFirestore.getInstance() }
    private const val COLLECTION_FACILITIES = "facilities"

    val campusReference by lazy { db.collection(COLLECTION_FACILITIES) }
    fun departmentReference(departmentId:String, floor:Int): CollectionReference = campusReference.document(departmentId).collection(floor.toString())

    fun init(context:Context){
        FirebaseApp.initializeApp(context)
    }

}
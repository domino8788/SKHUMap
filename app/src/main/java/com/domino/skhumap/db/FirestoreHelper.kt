package com.domino.skhumap.db

import android.content.Context
import com.domino.skhumap.Facility
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object FirestoreHelper {
    private var realTimeUpdateListener: ListenerRegistration? = null

    fun realTimeUpdate(facilities:MutableList<Facility> ,queryTarget: CollectionReference ,callback: (() -> Unit)?) {
        realTimeUpdateListener?.remove()
        realTimeUpdateListener = queryTarget.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            for(change in querySnapshot?.documentChanges!!){
                change.run {
                    when (type) {
                        DocumentChange.Type.ADDED -> {
                            facilities.add(newIndex, document.toObject(Facility::class.java))
                        }
                        DocumentChange.Type.MODIFIED -> {
                            facilities[oldIndex].marker?.map = null
                            facilities[newIndex] = document.toObject(Facility::class.java)
                        }
                        DocumentChange.Type.REMOVED -> {
                            facilities[oldIndex].removeMarker()
                            facilities.removeAt(oldIndex)
                        }
                    }
                }
            }
            (callback)?.let { func -> func() }
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
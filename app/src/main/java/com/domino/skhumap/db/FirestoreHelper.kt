package com.domino.skhumap.db

import android.util.Log
import android.widget.Toast
import com.domino.skhumap.Facility
import com.domino.skhumap.MainActivity
import com.domino.skhumap.manager.MapManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*

object FirestoreHelper {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private const val COLLECTION_FACILITIES = "facilities"
    private var realTimeUpdateListener:ListenerRegistration? = null

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

    fun init(){
        FirebaseApp.initializeApp(MainActivity.context)
    }

    fun insert(reference: CollectionReference, facility:Facility) {
        reference.document(if(facility.id!="") facility.id else facility.name!![0]).set(facility)
            .addOnSuccessListener {
                Log.d("Firebase : ", "DocumentSnapshot successfully written!")
                Toast.makeText(MainActivity.context, "문서 추가 성공", Toast.LENGTH_SHORT)
            }
            .addOnFailureListener { e ->
                Log.w("Firebase : ", "Error writing document", e)
                Toast.makeText(MainActivity.context, "문서 추가 실패", Toast.LENGTH_SHORT)
            }
    }

    fun update(reference: CollectionReference, id:String, map:Map<String, Any>) {
        reference.document(id).update(map)
    }

    fun delete(reference: CollectionReference, id:String) {
        reference.document(id).delete()
    }

}
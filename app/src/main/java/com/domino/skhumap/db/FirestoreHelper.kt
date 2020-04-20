package com.domino.skhumap.db

import android.content.Context
import com.domino.skhumap.Facility
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreHelper {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private const val COLLECTION_FACILITIES = "facilities"
    val campusReference by lazy { db.collection(COLLECTION_FACILITIES) }

    fun queryPullDriven(queryTarget: CollectionReference) {
        queryTarget.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result!!.toObjects(Facility::class.java)
            }
        }
    }

    fun init(context:Context){
        FirebaseApp.initializeApp(context)
    }

}
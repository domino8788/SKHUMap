package com.domino.skhumap.repository

import android.content.Context
import com.domino.skhumap.Facility
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*

object FirestoreHelper {
    private var realTimeUpdateListener: ListenerRegistration? = null
    val db by lazy { FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder().setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build()
    } }
    private const val COLLECTION_FACILITIES = "facilities"
    private const val COLLECTION_SEARCH = "search"

    lateinit var userReference: DocumentReference
    val campusReference by lazy { db.collection(COLLECTION_FACILITIES) }
    val favoritesReference by lazy { userReference.collection("favorites") }
    val searchReference by lazy { db.collection(COLLECTION_SEARCH) }
    val calendarReference by lazy { userReference.collection("calendar") }
    fun departmentReference(departmentId: String, floor: Int): CollectionReference =
        campusReference.document(departmentId).collection(floor.toString())

    fun init(context: Context) {
        FirebaseApp.initializeApp(context)
    }

    fun naverMapRealTimeUpdate(
        facilities: MutableList<Facility>,
        queryTarget: CollectionReference,
        callback: (() -> Unit)?
    ) {
        realTimeUpdateListener?.remove()
        realTimeUpdateListener = queryTarget.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                for (change in querySnapshot?.documentChanges!!) {
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
}
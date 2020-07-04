package com.domino.skhumap.repository

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*

object FirestoreHelper {
    val db by lazy { FirebaseFirestore.getInstance().apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder().setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build()
    } }
    private const val COLLECTION_FACILITIES = "facilities"
    private const val COLLECTION_SEARCH = "search"

    lateinit var userReference: DocumentReference
    val campusReference by lazy { db.collection(COLLECTION_FACILITIES) }
    val searchReference by lazy { db.collection(COLLECTION_SEARCH) }
    val favoritesReference
    get() = userReference.collection("favorites")
    val calendarReference
    get() = userReference.collection("calendar")
    fun departmentReference(departmentId: String, floor: Int): CollectionReference =
        campusReference.document(departmentId).collection(floor.toString())

    fun init(context: Context) {
        FirebaseApp.initializeApp(context)
    }
}
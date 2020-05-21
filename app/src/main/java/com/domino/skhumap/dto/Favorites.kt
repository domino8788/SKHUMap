package com.domino.skhumap.dto

import com.domino.skhumap.db.FirestoreHelper.favoritesReference
import com.google.firebase.firestore.*
import java.io.Serializable

@IgnoreExtraProperties
data class Favorites(@PropertyName("index") var index:Int = 0,
                     @PropertyName("department")var department:DocumentReference? = null,
                     @PropertyName("floorNumber")var floorNumber:Int = 0,
                     @PropertyName("facility") var facility:DocumentReference? = null):Serializable {


    /* Favorites 객체의 정보로 해당 정보가 담겨있는 DocumentReference 를 반환한다. */
    fun toReference() = favoritesReference.document(
        "${department?.id?:"null"}_${floorNumber}_${facility!!.id}"
    )

}

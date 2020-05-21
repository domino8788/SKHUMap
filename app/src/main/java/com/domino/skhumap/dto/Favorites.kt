package com.domino.skhumap.dto

import com.google.firebase.firestore.*
import java.io.Serializable

@IgnoreExtraProperties
data class Favorites(@PropertyName("index") var index:Int = 0,
                     @PropertyName("department")var department:DocumentReference? = null,
                     @PropertyName("floorNumber")var floorNumber:Int = 0,
                     @PropertyName("facility") var facility:DocumentReference? = null):Serializable {


}

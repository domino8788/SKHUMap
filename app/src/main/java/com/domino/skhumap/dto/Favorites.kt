package com.domino.skhumap.dto

import com.domino.skhumap.Facility
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

    /* DB에서 조회한 Favorites 객체를 검색가능한 시설객체로 변환 */
    fun toSearchableFacility(result:HashMap<Int, SearchableFacility>, delete:HashMap<Int, DocumentReference> ,callback: (()->Unit)?) {
        department?.let { department ->
            department.get().addOnCompleteListener { departmentTask ->
                facility!!.get().addOnCompleteListener { facilityTask ->
                    facilityTask.result?.toObject(Facility::class.java)?.let {
                        result[index] = SearchableFacility(
                            departmentTask.result?.toObject(Facility::class.java),
                            floorNumber,
                            it
                        )
                    } ?: let { delete[index] = toReference() }
                    (callback)?.let { func -> func() }
                }
            }
        }
    }
}

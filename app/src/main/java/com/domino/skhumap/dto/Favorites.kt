package com.domino.skhumap.dto

import com.domino.skhumap.Facility
import com.domino.skhumap.db.FirestoreHelper.campusReference
import com.domino.skhumap.db.FirestoreHelper.favoritesReference
import com.google.firebase.firestore.*
import java.io.Serializable

@IgnoreExtraProperties
data class Favorites(@PropertyName("index") var index:Int = 0,
                     @get:Exclude private val paramId:String=""):Serializable {

    @DocumentId var id:String = paramId
        set(id){
            field = id
            /*
            0번 인덱스 : 건물 id
            1번 인덱스 : 층 번호
            2번 인덱스 : 시설 id
            */
            id.split("_").let { infoValues ->
                department = if(infoValues[0] != "null") campusReference.document(infoValues[0]) else null
                floorNumber = infoValues[1].toInt()
                facility =  department?.let { it.collection(infoValues[1]).document(infoValues[2]) }?: campusReference.document(infoValues[2])
            }
        }

    var department:DocumentReference? = null
        @Exclude
        get
    var floorNumber:Int = 0
        @Exclude
        get
    var facility:DocumentReference? = null
        @Exclude
        get

    /* Favorites 객체의 정보로 해당 정보가 담겨있는 DocumentReference 를 반환한다. */
    fun toReference() = favoritesReference.document(id)

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

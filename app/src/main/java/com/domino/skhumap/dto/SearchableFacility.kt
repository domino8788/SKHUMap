package com.domino.skhumap.dto

import android.os.Parcel
import android.os.Parcelable
import com.domino.skhumap.Facility
import com.domino.skhumap.db.FirestoreHelper
import com.domino.skhumap.db.FirestoreHelper.campusReference

data class SearchableFacility(val department: Facility?, val floorNumber:Int=0, val facility:Facility):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Facility::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readParcelable(Facility::class.java.classLoader)!!
    )

    /* 검색가능한 시설객체를 DB에 저장하는 규격인 Favorites 객체로 변환 */
    fun toFavorites(index:Int) = Favorites(
        index,
        /* department 값이 null이면 null 아니면 해당 건물의 DocumentReference 를 반환한다. */
        department?.let { department ->
            campusReference.document(department.id)
        } ?: null,
        floorNumber,
        /*
        department 값이 null 이면 campus 지도상의 시설이라는 뜻이다.
        department 값이 null 이 아니면 indoor 지도상의 시설이라는 뜻이다.
        */
        department?.let { department ->
        campusReference.document(department.id)
                .collection(floorNumber.toString())
                .document(facility.id)
        } ?: campusReference.document(facility.id)
    )

    /* SearchableFacility 객체의 정보로 해당 정보가 담겨있는 DocumentReference 를 반환한다. */
    fun toReference() = FirestoreHelper.favoritesReference.document(
        "${department?.id?:"null"}_${floorNumber}_${facility.id}"
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(department, flags)
        parcel.writeInt(floorNumber)
        parcel.writeParcelable(facility, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchableFacility> {
        override fun createFromParcel(parcel: Parcel): SearchableFacility {
            return SearchableFacility(parcel)
        }

        override fun newArray(size: Int): Array<SearchableFacility?> {
            return arrayOfNulls(size)
        }
    }
}
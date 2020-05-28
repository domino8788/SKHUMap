package com.domino.skhumap.dto

import android.os.Parcel
import android.os.Parcelable
import com.domino.skhumap.Facility
import com.domino.skhumap.repository.FirestoreHelper

data class SearchableFacility(val department: Facility?, val floorNumber:Int=0, val facility:Facility):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Facility::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readParcelable(Facility::class.java.classLoader)!!
    )

    private val documentId
    get() = "${department?.id?:"null"}_${floorNumber}_${facility!!.id}"

    /* 검색가능한 시설객체를 DB에 저장하는 규격인 Favorites 객체로 변환 */
    fun toFavorites(index:Int) = Favorites(index, documentId)

    /* SearchableFacility 객체의 정보로 해당 정보가 담겨있는 DocumentReference 를 반환한다. */
    fun toReference() = FirestoreHelper.favoritesReference.document(documentId)

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

    override fun equals(other: Any?): Boolean =
        if(other is SearchableFacility) {
            documentId == other.documentId
        } else
            super.equals(other)
}
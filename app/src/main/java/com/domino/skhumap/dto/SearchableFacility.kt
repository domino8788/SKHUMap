package com.domino.skhumap.dto

import android.os.Parcel
import android.os.Parcelable
import com.domino.skhumap.Facility

data class SearchableFacility(val department: Facility, val floorNumber:Int, val facility:Facility):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Facility::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readParcelable(Facility::class.java.classLoader)!!
    ) {
    }

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
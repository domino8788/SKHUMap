package com.domino.skhumap

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.*
import com.naver.maps.map.overlay.Marker

@IgnoreExtraProperties
data class Facility(
    @DocumentId var id: String = "",
    @PropertyName("name") var name: String = "",
    @PropertyName("location") var location: GeoPoint? = null,
    @PropertyName("type") var type: Int = 0,
    @PropertyName("info") var info: HashMap<String, Any>? = null
) : Parcelable {
    var marker: Marker? = null
        @Exclude
        get

    constructor(parcel: Parcel) : this() {
        parcel.run {
            id = readString()!!
            name = readString()!!
            location = GeoPoint(parcel.readDouble(), parcel.readDouble())
            type = readInt()
            info = readSerializable() as HashMap<String, Any>
        }
    }

    enum class TYPE(val id:Int, val icon:Int){
        DEPARTMENT(0, R.drawable.ic_apartment_black_24dp)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.run {
            writeString(id)
            writeString(name)
            writeDouble(location!!.latitude)
            writeDouble(location!!.longitude)
            writeInt(type)
            writeSerializable(info)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Facility> {
        override fun createFromParcel(parcel: Parcel): Facility {
            return Facility(parcel)
        }

        override fun newArray(size: Int): Array<Facility?> {
            return arrayOfNulls(size)
        }
    }
}
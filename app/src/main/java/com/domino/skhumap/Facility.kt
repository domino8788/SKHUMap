package com.domino.skhumap

import android.os.Parcel
import android.os.Parcelable
import com.domino.skhumap.manager.MapManager
import com.google.firebase.firestore.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

@IgnoreExtraProperties
data class Facility(
    @DocumentId var id: String = "",
    @PropertyName("name") var name: List<String>? = null,
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
            readList(name, Facility.javaClass.classLoader)!!
            location = GeoPoint(parcel.readDouble(), parcel.readDouble())
            type = readInt()
            info = readSerializable() as HashMap<String, Any>
        }
    }

    enum class TYPE(val id:Int, val icon:Int){
        DEPARTMENT(0, R.drawable.ic_apartment_black_24dp),
        LECTUREROOM(1, R.drawable.ic_apartment_black_24dp),
        TOILET(2, R.drawable.ic_apartment_black_24dp),
        ADMINISTRATION(3, R.drawable.ic_apartment_black_24dp),
        PROJECTROOM(4, R.drawable.ic_apartment_black_24dp),
        PROFESSORROOM(5, R.drawable.ic_apartment_black_24dp),
        DOOR(6, R.drawable.ic_apartment_black_24dp),
        INFO(7, R.drawable.ic_apartment_black_24dp)
    }

    fun addMarker(): Marker? = Marker().apply {
        captionText = "$id  $name"
        position = LatLng(location!!.latitude, location!!.longitude)
        icon = OverlayImage.fromResource(
            when (type) {
                TYPE.DEPARTMENT.id -> TYPE.DEPARTMENT.icon
                else -> TYPE.DEPARTMENT.icon
            }
        )
        isHideCollidedSymbols = true
        map = MapManager.naverMap
        marker = this
    }

    fun removeMarker() {
        marker?.map = null
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.run {
            writeString(id)
            writeList(name)
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
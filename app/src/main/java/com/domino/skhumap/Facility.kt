package com.domino.skhumap

import android.os.Parcel
import android.os.Parcelable
import com.domino.skhumap.MapManager.naverMap
import com.google.firebase.firestore.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

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
        }
    }

    fun addMarker(): Marker? = Marker().apply {
        captionText = "$id  $name"
        position = LatLng(location!!.latitude, location!!.longitude)
        icon = OverlayImage.fromResource(
            when (type) {
                MapManager.Type.DEPARTMENT.id -> MapManager.Type.DEPARTMENT.icon
                else -> MapManager.Type.DEPARTMENT.icon
            }
        )
        isHideCollidedSymbols = true

        map = naverMap
        marker = this
    }

    fun removeMarker() {
        marker?.map = null
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.run {
            writeString(id)
            writeString(name)
            writeDouble(location!!.latitude)
            writeDouble(location!!.longitude)
            writeInt(type)
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
package com.domino.skhumap

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.*
import com.naver.maps.map.overlay.Marker

@IgnoreExtraProperties
data class Facility(
    @DocumentId var id: String = "",
    @PropertyName("name") var name: List<String> = mutableListOf(),
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
            readList(name, String.javaClass.classLoader)!!
            location = GeoPoint(parcel.readDouble(), parcel.readDouble())
            type = readInt()
            info = readSerializable() as HashMap<String, Any>
        }
    }

    enum class TYPE(val id:Int, val icon:Int){
        DEPARTMENT(0, R.drawable.ic_apartment_black_24dp),
        LECTUREROOM(1, R.drawable.ic_lecture_room),
        LIBRARY(2, R.drawable.ic_book),
        ADMINISTRATION(3, R.drawable.ic_work),
        PROJECTROOM(4, R.drawable.ic_apartment_black_24dp),
        PROFESSORROOM(5, R.drawable.ic_professor_room),
        DOOR(6, R.drawable.ic_meeting_room_24px),
        INFO(7, R.drawable.ic_info),
        UPSTAIRS(8, R.drawable.ic_up_stair),
        DOWNSTAIRS(9, R.drawable.ic_down_stair),
        ENTRANCE(10, R.drawable.ic_entrance),
        ELEVATOR(11, R.drawable.ic_elevator),
        TOILET_MAN(12, R.drawable.ic_toilet_man),
        TOILET_WOMAN(13, R.drawable.ic_toilet_woman),
        CAFE(14, R.drawable.ic_cafe),
        DEPARTMENTROOM(15, R.drawable.ic_department_room),
        POSTOFFICE(16, R.drawable.ic_post_office),
        STATIONERY(17, R.drawable.ic_store)
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
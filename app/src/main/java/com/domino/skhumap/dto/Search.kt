package com.domino.skhumap.dto

import android.os.Parcel
import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.google.firebase.firestore.*

@IgnoreExtraProperties
data class Search(@DocumentId var id: String="", @PropertyName("keyword") var keyword:List<String>? = null):SearchSuggestion {

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()!!
        parcel.readList(keyword, String::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeList(keyword)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun getBody(): String = "${keyword!!.joinToString(" ","","")}"

    companion object CREATOR : Parcelable.Creator<Search> {
        override fun createFromParcel(parcel: Parcel): Search {
            return Search(parcel)
        }

        override fun newArray(size: Int): Array<Search?> {
            return arrayOfNulls(size)
        }
    }

}
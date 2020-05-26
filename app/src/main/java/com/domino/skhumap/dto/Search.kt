package com.domino.skhumap.dto

import android.os.Parcel
import android.os.Parcelable
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion
import com.domino.skhumap.Facility
import com.domino.skhumap.db.FirestoreHelper
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

    suspend fun toSearchableFacility():SearchableFacility{
        id.split("_").let { infoValues ->
            val department: DocumentReference? = if(infoValues[0] != "null") FirestoreHelper.campusReference.document(infoValues[0]) else null
            val floorNumber:Int = infoValues[1].toInt()
            val facility: DocumentReference? =  department?.let { it.collection(infoValues[1]).document(infoValues[2]) }?: FirestoreHelper.campusReference.document(infoValues[2])
            return withContext(Dispatchers.IO) {
                SearchableFacility(
                    department?.get()!!.await().toObject(Facility::class.java) ?: null,
                    floorNumber,
                    facility!!.get().await().toObject(Facility::class.java)!!
                )
            }
        }
    }

    val idToLocationInfo:String
    get() = id.split("_").let{
            if(it[0]!="null")
                "${when(it[0]){
                    "0"->"구두인관"
                    "1"->"승연관"
                    "2"->"일만관"
                    "3"->"월당관"
                    "5"->"나눔관"
                    "6"->"이천환기념관"
                    "7"->"새천년관"
                    "8"->"중앙도서관"
                    "9"->"성미가엘성당"
                    "m"->"미가엘관"
                    else->""
                }} ${it[1]}층"
            else
                it[2]
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
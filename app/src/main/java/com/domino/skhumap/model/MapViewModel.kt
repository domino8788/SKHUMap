package com.domino.skhumap.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domino.skhumap.Facility
import com.domino.skhumap.db.FirestoreHelper
import com.domino.skhumap.dto.SearchableFacility
import com.google.firebase.firestore.CollectionReference
import kotlin.math.abs

class MapViewModel:ViewModel(){
    val facilities:MutableList<Facility> = mutableListOf()
    val facilitiesLiveData:MutableLiveData<MutableList<Facility>> by lazy { MutableLiveData<MutableList<Facility>>() }
    val floorListLiveData: MutableLiveData<MutableList<Pair<String, Int>>> by lazy { MutableLiveData<MutableList<Pair<String, Int>>>() }
    val selectedFloorLiveData:MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val markMapLivdeData:MutableLiveData<SearchableFacility> by lazy { MutableLiveData<SearchableFacility>() }
    var selectedDepartment:Facility? = null
    set(facility) {
        field = facility
        facility?.let {
            floorListLiveData.value = getFloorList((it.info!!["minFloor"] as Long).toInt(), (it.info!!["maxFloor"] as Long).toInt())
            setSelectedFloor(1)
        }?: setSelectedFloor(null)
    }


    fun setSelectedFloor(floor:Int?) {
        selectedFloorLiveData.value = floor
        floor?.let{
            query(FirestoreHelper.departmentReference(selectedDepartment!!.id, selectedFloorLiveData!!.value!!))
        }?: query(FirestoreHelper.campusReference)
    }

    private fun query(target: CollectionReference) {
        FirestoreHelper.realTimeUpdate(facilities, target){
            facilitiesLiveData.postValue(facilities)
        }
    }

    private fun getFloorList(minFloor: Int, maxFloor: Int): MutableList<Pair<String, Int>> =
        mutableListOf<Pair<String, Int>>().apply {
            (minFloor..maxFloor).forEach { if(it!=0) add(getFloorName(it) to it) }
        }

    fun getSelectedDepartmentId() = selectedDepartment!!.id
    fun getFloorName(floorNumber: Int): String = if (floorNumber > 0) "f${floorNumber}" else "b${abs(floorNumber)}"
    fun getSelectedFloorNumber() = selectedFloorLiveData.value
    fun pickerValueToFloorNumber(pickerValue:Int) = floorListLiveData.value!![pickerValue]!!.second
    fun getCurrentSelectToSearchableFacility(facility: Facility?) = SearchableFacility(selectedDepartment!!, selectedFloorLiveData.value!!, facility!!)
}
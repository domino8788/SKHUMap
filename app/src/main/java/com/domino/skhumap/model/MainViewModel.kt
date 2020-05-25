package com.domino.skhumap.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domino.skhumap.view.MultipleLevelBottomSheetView

class MainViewModel:ViewModel() {
    val bottomSheetStateLiveData: MutableLiveData<MultipleLevelBottomSheetView.State> by lazy { MutableLiveData<MultipleLevelBottomSheetView.State>() }

    fun setBottomSheetState(state:MultipleLevelBottomSheetView.State){
        bottomSheetStateLiveData.postValue(state)
    }
}
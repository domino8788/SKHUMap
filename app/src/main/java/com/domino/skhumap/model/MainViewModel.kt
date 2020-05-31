package com.domino.skhumap.model

import android.webkit.WebViewClient
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domino.skhumap.view.MultipleLevelBottomSheetView

class MainViewModel:ViewModel() {
    val bottomSheetStateLiveData: MutableLiveData<MultipleLevelBottomSheetView.State> by lazy { MutableLiveData<MultipleLevelBottomSheetView.State>() }
    val webClientLiveData: MutableLiveData<WebViewClient> by lazy { MutableLiveData<WebViewClient>() }
    val toastLiveData: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun setBottomSheetState(state:MultipleLevelBottomSheetView.State){
        bottomSheetStateLiveData.postValue(state)
    }

    fun requestHttp(webClient:WebViewClient){
        webClientLiveData.postValue(webClient)
    }
}
package com.domino.skhumap.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domino.skhumap.dto.Search

class SearchViewModel : ViewModel() {

    val searchListMapLivdeData: MutableLiveData<List<Search>> by lazy { MutableLiveData<List<Search>>() }

}
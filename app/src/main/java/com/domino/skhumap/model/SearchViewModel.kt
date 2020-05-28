package com.domino.skhumap.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domino.skhumap.repository.FirestoreHelper
import com.domino.skhumap.dto.Search

class SearchViewModel : ViewModel() {

    val searchListMapLivdeData: MutableLiveData<List<Search>> by lazy { MutableLiveData<List<Search>>() }

    fun queryText(text: String) {
        FirestoreHelper
            .searchReference
            .whereArrayContains("keywords", text.trim().toUpperCase())
            .limit(6)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    searchListMapLivdeData.postValue(it.result?.toObjects(Search::class.java))
                }
            }
    }
}
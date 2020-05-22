package com.domino.skhumap.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domino.skhumap.dto.SearchableFacility

class FavoritesViewModel : ViewModel() {
    private val favorites: ArrayList<SearchableFacility> = ArrayList()

    val favoritesLiveData: MutableLiveData<ArrayList<SearchableFacility>> = MutableLiveData<ArrayList<SearchableFacility>>(favorites)



}
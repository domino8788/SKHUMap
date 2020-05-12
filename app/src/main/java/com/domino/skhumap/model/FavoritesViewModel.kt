package com.domino.skhumap.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domino.skhumap.dto.SearchableFacility

class FavoritesViewModel: ViewModel() {
    private val favorites:ArrayList<SearchableFacility> = ArrayList()
    val favoritesLiveData: MutableLiveData<ArrayList<SearchableFacility>> = MutableLiveData<ArrayList<SearchableFacility>>().also {
        it.value = favorites
    }

    fun add(favoritesItem:SearchableFacility){
        favorites.add(favoritesItem)
        favoritesLiveData.value = favorites
    }

    fun addAll(favoritesList:ArrayList<SearchableFacility>){
        favorites.clear()
        favorites.addAll(favoritesList)
        favoritesLiveData.value = favorites
    }

}
package com.domino.skhumap.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domino.skhumap.dto.SearchableFacility

class FavoritesViewModel : ViewModel() {
    private val favorites: ArrayList<SearchableFacility> = ArrayList()

    val favoritesLiveData: MutableLiveData<ArrayList<SearchableFacility>> = MutableLiveData<ArrayList<SearchableFacility>>(favorites)

    /* 즐겨찾기 삽입 */
    fun insert(searchableFavorites: SearchableFacility) {
        if(!favorites.contains(searchableFavorites)){
            favorites.add(searchableFavorites)
            favoritesLiveData.postValue(favorites)
            searchableFavorites.toReference().set(
                searchableFavorites.toFavorites(favorites.size-1)
            ).addOnFailureListener {
                favorites.remove(searchableFavorites)
                favoritesLiveData.postValue(favorites)
            }
        }
    }


}
package com.domino.skhumap.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domino.skhumap.db.FirestoreHelper.db
import com.domino.skhumap.db.FirestoreHelper.favoritesReference
import com.domino.skhumap.dto.Favorites
import com.domino.skhumap.dto.SearchableFacility
import com.google.firebase.firestore.DocumentReference

class FavoritesViewModel : ViewModel() {
    private val favorites: ArrayList<SearchableFacility> = ArrayList()
    private val result: HashMap<Int, SearchableFacility> = HashMap()
    private val delete: HashMap<Int, DocumentReference> = HashMap()

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

    fun query() {
        favoritesReference.orderBy("index").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result!!.toObjects(Favorites::class.java).forEach {
                    it.toSearchableFacility(result, delete) {
                        if ((result.size + delete.size) == task.result!!.size()) {
                            favorites.clear()
                            favorites.addAll(result.values)
                            favoritesLiveData.postValue(favorites)
                            if (delete.size != 0) {
                                db.runBatch { batch ->
                                    delete.values.forEach { deleteTarget ->
                                        batch.delete(deleteTarget)
                                    }
                                    favorites.forEachIndexed { index, updateTarget ->
                                        batch.update(updateTarget.toReference(), mapOf("index" to index))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateAll(newFavorites: ArrayList<SearchableFacility>) {
        favorites.removeAll(newFavorites)
        db.runBatch {batch ->
            favorites.forEach { batch.delete(it.toReference()) }
            newFavorites.forEachIndexed { index, searchableFacility -> batch.update(searchableFacility.toReference(), mapOf("index" to index)) }
        }
        favorites.clear()
        favorites.addAll(newFavorites)
        favoritesLiveData.postValue(favorites)
    }

}
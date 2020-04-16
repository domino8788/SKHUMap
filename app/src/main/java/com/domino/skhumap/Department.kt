package com.domino.skhumap

import com.google.firebase.firestore.GeoPoint

data class Department(val name:String ,val location:GeoPoint) {
}
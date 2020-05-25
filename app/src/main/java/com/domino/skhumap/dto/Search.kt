package com.domino.skhumap.dto

import com.google.firebase.firestore.*

@IgnoreExtraProperties
data class Search(@DocumentId var id: String="", @PropertyName("keyword") var keyword:List<String>? = null)
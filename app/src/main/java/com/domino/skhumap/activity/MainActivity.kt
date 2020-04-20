package com.domino.skhumap.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.domino.skhumap.R
import com.domino.skhumap.db.FirestoreHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirestoreHelper.init(this)

    }
}

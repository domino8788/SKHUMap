package com.domino.skhumap.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.domino.skhumap.R
import com.domino.skhumap.db.FirestoreHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirestoreHelper.init(this)
        main_bottom_sheet.initView()
    }
}

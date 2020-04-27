package com.domino.skhumap

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.domino.skhumap.db.FirestoreHelper
import com.domino.skhumap.manager.MapManager
import kotlinx.android.synthetic.main.fragment_map.*


class MainActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false

    lateinit var context: Context

    init {
        instance = this
    }

    companion object {
        private var instance: AppCompatActivity? = null
        val context: Context
            get() {
                return instance!!.applicationContext
            }
        val appCompatActivity: AppCompatActivity
            get() {
                return instance!!
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirestoreHelper.init()
        indoor_level_picker.run {
            setOnValueChangedListener { picker, oldVal, newVal ->
                MapManager.selectedFloor = MapManager.floorList[newVal].second
            }
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            wrapSelectorWheel = false
        }
    }

    override fun onBackPressed() {
        if (MapManager.selectedFloor != null) {
            MapManager.selectedFloor = null
            MapManager.selectedDepartment = null
        } else {
            if (doubleBackToExitPressedOnce) {
                return super.onBackPressed()
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
        }
    }
}

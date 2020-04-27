package com.domino.skhumap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

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

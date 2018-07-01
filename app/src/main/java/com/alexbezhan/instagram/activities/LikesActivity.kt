package com.alexbezhan.instagram.activities

import android.os.Bundle
import android.util.Log
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.views.setupBottomNavigation

class LikesActivity : BaseActivity() {
    private val TAG = "LikesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation(3)
        Log.d(TAG, "onCreate")
    }
}

package com.alexbezhan.instagram.activities.search

import android.os.Bundle
import android.util.Log
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.activities.BottomNavBar

class SearchActivity : BaseActivity() {
    private val TAG = "SearchActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBottomNavigation(BottomNavBar.POSITION_SEARCH)
        Log.d(TAG, "onCreate")
    }
}

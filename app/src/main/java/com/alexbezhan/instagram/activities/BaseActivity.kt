package com.alexbezhan.instagram.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.alexbezhan.instagram.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.bottom_navigation_view.*

abstract class BaseActivity(val navNumber: Int? = null, val isAuthProtected: Boolean = true)
    : AppCompatActivity() {
    private val TAG = "BaseActivity"

    fun setupBottomNavigation() {
        bottom_navigation_view.setIconSize(29f, 29f)
        bottom_navigation_view.setTextVisibility(false)
        bottom_navigation_view.enableItemShiftingMode(false)
        bottom_navigation_view.enableShiftingMode(false)
        bottom_navigation_view.enableAnimation(false)
        for (i in 0 until bottom_navigation_view.menu.size()) {
            bottom_navigation_view.setIconTintList(i, null)
        }
        bottom_navigation_view.setOnNavigationItemSelectedListener {
            val nextActivity =
                    when (it.itemId) {
                        R.id.nav_item_home -> HomeActivity::class.java
                        R.id.nav_item_search -> SearchActivity::class.java
                        R.id.nav_item_share -> ShareActivity::class.java
                        R.id.nav_item_likes -> LikesActivity::class.java
                        R.id.nav_item_profile -> ProfileActivity::class.java
                        else -> {
                            Log.e(TAG, "unknown nav item clicked $it")
                            null
                        }
                    }
            if (nextActivity != null) {
                val intent = Intent(this, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            } else {
                false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isAuthProtected) {
            verifyUserAuthenticated()
        }
    }

    private fun verifyUserAuthenticated() {
        fun goToLogin() {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        if (FirebaseAuth.getInstance().currentUser == null) {
            goToLogin()
        }
        FirebaseAuth.getInstance().addAuthStateListener {
            if (it.currentUser == null) {
                goToLogin()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (bottom_navigation_view != null && navNumber != null) {
            bottom_navigation_view.menu.getItem(navNumber).isChecked = true
        }
    }
}
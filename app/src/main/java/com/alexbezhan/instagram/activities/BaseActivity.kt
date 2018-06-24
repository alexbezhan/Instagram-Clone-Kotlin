package com.alexbezhan.instagram.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alexbezhan.instagram.activities.login.LoginActivity
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper.auth
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.bottom_navigation_view.*

abstract class BaseActivity(val isAuthProtected: Boolean = true)
    : AppCompatActivity() {
    private val TAG = "BaseActivity"

    protected var bottomNavBar: BottomNavBar? = null

    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (isAuthProtected) {
            mAuthListener = FirebaseAuth.AuthStateListener {
                if (!isAuthenticated()) {
                    goToLogin()
                }
            }
            mAuthListener?.onAuthStateChanged(FirebaseHelper.auth)
        }
    }

    protected inline fun <reified T : BaseViewModel> initModel(
            factory: ViewModelProvider.Factory): T {
        val model = ViewModelProviders.of(this, factory).get(T::class.java)
        model.error.observe(this, Observer {
            it?.let {
                when (it) {
                    is ErrorMessage.Plain -> showToast(it.message)
                    is ErrorMessage.StringRes -> showToast(resources.getString(it.resId))
                }
            }
        })
        model.notifications.observe(this, Observer {
            it?.let { notifications ->
                bottomNavBar?.setNotifications(notifications)
                bottomNavBar?.showNotificationPopover(tooltip_relative_layout)
            }
        })
        return model
    }

    protected fun isAuthenticated() = auth.currentUser != null

    override fun onStart() {
        super.onStart()
        mAuthListener?.let { FirebaseHelper.auth.addAuthStateListener(it) }
    }

    override fun onStop() {
        super.onStop()
        mAuthListener?.let { FirebaseHelper.auth.removeAuthStateListener(it) }
    }

    fun setupBottomNavigation(navPosition: Int) {
        bottomNavBar = BottomNavBar(this, bottom_navigation_view, navPosition)
        bottomNavBar?.setupBottomNavigation()
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        bottomNavBar?.let { navBar ->
            navBar.setCurrentNavItemActive()
            navBar.showNotificationPopover(tooltip_relative_layout)
        }
    }
}
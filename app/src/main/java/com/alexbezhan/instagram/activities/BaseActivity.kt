package com.alexbezhan.instagram.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.alexbezhan.instagram.activities.login.LoginActivity
import kotlinx.android.synthetic.main.bottom_navigation_view.*

abstract class BaseActivity(val isAuthProtected: Boolean = true)
    : AppCompatActivity() {
    protected var bottomNavBar: BottomNavBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    protected inline fun <reified T : BaseViewModel> initModel(
            factory: ViewModelProvider.Factory): T {
        val model = ViewModelProviders.of(this, factory).get(T::class.java)
        model.error.observe(this, Observer {
            it?.let {
                val message = when (it) {
                    is ErrorMessage.Plain -> it.message
                    is ErrorMessage.StringRes -> resources.getString(it.resId)
                }
                Log.e(TAG, "error: $message")
                showToast(message)
            }
        })
        model.notifications.observe(this, Observer {
            it?.let { notifications ->
                bottomNavBar?.setNotifications(notifications)
                bottomNavBar?.showNotificationPopover(tooltip_relative_layout)
            }
        })
        model.authState.observe(this, Observer { uid ->
            if (uid == null && isAuthProtected) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        })
        model.isLoading.observe(this, Observer {
            it?.let { isLoading ->
                with(findViewById<View>(android.R.id.content)) {
                    if (isLoading) {
                        alpha = 0.3f
                        setClickableDeep(false)
                    } else {
                        alpha = 1.0f
                        setClickableDeep(true)
                    }
                }
            }
        })
        return model
    }

    fun setupBottomNavigation(navPosition: Int) {
        bottomNavBar = BottomNavBar(this, bottom_navigation_view, navPosition)
        bottomNavBar?.setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        bottomNavBar?.let { navBar ->
            navBar.setCurrentNavItemActive()
            navBar.showNotificationPopover(tooltip_relative_layout)
        }
    }

    companion object {
        const val TAG = "BaseActivity"
    }
}
package com.alexbezhan.instagram.activities.register

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.activities.home.HomeActivity

class RegisterActivity : BaseActivity(isAuthProtected = false), EmailFragment.Listener,
        NamePassFragment.Listener {
    private val TAG = "RegisterActivity"

    private var mEmail: String? = null

    private lateinit var mModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mModel = initModel(RegisterViewModelFactory())
        mModel.email.observe(this, Observer {
            it?.let {
                mEmail = it
            }
        })
        mModel.goBackToEmailUiCmd.observe(this, Observer {
            supportFragmentManager.popBackStack()
        })
        mModel.openHomeUiCmd.observe(this, Observer {
            startHomeActivity()
        })
        mModel.openNamePassUiCmd.observe(this, Observer {
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, NamePassFragment())
                    .addToBackStack(null)
                    .commit()
        })
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, EmailFragment())
                    .commit()
        }
    }

    override fun onEmailEntered(email: String) =
            mModel.onEmailEntered(email)

    override fun onRegister(fullName: String, password: String) {
        mModel.onRegister(mEmail, fullName, password)
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
package com.alexbezhan.instagram.activities.login

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.activities.coordinateBtnAndInputs
import com.alexbezhan.instagram.activities.home.HomeActivity
import com.alexbezhan.instagram.activities.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : BaseActivity(isAuthProtected = false), KeyboardVisibilityEventListener {

    private val TAG = "LoginActivity"

    private lateinit var mModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")

        mModel = initModel(LoginViewModelFactory())
        mModel.openHomeUiCmd.observe(this, Observer {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        })
        mModel.openRegisterUiCmd.observe(this, Observer {
            startActivity(Intent(this, RegisterActivity::class.java))
        })

        KeyboardVisibilityEvent.setEventListener(this, this)
        coordinateBtnAndInputs(login_btn, email_input, password_input)

        login_btn.setOnClickListener{
            mModel.onLogin(
                    email = email_input.text.toString(),
                    password = password_input.text.toString())
        }
        create_account_text.setOnClickListener{
            mModel.onCreateAccount()
        }
    }

    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if (isKeyboardOpen) {
            create_account_text.visibility = View.GONE
        } else {
            create_account_text.visibility = View.VISIBLE
        }
    }
}

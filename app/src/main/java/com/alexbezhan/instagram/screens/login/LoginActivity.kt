package com.alexbezhan.instagram.screens.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.screens.common.BaseActivity
import com.alexbezhan.instagram.screens.common.coordinateBtnAndInputs
import com.alexbezhan.instagram.screens.home.HomeActivity
import com.alexbezhan.instagram.screens.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : BaseActivity(), KeyboardVisibilityEventListener, View.OnClickListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")

        KeyboardVisibilityEvent.setEventListener(this, this)
        coordinateBtnAndInputs(login_btn, email_input, password_input)
        login_btn.setOnClickListener(this)
        create_account_text.setOnClickListener(this)

        mViewModel = initViewModel()
        mViewModel.goToHomeScreen.observe(this, Observer {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        })
        mViewModel.goToRegisterScreen.observe(this, Observer {
            startActivity(Intent(this, RegisterActivity::class.java))
        })
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.login_btn ->
                mViewModel.onLoginClick(
                        email = email_input.text.toString(),
                        password = password_input.text.toString()
                )
            R.id.create_account_text -> mViewModel.onRegisterClick()

        }
    }

    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if (isKeyboardOpen) {
            create_account_text.visibility = View.GONE
        } else {
            create_account_text.visibility = View.VISIBLE
        }
    }

    companion object {
        const val TAG = "LoginActivity"
    }
}

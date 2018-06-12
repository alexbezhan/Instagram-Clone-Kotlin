package com.alexbezhan.instagram.activities.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.activities.register.RegisterActivity
import com.alexbezhan.instagram.activities.coordinateBtnAndInputs
import com.alexbezhan.instagram.activities.home.HomeActivity
import com.alexbezhan.instagram.activities.showToast
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : BaseActivity(isAuthProtected = false), KeyboardVisibilityEventListener,
        View.OnClickListener {

    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")

        KeyboardVisibilityEvent.setEventListener(this, this)
        coordinateBtnAndInputs(login_btn, email_input, password_input)
        login_btn.setOnClickListener(this)
        create_account_text.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.login_btn -> {
                val email = email_input.text.toString()
                val password = password_input.text.toString()
                if (validate(email, password)) {
                    FirebaseHelper.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                        if (it.isSuccessful) {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    showToast("Please enter email and password")
                }
            }
            R.id.create_account_text -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }

    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if (isKeyboardOpen) {
            create_account_text.visibility = View.GONE
        } else {
            create_account_text.visibility = View.VISIBLE
        }
    }

    private fun validate(email: String, password: String) =
            email.isNotEmpty() && password.isNotEmpty()

}

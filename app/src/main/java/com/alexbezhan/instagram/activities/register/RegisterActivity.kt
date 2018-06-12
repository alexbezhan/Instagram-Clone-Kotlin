package com.alexbezhan.instagram.activities.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.activities.home.HomeActivity
import com.alexbezhan.instagram.activities.showToast
import com.alexbezhan.instagram.models.User
import com.alexbezhan.instagram.utils.firebase.FirebaseHelper
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class RegisterActivity : BaseActivity(isAuthProtected = false), EmailFragment.Listener,
        NamePassFragment.Listener {
    private val TAG = "RegisterActivity"

    private var mEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, EmailFragment())
                    .commit()
        }
    }

    override fun onNext(email: String) {
        if (email.isNotEmpty()) {
            mEmail = email
            FirebaseHelper.auth.fetchSignInMethodsForEmail(email) { signInMethods ->
                if (signInMethods.isEmpty()) {
                    supportFragmentManager.beginTransaction().replace(R.id.frame_layout, NamePassFragment())
                            .addToBackStack(null)
                            .commit()
                } else {
                    showToast("This email already exists")
                }
            }
        } else {
            showToast("Please enter email")
        }
    }

    override fun onRegister(fullName: String, password: String) {
        if (fullName.isNotEmpty() && password.isNotEmpty()) {
            val email = mEmail
            if (email != null) {
                FirebaseHelper.auth.createUserWithEmailAndPassword(email, password) {
                    FirebaseHelper.database.createUser(it.user.uid, mkUser(fullName, email)) {
                        startHomeActivity()
                    }
                }
            } else {
                Log.e(TAG, "onRegister: email is null")
                showToast("Please enter email")
                supportFragmentManager.popBackStack()
            }
        } else {
            showToast("Please enter full name and password")
        }
    }

    private fun unknownRegisterError(it: Task<*>) {
        Log.e(TAG, "failed to create user profile", it.exception)
        showToast("Something wrong happened. Please try again later")
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun mkUser(fullName: String, email: String): User {
        val username = mkUsername(fullName)
        return User(name = fullName, username = username, email = email)
    }

    private fun mkUsername(fullName: String) =
            fullName.toLowerCase().replace(" ", ".")

    private fun FirebaseAuth.fetchSignInMethodsForEmail(email: String,
                                                        onSuccess: (List<String>) -> Unit) {
        fetchSignInMethodsForEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess(it.result.signInMethods ?: emptyList<String>())
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }

    private fun DatabaseReference.createUser(uid: String, user: User, onSuccess: () -> Unit) {
        val reference = child("users").child(uid)
        reference.setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                unknownRegisterError(it)
            }
        }
    }

    private fun FirebaseAuth.createUserWithEmailAndPassword(email: String, password: String,
                                                            onSuccess: (AuthResult) -> Unit) {
        createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onSuccess(it.result)
                    } else {
                        unknownRegisterError(it)
                    }
                }
    }
}
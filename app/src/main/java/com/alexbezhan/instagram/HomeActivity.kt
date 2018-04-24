package com.alexbezhan.instagram

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : BaseActivity(0) {
    private val TAG = "HomeActivity"
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()

        mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()
//        mAuth.signInWithEmailAndPassword("alex@alexbezhan.com", "password")
//                .addOnCompleteListener{
//                    if (it.isSuccessful) {
//                        Log.d(TAG, "signIn: success")
//                    } else {
//                        Log.e(TAG, "signIn: failure", it.exception)
//                    }
//                }
    }

    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

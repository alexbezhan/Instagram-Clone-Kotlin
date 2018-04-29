package com.alexbezhan.instagram.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    private val TAG = "EditProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        close_image.setOnClickListener {
            finish()
        }

        val auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance().reference
        database.child("users").child(auth.currentUser!!.uid)
                .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                    val user = it.getValue(User::class.java)
                    name_input.setText(user!!.name, TextView.BufferType.EDITABLE)
                    username_input.setText(user.username, TextView.BufferType.EDITABLE)
                    website_input.setText(user.website, TextView.BufferType.EDITABLE)
                    bio_input.setText(user.bio, TextView.BufferType.EDITABLE)
                    email_input.setText(user.email, TextView.BufferType.EDITABLE)
                    phone_input.setText(user.phone.toString(), TextView.BufferType.EDITABLE)
                })
    }
}
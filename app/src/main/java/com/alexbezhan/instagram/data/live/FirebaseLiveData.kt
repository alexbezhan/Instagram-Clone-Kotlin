package com.alexbezhan.instagram.data.live

import android.arch.lifecycle.LiveData
import com.alexbezhan.instagram.data.firebase.utils.FirebaseHelper.database
import com.alexbezhan.instagram.data.firebase.utils.ValueEventListenerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class FirebaseLiveData(private val computeReference: (String) -> String)
    : LiveData<DataSnapshot>() {
    private val listener = ValueEventListenerAdapter {
        value = it
    }
    private var isActive = false
    private var currentUid: String? = null
    private var reference: DatabaseReference? = null

    init {
        FirebaseAuth.getInstance().addAuthStateListener {
            currentUid = it.currentUser?.uid
            updateListener()
        }
    }

    override fun onActive() {
        super.onActive()
        isActive = true
        updateListener()
    }

    override fun onInactive() {
        super.onInactive()
        isActive = false
        updateListener()
    }

    private fun updateListener() {
        val localCurrentUid = currentUid
        val localReference = reference
        if (localReference == null && isActive && localCurrentUid != null) {
            reference = database.child(computeReference(localCurrentUid)).apply {
                addValueEventListener(listener)
            }
        } else if (localReference != null && (!isActive || localCurrentUid == null)) {
            localReference.removeEventListener(listener)
            reference = null
        }
    }
}
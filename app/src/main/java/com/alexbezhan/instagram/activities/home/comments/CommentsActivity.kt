package com.alexbezhan.instagram.activities.home.comments

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import com.alexbezhan.instagram.utils.livedata.HasUserLiveData
import com.alexbezhan.instagram.utils.livedata.UserLiveDataComponent

class CommentsViewModel : ViewModel(), HasUserLiveData by UserLiveDataComponent() {

}

class CommentsActivity : BaseActivity() {
    private lateinit var mModel: CommentsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
    }

    override fun onStart() {
        super.onStart()
        mModel = ViewModelProviders.of(this).get(CommentsViewModel::class.java)
    }
}
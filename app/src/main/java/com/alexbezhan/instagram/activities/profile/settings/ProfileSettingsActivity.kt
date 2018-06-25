package com.alexbezhan.instagram.activities.profile.settings

import android.os.Bundle
import com.alexbezhan.instagram.R
import com.alexbezhan.instagram.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_profile_settings.*

class ProfileSettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        val model = initModel<ProfileSettingsViewModel>(ProfileSettingsViewModelFactory())
        sign_out_text.setOnClickListener { model.onSignOut() }
        back_image.setOnClickListener { finish() }
    }
}

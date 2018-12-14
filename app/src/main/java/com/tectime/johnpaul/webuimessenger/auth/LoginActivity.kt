package com.tectime.johnpaul.webuimessenger.auth

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tectime.johnpaul.webuimessenger.R
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        back_to_register_button_login.setOnClickListener {
            finish()
        }
    }
}
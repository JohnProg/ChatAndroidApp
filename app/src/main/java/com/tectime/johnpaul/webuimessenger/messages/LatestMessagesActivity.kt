package com.tectime.johnpaul.webuimessenger.messages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.tectime.johnpaul.webuimessenger.R
import com.tectime.johnpaul.webuimessenger.auth.RegisterActivity

class LatestMessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        verifyUserIsLoggedIn()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_new_message -> {
                moveToNewMessageScreen()
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                moveToRegisterScreen()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid

        if (uid == null) {
            moveToRegisterScreen()
        }
    }

    private fun moveToRegisterScreen() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun moveToNewMessageScreen() {
        val intent = Intent(this, NewMessageActivity::class.java)
        startActivity(intent)
    }
}

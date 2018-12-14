package com.tectime.johnpaul.webuimessenger.auth

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.tectime.johnpaul.webuimessenger.R
import com.tectime.johnpaul.webuimessenger.messages.LatestMessagesActivity
import com.tectime.johnpaul.webuimessenger.models.User
import kotlinx.android.synthetic.main.register_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG: String = "RegisterActivity"
    }

    private var selectedPhotoUri: Uri? = null
    private val progressDialog by lazy {
        ProgressDialog(this).apply {
            setMessage("Loading...")
            setCancelable(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_main)

        register_button_register.setOnClickListener {
            performRegister()
        }

        select_avatar_button_register.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        already_have_account_text_view_register.setOnClickListener {
            Log.d(TAG, "Try to show login activity")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            profile_image_view_register.setImageBitmap(bitmap)
            select_avatar_button_register.alpha = 0f

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            select_avatar_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister() {
        val username = username_edittext_register.text.toString().trim()
        val email = email_edittext_register.text.toString().trim()
        val password = password_edittext_register.text.toString().trim()

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_LONG).show()
            return
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter a email", Toast.LENGTH_LONG).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_LONG).show()
            return
        }

        if (selectedPhotoUri == null) {
            Toast.makeText(this, "Please select a photo", Toast.LENGTH_LONG).show()
            return
        }

        progressDialog.show()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    Toast.makeText(this, "Successfully Login with uid: ${it.result.user.uid}", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Successfully Login with uid: ${it.result.user.uid}")
                    uploadProfileImage()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Failed to create a user: ${it.message}")
                    progressDialog.dismiss()
                }
    }

    private fun uploadProfileImage() {
        val uri = selectedPhotoUri ?: return progressDialog.dismiss()

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(uri)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully uploading image with uid: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d(TAG, "File location: $it")
                        saveUserData(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to upload the user profile: ${it.message}")
                    progressDialog.dismiss()
                }
    }

    private fun saveUserData(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)

        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully saved the user info")
                    progressDialog.dismiss()
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to save a user: ${it.message}")
                    progressDialog.dismiss()
                }
    }
}
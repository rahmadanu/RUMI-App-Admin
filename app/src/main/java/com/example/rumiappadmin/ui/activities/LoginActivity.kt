package com.example.rumiappadmin.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.rumiappadmin.R
import com.example.rumiappadmin.databinding.ActivityLoginBinding
import com.example.rumiappadmin.firestore.FirestoreClass
import com.example.rumiappadmin.models.Admin
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_RUMIAppAdmin_NoActionBar)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener(this)

        userAutoLoggedIn()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_login -> {
                    loginRegisteredUser()
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this@LoginActivity, "Please enter email.", Toast.LENGTH_SHORT).show()
                false
            }

            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(this@LoginActivity, "Please enter password.", Toast.LENGTH_SHORT)
                    .show()
                false
            }
            else -> {
                true
            }
        }
    }

    private fun loginRegisteredUser() {
        if (validateLoginDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        //showErrorSnackBar("You are logged in successfully.", false)
                        FirestoreClass().getUserDetails(this@LoginActivity)
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userLoggedInSuccess(admin: Admin) {
        hideProgressDialog()

        startActivity(Intent(this@LoginActivity, MenuActivity::class.java))
        finish()
    }

   private fun userAutoLoggedIn() {
        val currentUserId = FirestoreClass().getCurrentUserId()

        if (currentUserId.isNotEmpty()) {
            startActivity(Intent(this@LoginActivity, MenuActivity::class.java))
        }
   }
}
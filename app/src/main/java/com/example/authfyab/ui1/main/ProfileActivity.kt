package com.example.authfyab.ui1.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.authfyab.databinding.ActivityProfileBinding
import com.example.authfyab.ui1.auth.AuthViewModel

import com.example.authfyab.ui1.auth.SigninActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // 1. Get the currently logged-in user directly from Firebase
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email ?: ""

        // 2. Put their email into the read-only box
        binding.profileEmailField.setText(userEmail)

        // 3. Listen for the UPDATE button click
        binding.btnUpdatePassword.setOnClickListener {
            val currentPassword = binding.profileCurrentPasswordField.text.toString()
            val newPassword = binding.profileNewPasswordField.text.toString()
            val confirmPassword = binding.profileConfirmPasswordField.text.toString()

            authViewModel.changePassword(userEmail, currentPassword, newPassword, confirmPassword)
        }

        // 4. Listen for the DELETE button click
        binding.btnDeleteAccount.setOnClickListener {
            val currentPassword = binding.profileCurrentPasswordField.text.toString()

            // We only need the current password to delete the account
            authViewModel.deleteAccount(userEmail, currentPassword)
        }

        // 5. Observer for Password Update Success (I deleted the duplicate one!)
        authViewModel.passwordUpdateSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                // Clear the boxes if it succeeded
                binding.profileCurrentPasswordField.text?.clear()
                binding.profileNewPasswordField.text?.clear()
                binding.profileConfirmPasswordField.text?.clear()
            }
        }

        // 6. Observer for Deletion Success
        authViewModel.accountDeletionSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Account Deleted!", Toast.LENGTH_SHORT).show()

                // Navigate back to Login Screen
                val intent = Intent(this@ProfileActivity, SigninActivity::class.java)
                startActivity(intent)

                // This safely closes the Profile and Dashboard screens behind it
                finishAffinity()
            }
        }

        // 7. This is the "Listener" that shows the error messages!
        authViewModel.toastMessage.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
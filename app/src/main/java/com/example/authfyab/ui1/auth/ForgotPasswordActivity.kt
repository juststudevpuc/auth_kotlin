package com.example.authfyab.ui1.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.authfyab.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Setup View Binding (Modern standard)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Initialize ViewModel
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // 3. Listen for the Reset Button click
        binding.btnReset.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.tvPhone.text.toString().trim()

            authViewModel.resetPassword(email, phone)
        }

        // 4. Observe the Toa   st messages from ViewModel
        authViewModel.toastMessage.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                // If successful, close the screen
                if (message.contains("sent")) {
                    finish()
                }
            }
        }
    }
}
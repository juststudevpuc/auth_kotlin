package com.example.authfyab.ui1.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.authfyab.databinding.ActivitySignupBinding
import com.example.authfyab.ui.auth.AuthViewModel

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // 1. One line of code to handle the button click!
        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            authViewModel.performSignup(email, password)
        }

        // 2. Listen for Success
        authViewModel.signupSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                val intent = Intent(this, SigninActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // 3. Listen for Errors
        authViewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        // Redirect back to login
        binding.loginRedirectText.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
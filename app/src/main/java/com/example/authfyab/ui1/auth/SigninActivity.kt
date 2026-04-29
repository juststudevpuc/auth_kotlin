package com.example.authfyab.ui1.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.authfyab.databinding.ActivitySigninBinding
import com.example.authfyab.ui.auth.AuthViewModel
import com.example.authfyab.ui1.main.MainActivity

class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding

    // 1. Declare the Manager (ViewModel) instead of Firebase
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Initialize the ViewModel
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // 3. When the user clicks login, just hand the order to the Manager
        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            // The ViewModel does all the checking and Firebase calling now!
            authViewModel.performLogin(email, password)
        }

        // 4. Listen for the Success Bell from the ViewModel
        authViewModel.loginSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Destroy the login screen so they can't press 'back' to get here
            }
        }

        // 5. Listen for the Error Bell from the ViewModel
        authViewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        // Keep your redirect button exactly the same
        binding.signupRedirectText.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }
    }
}
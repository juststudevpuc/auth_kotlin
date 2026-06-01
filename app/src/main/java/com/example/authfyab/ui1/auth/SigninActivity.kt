package com.example.authfyab.ui1.auth // Update this package name if necessary!

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.authfyab.databinding.ActivitySigninBinding
import com.example.authfyab.ui1.main.MainActivity

class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the ViewModel (Fixed the lowercase 'a' typo here!)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // LOGIN BUTTON CLICK (Matches XML: @+id/login_button)
        binding.loginButton.setOnClickListener {
            // Matches XML: @+id/login_email and @+id/login_password
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            authViewModel.performLogin(email, password)

            // LOGIN BUTTON CLICK
            binding.loginButton.setOnClickListener {
                val email = binding.loginEmail.text.toString().trim()
                val password = binding.loginPassword.text.toString().trim()

                // FIXED: Use performLogin to match your ViewModel!
                authViewModel.performLogin(email, password)
            }
        }

        // FORGOT PASSWORD CLICK (Matches XML: @+id/forgot_password)
        binding.forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // SIGNUP REDIRECT CLICK (Matches XML: @+id/signupRedirectText)
        binding.signupRedirectText.setOnClickListener {
            // Assuming you have a SignupActivity. Change if needed!
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // OBSERVE SUCCESS -> Go to MainActivity
        authViewModel.loginSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Close the login screen
            }
        }

        // OBSERVE ERRORS -> Show Toast
        authViewModel.toastMessage.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
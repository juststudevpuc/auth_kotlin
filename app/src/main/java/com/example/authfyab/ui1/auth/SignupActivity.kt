package com.example.authfyab.ui1.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.authfyab.databinding.ActivitySignupBinding


class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString().trim()
            val phone = binding.signupPhone.text.toString().trim()
            val password = binding.signupPassword.text.toString().trim()

            authViewModel.performSignup(email, phone, password)
        }

        authViewModel.signupSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                val intent = Intent(this, SigninActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        authViewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        binding.loginRedirectText.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
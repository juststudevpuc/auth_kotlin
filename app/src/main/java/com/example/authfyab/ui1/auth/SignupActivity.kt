package com.example.authfyab.ui1.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.authfyab.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore // 1. Added Firestore Import

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore // 2. Declared Firestore variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setup ViewBinding
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fix: Use binding.root to prevent crashes if R.id.main is missing in XML
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance() // 3. Initialized Firestore

        // Sign Up Button
        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupConfirm.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {

                    // Step 1 of the Dance: Create the Auth ID
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            // Step 2 of the Dance: Save to Firestore
                            val userId = firebaseAuth.currentUser?.uid

                            // Package up the data we want to save
                            val userMap = hashMapOf(
                                "email" to email,
                                "role" to "user" // You can add more profile fields here later!
                            )

                            // Send it to a collection called "Users"
                            if (userId != null) {
                                firestore.collection("Users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Account Created & Data Saved!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, SigninActivity::class.java)
                                        startActivity(intent)
                                        finish() // Destroy Signup screen so they can't go back
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Firestore Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Redirect to Login
        binding.loginRedirectText.setOnClickListener {
            val loginIntent = Intent(this, SigninActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }
}
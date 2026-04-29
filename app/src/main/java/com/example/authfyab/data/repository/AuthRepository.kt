package com.example.authfyab.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // The callback (onResult) sends the answer back up to the ViewModel
    fun loginUser(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Tell the ViewModel: True (Success), with a success message
                    onResult(true, "Login Successful")
                } else {
                    // Tell the ViewModel: False (Failed), with the exact Firebase error
                    onResult(false, task.exception?.message ?: "Unknown login error")
                }
            }
    }
    // NEW: The Signup Function
    fun registerUser(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        // Step 1: Create the Vault Account
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                // Step 2: Save to Firestore
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    val userMap = hashMapOf(
                        "email" to email,
                        "role" to "user"
                    )

                    firestore.collection("Users").document(userId).set(userMap)
                        .addOnSuccessListener {
                            onResult(true, "Account Created & Data Saved!") // Success!
                        }
                        .addOnFailureListener { e ->
                            onResult(false, "Firestore Error: ${e.message}") // Database failed
                        }
                }
            } else {
                onResult(false, task.exception?.message ?: "Unknown signup error") // Auth failed
            }
        }
    }
}
package com.example.authfyab.data.repository

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

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
}
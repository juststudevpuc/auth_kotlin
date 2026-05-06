package com.example.authfyab.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.EmailAuthProvider

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

    //    01/may
    fun updatePassword(
        email: String,
        oldPass: String,
        newPass: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val user = firebaseAuth.currentUser

        if (user != null && user.email == email) {
            // 1. Create a "key" using their old password
            val credential = EmailAuthProvider.getCredential(email, oldPass)

            // 2. Re-authenticate them behind the scenes
            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {

                    // 3. If old password is correct, update to the new one!
                    user.updatePassword(newPass).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            onResult(true, "Password updated successfully!")
                        } else {
                            onResult(false, "Failed: ${updateTask.exception?.message}")
                        }
                    }
                } else {
                    // This triggers if they typed the wrong CURRENT password
                    onResult(false, "Incorrect Current Password!")
                }
            }
        } else {
            onResult(false, "No user logged in.")
        }
    }

    // NEW: Delete Account Function
    fun deleteUserAccount(email: String, currentPass: String, onResult: (Boolean, String) -> Unit) {
        val user = firebaseAuth.currentUser

        if (user != null && user.email == email) {
            // 1. Create a "key" using their password
            val credential = EmailAuthProvider.getCredential(email, currentPass)

            // 2. Re-authenticate them behind the scenes
            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {

                    // 3. If password is correct, DELETE the account!
                    user.delete().addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            onResult(true, "Account deleted successfully.")
                        } else {
                            onResult(false, "Failed to delete: ${deleteTask.exception?.message}")
                        }
                    }
                } else {
                    // Triggers if they typed the wrong password
                    onResult(false, "Incorrect Password! Cannot delete account.")
                }
            }
        } else {
            onResult(false, "No user logged in.")
        }
    }

}
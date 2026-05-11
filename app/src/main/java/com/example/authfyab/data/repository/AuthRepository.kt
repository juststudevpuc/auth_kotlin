package com.example.authfyab.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.EmailAuthProvider

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun loginUser(email: String, pass: String, onResult: (Boolean, String) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Login Successful")
                } else {
                    onResult(false, task.exception?.message ?: "Unknown login error")
                }
            }
    }

    fun registerUser(email: String, phone: String, pass: String, onResult: (Boolean, String) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    val userMap = hashMapOf(
                        "email" to email,
                        "phone" to phone,
                        "role" to "user"
                    )

                    firestore.collection("Users").document(userId).set(userMap)
                        .addOnSuccessListener {
                            onResult(true, "Account Created & Data Saved!")
                        }
                        .addOnFailureListener { e ->
                            onResult(false, "Firestore Error: ${e.message}")
                        }
                }
            } else {
                onResult(false, task.exception?.message ?: "Unknown signup error")
            }
        }
    }

    fun updatePassword(
        email: String,
        oldPass: String,
        newPass: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val user = firebaseAuth.currentUser

        if (user != null && user.email == email) {
            val credential = EmailAuthProvider.getCredential(email, oldPass)

            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updatePassword(newPass).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            onResult(true, "Password updated successfully!")
                        } else {
                            onResult(false, "Failed: ${updateTask.exception?.message}")
                        }
                    }
                } else {
                    onResult(false, "Incorrect Current Password!")
                }
            }
        } else {
            onResult(false, "No user logged in.")
        }
    }

    fun deleteUserAccount(email: String, currentPass: String, onResult: (Boolean, String) -> Unit) {
        val user = firebaseAuth.currentUser

        if (user != null && user.email == email) {
            val credential = EmailAuthProvider.getCredential(email, currentPass)

            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.delete().addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            onResult(true, "Account deleted successfully.")
                        } else {
                            onResult(false, "Failed to delete: ${deleteTask.exception?.message}")
                        }
                    }
                } else {
                    onResult(false, "Incorrect Password! Cannot delete account.")
                }
            }
        } else {
            onResult(false, "No user logged in.")
        }
    }

    fun sendPasswordReset(email: String, phone: String, onResult: (Boolean, String) -> Unit) {
        firestore.collection("Users")
            .whereEqualTo("email", email)
            .whereEqualTo("phone", phone)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onResult(false, "No account matches this email and phone number.")
                } else {
                    firebaseAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            onResult(true, "Reset link sent to your email")
                        }
                        .addOnFailureListener { e ->
                            onResult(false, "Error: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                onResult(false, "Database check failed: ${e.message}")
            }
    }
}
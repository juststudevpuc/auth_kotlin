package com.example.authfyab.ui1.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.authfyab.data.repository.AuthRepository

class AuthViewModel : ViewModel() {

    // Connect to the Repository we just made
    private val repository = AuthRepository()

    // LiveData acts like a radio broadcast. The Activity will "tune in" to listen to these.
    val loginSuccess = MutableLiveData<Boolean>()
    val toastMessage = MutableLiveData<String>()

    // NEW: Bell for Signup Success
    val signupSuccess = MutableLiveData<Boolean>()

    fun performLogin(email: String, pass: String) {
        // 1. Check for empty fields right here in the brain!
        if (email.isEmpty() || pass.isEmpty()) {
            toastMessage.value = "Fields cannot be empty"
            return
        }

        // 2. Ask the Repository to do the heavy lifting
        repository.loginUser(email, pass) { isSuccess, message ->
            if (isSuccess) {
                // Broadcast that we succeeded!
                loginSuccess.value = true
            } else {
                // Broadcast the error message
                toastMessage.value = message
            }
        }
    }

    // NEW: The Signup Check
    fun performSignup(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            toastMessage.value = "Fields cannot be empty"
            return
        }

        repository.registerUser(email, pass) { isSuccess, message ->
            if (isSuccess) {
                signupSuccess.value = true
                toastMessage.value = message // Optional: Show success toast
            } else {
                toastMessage.value = message
            }
        }
    }

    //    update on 01/5
    val passwordUpdateSuccess = MutableLiveData<Boolean>()

    fun changePassword(email: String, oldPass: String, newPass: String, confirmPass: String) {
        // 1. Check for empty fields
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            toastMessage.value = "Please fill out all password fields"
            return
        }

        // 2. Check if new passwords match
        if (newPass != confirmPass) {
            toastMessage.value = "New passwords do not match!"
            return
        }

        // 3. Check password strength
        if (newPass.length < 6) {
            toastMessage.value = "New password must be at least 6 characters"
            return
        }

        // 4. Send to the Repository
        repository.updatePassword(email, oldPass, newPass) { isSuccess, message ->
            if (isSuccess) {
                passwordUpdateSuccess.value = true // Rings the bell for success!
                toastMessage.value = message
            } else {
                toastMessage.value = message // Shows the error message
            }
        }
    }

    // NEW: Bell for Account Deletion Success
    val accountDeletionSuccess = MutableLiveData<Boolean>()

    fun deleteAccount(email: String, currentPass: String) {
        // 1. Check if they actually typed a password
        if (currentPass.isEmpty()) {
            toastMessage.value = "You must enter your current password to delete your account."
            return
        }

        // 2. Send request to the Repository
        repository.deleteUserAccount(email, currentPass) { isSuccess, message ->
            if (isSuccess) {
                accountDeletionSuccess.value = true // Ring the success bell!
                toastMessage.value = message
            } else {
                toastMessage.value = message // Show the error
            }
        }
    }
}
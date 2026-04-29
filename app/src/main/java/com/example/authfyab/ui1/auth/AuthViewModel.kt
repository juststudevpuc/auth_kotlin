package com.example.authfyab.ui.auth

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
}
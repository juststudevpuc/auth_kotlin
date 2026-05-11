package com.example.authfyab.ui1.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.authfyab.data.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    val loginSuccess = MutableLiveData<Boolean>()
    val toastMessage = MutableLiveData<String>()
    val signupSuccess = MutableLiveData<Boolean>()
    val passwordUpdateSuccess = MutableLiveData<Boolean>()
    val accountDeletionSuccess = MutableLiveData<Boolean>()
    val passwordResetSuccess = MutableLiveData<Boolean>()

    fun performLogin(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            toastMessage.value = "Fields cannot be empty"
            return
        }

        repository.loginUser(email, pass) { isSuccess, message ->
            if (isSuccess) {
                loginSuccess.value = true
            } else {
                toastMessage.value = message
            }
        }
    }

    fun performSignup(email: String, phone: String, pass: String) {
        if (email.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
            toastMessage.value = "Fields cannot be empty"
            return
        }

        repository.registerUser(email, phone, pass) { isSuccess, message ->
            if (isSuccess) {
                signupSuccess.value = true
                toastMessage.value = message
            } else {
                toastMessage.value = message
            }
        }
    }

    fun changePassword(email: String, oldPass: String, newPass: String, confirmPass: String) {
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            toastMessage.value = "Please fill out all password fields"
            return
        }

        if (newPass != confirmPass) {
            toastMessage.value = "New passwords do not match!"
            return
        }

        if (newPass.length < 6) {
            toastMessage.value = "New password must be at least 6 characters"
            return
        }

        repository.updatePassword(email, oldPass, newPass) { isSuccess, message ->
            if (isSuccess) {
                passwordUpdateSuccess.value = true
                toastMessage.value = message
            } else {
                toastMessage.value = message
            }
        }
    }

    fun deleteAccount(email: String, currentPass: String) {
        if (currentPass.isEmpty()) {
            toastMessage.value = "You must enter your current password to delete your account."
            return
        }

        repository.deleteUserAccount(email, currentPass) { isSuccess, message ->
            if (isSuccess) {
                accountDeletionSuccess.value = true
                toastMessage.value = message
            } else {
                toastMessage.value = message
            }
        }
    }

    fun resetPassword(email: String, phone: String) {
        if (email.isEmpty() || phone.isEmpty()) {
            toastMessage.value = "Please enter both email and phone number"
            return
        }

        repository.sendPasswordReset(email, phone) { success, message ->
            toastMessage.value = message

            if (success) {
                passwordResetSuccess.value = true
            }
        }
    }
}
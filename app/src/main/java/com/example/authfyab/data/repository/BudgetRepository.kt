package com.example.authfyab.data.repository

import com.example.authfyab.data.model.ExpenseItem
import com.example.authfyab.data.model.MonthlyBudget
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BudgetRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // A quick helper to always get the current logged-in user's ID
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // 1. Save or Update their Total Salary
    // Fetch their saved Salary when the app opens
    fun getSalary(onResult: (Boolean, Double, String) -> Unit) {
        val userId = getCurrentUserId()
        if (userId == null) {
            onResult(false, 0.0, "User not logged in")
            return
        }

        firestore.collection("Users").document(userId)
            .collection("Budget").document("Summary")
            .get()
            .addOnSuccessListener { document ->
                // Grab the saved salary, or default to 0.0 if they don't have one yet
                val salary = document.getDouble("totalSalary") ?: 0.0
                onResult(true, salary, "Success")
            }
            .addOnFailureListener { e ->
                onResult(false, 0.0, "Failed to load salary")
            }
    }

    // Save the Permanent Salary to Firebase
    fun saveSalary(salary: Double, onResult: (Boolean, String) -> Unit) {
        val userId = getCurrentUserId() // Grab the logged-in user
        if (userId == null) {
            onResult(false, "User not logged in")
            return
        }

        // We wrap the salary in a HashMap to send it to the cloud
        val data = hashMapOf(
            "totalSalary" to salary
        )

        // Save it in a special "Summary" document
        firestore.collection("Users").document(userId)
            .collection("Budget").document("Summary")
            .set(data, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                onResult(true, "Salary saved forever!")
            }
            .addOnFailureListener { e ->
                onResult(false, "Failed to save: ${e.message}")
            }
    }

    // 2. Add a new Expense (like Rent or Water)
    fun addExpense(expenseName: String, amount: Double, onResult: (Boolean, String) -> Unit) {
        val userId = getCurrentUserId()
        if (userId == null) {
            onResult(false, "User not logged in")
            return
        }

        // Let Firebase generate a random, unique ID for this new bill
        val newExpenseRef = firestore.collection("Users").document(userId)
            .collection("Expenses").document()

        val newExpense = ExpenseItem(
            id = newExpenseRef.id, // Save the generated ID inside the object
            name = expenseName,
            amount = amount,
            isPaid = false
        )

        newExpenseRef.set(newExpense)
            .addOnSuccessListener { onResult(true, "Expense added!") }
            .addOnFailureListener { e -> onResult(false, "Failed to add expense: ${e.message}") }
    }

    // 3. Fetch all their Expenses to display on the screen
    fun getExpenses(onResult: (Boolean, List<ExpenseItem>, String) -> Unit) {
        val userId = getCurrentUserId()
        if (userId == null) {
            onResult(false, emptyList(), "User not logged in")
            return
        }

        firestore.collection("Users").document(userId)
            .collection("Expenses")
            .get()
            .addOnSuccessListener { documents ->
                val expenseList = mutableListOf<ExpenseItem>()
                for (document in documents) {
                    val expense = document.toObject(ExpenseItem::class.java)
                    expenseList.add(expense)
                }
                // Send the list of bills back up to the Manager (ViewModel)
                onResult(true, expenseList, "Success")
            }
            .addOnFailureListener { e ->
                onResult(false, emptyList(), "Error loading data: ${e.message}")
            }
    }

    // 4. Delete an Expense from Firebase
    fun deleteExpense(expenseId: String, onResult: (Boolean, String) -> Unit) {
        val userId = getCurrentUserId()
        if (userId == null) {
            onResult(false, "User not logged in")
            return
        }

        // Find the exact bill using its unique ID and delete it
        firestore.collection("Users").document(userId)
            .collection("Expenses").document(expenseId)
            .delete()
            .addOnSuccessListener { onResult(true, "Expense deleted!") }
            .addOnFailureListener { e -> onResult(false, "Failed to delete: ${e.message}") }
    }
}
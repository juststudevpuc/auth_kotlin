package com.example.authfyab.data.model

// This holds their main salary info
data class MonthlyBudget(
    val totalSalary: Double = 0.0,
    val targetSavingsGoal: Double = 0.0 // We can calculate this later based on their goals
)

// This holds the individual bills they add (like "Rent" or "Water")
data class ExpenseItem(
    val id: String = "",       // A unique ID so Firebase knows which bill is which
    val name: String = "",     // e.g., "Electricity"
    val amount: Double = 0.0,  // e.g., 50.50
    val isPaid: Boolean = false // A checkbox for them to mark it as paid
)
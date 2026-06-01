package com.example.authfyab.ui1.budget

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.authfyab.data.model.ExpenseItem
import com.example.authfyab.data.repository.BudgetRepository

class BudgetViewModel : ViewModel() {

    // Connect to the Kitchen
    private val repository = BudgetRepository()

    // The "Bells" that the UI will listen to
    val expenseList = MutableLiveData<List<ExpenseItem>>()
    val totalExpenses = MutableLiveData<Double>()
    val safeToSave = MutableLiveData<Double>()
    val toastMessage = MutableLiveData<String>()

    val currentSalary = MutableLiveData<Double>()

    // 1. Fetch the bills and do the Math!
    fun loadExpenses(currentSalary: Double) {
        repository.getExpenses { isSuccess, expenses, message ->
            if (isSuccess) {
                // Send the list to the screen
                expenseList.value = expenses

                // Math Time: Add up all the bills
                var calculatedTotal = 0.0
                for (item in expenses) {
                    calculatedTotal += item.amount
                }
                totalExpenses.value = calculatedTotal

                // Math Time: Calculate what is safe to save
                safeToSave.value = currentSalary - calculatedTotal
            } else {
                toastMessage.value = message
            }
        }
    }

    fun loadInitialData() {
        repository.getSalary { isSuccess, salary, _ ->
            if (isSuccess) {
                currentSalary.value = salary
                // Now that we have the real salary, load the bills and do the math!
                loadExpenses(salary)
            }
        }
    }

    // 2. Add a new bill from the screen
    fun addNewExpense(name: String, amountText: String, currentSalary: Double) {
        // Safety check: Did they leave it blank?
        if (name.isEmpty() || amountText.isEmpty()) {
            toastMessage.value = "Please fill out both the Name and Amount"
            return
        }

        // Safety check: Is the amount a real number?
        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            toastMessage.value = "Please enter a valid money amount"
            return
        }

        // Send it to the Kitchen to save
        repository.addExpense(name, amount) { isSuccess, message ->
            toastMessage.value = message

            if (isSuccess) {
                // If it saved successfully, reload the list and recalculate the math!
                loadExpenses(currentSalary)
            }
        }
    }

    // 3. Save their Salary to the database
    fun saveMySalary(salaryText: String) {
        val salary = salaryText.toDoubleOrNull()

        if (salary == null || salary <= 0) {
            toastMessage.value = "Please enter a valid salary"
            return
        }

        repository.saveSalary(salary) { isSuccess, message ->
            toastMessage.value = message
            if (isSuccess) {
                // Recalculate everything with the new official salary
                loadExpenses(salary)
            }
        }
    }
    // 4. Delete the bill and redo the math
    fun removeExpense(expense: ExpenseItem, currentSalary: Double) {
        repository.deleteExpense(expense.id) { isSuccess, message ->
            toastMessage.value = message

            if (isSuccess) {
                // If it successfully deleted from the database, reload the list!
                loadExpenses(currentSalary)
            }
        }
    }
}
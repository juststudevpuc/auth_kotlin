package com.example.authfyab.ui1.budget

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.authfyab.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var budgetViewModel: BudgetViewModel
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This is the magic line that connects to your activity_dashboard.xml!
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Initialize the Brain (ViewModel)
        budgetViewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)

        // 2. Setup the RecyclerView List with the Long-Click Delete feature
        expenseAdapter = ExpenseAdapter(emptyList()) { clickedExpense ->
            // This code runs when they LONG PRESS a bill
            android.app.AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete ${clickedExpense.name}?")
                .setPositiveButton("Delete") { _, _ ->
                    // Grab the current salary from the text box to redo the math
                    val currentSalaryStr = binding.etSalary.text.toString().trim()
                    val salary = if (currentSalaryStr.isEmpty()) 0.0 else currentSalaryStr.toDouble()

                    budgetViewModel.removeExpense(clickedExpense, salary)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        binding.rvExpenses.layoutManager = LinearLayoutManager(this)

        // Attach the adapter to the screen
        binding.rvExpenses.adapter = expenseAdapter

        // Load any existing bills from Firebase immediately
        budgetViewModel.loadInitialData()
        // The new Save Salary Button
        binding.btnSaveSalary.setOnClickListener {
            val salaryStr = binding.etSalary.text.toString().trim()
            budgetViewModel.saveMySalary(salaryStr)
        }

        // 3. "Add Expense" Button Click
        binding.btnAddExpense.setOnClickListener {
            val name = binding.etExpenseName.text.toString().trim()
            val amountStr = binding.etExpenseAmount.text.toString().trim()

            // Grab whatever they typed in the Salary box
            val currentSalaryStr = binding.etSalary.text.toString().trim()
            val salary = if (currentSalaryStr.isEmpty()) 0.0 else currentSalaryStr.toDouble()

            // Send to the brain!
            budgetViewModel.addNewExpense(name, amountStr, salary)

            // Clear the small input boxes so they can type the next bill quickly
            binding.etExpenseName.text?.clear()
            binding.etExpenseAmount.text?.clear()
        }

        // 4. Listeners: Wait for the ViewModel to ring the bells
        // Listen for new bills
        budgetViewModel.currentSalary.observe(this) { salary ->
            // Only update the text box if it's currently empty
            if (binding.etSalary.text.toString().isEmpty() && salary > 0) {
                binding.etSalary.setText(salary.toString())
            }
            budgetViewModel.expenseList.observe(this) { list ->
                expenseAdapter.updateData(list)
            }
        }

        // Listen for math updates
        budgetViewModel.safeToSave.observe(this) { amount ->
            binding.tvSafeToSave.text = "$${String.format("%.2f", amount)}"
        }

        // Listen for errors or success messages
        budgetViewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
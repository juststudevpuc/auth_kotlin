package com.example.authfyab.ui1.budget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.authfyab.R
import com.example.authfyab.data.model.ExpenseItem

class ExpenseAdapter(
    private var expenseList: List<ExpenseItem>,
    private val onDeleteClick: (ExpenseItem) -> Unit // Renamed to make more sense!
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    fun updateData(newList: List<ExpenseItem>) {
        expenseList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]

        holder.nameText.text = expense.name
        holder.amountText.text = "$${expense.amount}"
        holder.checkBox.isChecked = expense.isPaid

        // NEW: Listen for a normal click specifically on the Delete button!
        holder.deleteButton.setOnClickListener {
            onDeleteClick(expense)
        }
    }

    override fun getItemCount(): Int = expenseList.size

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.tvExpenseNameItem)
        val amountText: TextView = itemView.findViewById(R.id.tvExpenseAmountItem)
        val checkBox: CheckBox = itemView.findViewById(R.id.cbExpensePaid)
        // Grab the new delete button from the XML
        val deleteButton: ImageView = itemView.findViewById(R.id.btnDeleteExpense)
    }
}
package com.example.authfyab.ui1.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.authfyab.R
import com.example.authfyab.data.model.UserProfile
import com.google.android.material.button.MaterialButton // We need this for your new buttons!

// 1. Notice the two NEW variables in the constructor. These are our "messengers"
class UserAdapter(
    private val userList: List<UserProfile>,
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_card, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        // Draw the text
        holder.emailText.text = currentUser.email
        holder.roleText.text = "Role: ${currentUser.role}"


    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emailText: TextView = itemView.findViewById(R.id.tvEmail)
        val roleText: TextView = itemView.findViewById(R.id.tvRole)


    }
}
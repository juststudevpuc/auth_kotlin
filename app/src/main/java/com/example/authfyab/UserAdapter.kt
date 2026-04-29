package com.example.authfyab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// We pass in a list of UserProfile objects when we create this Adapter
class UserAdapter(private val userList: List<UserProfile>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // 1. This creates the physical card on the screen using your XML layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_card, parent, false)
        return UserViewHolder(view)
    }

    // 2. This plugs the actual database text into the visual card
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.emailText.text = currentUser.email
        holder.roleText.text = "Role: ${currentUser.role}"
    }

    // 3. This tells the system exactly how many cards to draw
    override fun getItemCount(): Int {
        return userList.size
    }

    // The ViewHolder acts like a map, pointing to the IDs in your item_user_card.xml
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emailText: TextView = itemView.findViewById(R.id.tvEmail)
        val roleText: TextView = itemView.findViewById(R.id.tvRole)
    }
}
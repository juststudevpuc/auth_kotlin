package com.example.authfyab.ui1.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.authfyab.data.model.UserProfile
import com.example.authfyab.databinding.ActivityMainBinding
import com.example.authfyab.ui1.auth.SigninActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userAdapter: UserAdapter

    private val userList = mutableListOf<UserProfile>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- FIX: THESE TWO LINES MUST BE FIRST ---
        // You MUST inflate the binding before you can touch any buttons or text views.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- FIX: MOVED THIS DOWN HERE ---
        // Now it is safe to make the profile picture clickable!
        binding.profileCard.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Setup RecyclerView using Binding
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(userList)
        binding.recyclerViewUsers.adapter = userAdapter

        // Set up Logout using Binding
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set up the Dynamic Profile Initial
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null && currentUser.email != null) {
            val email = currentUser.email!!
            if (email.isNotEmpty()) {
                val firstLetter = email.substring(0, 1).uppercase()
                binding.tvProfileInitial.text = firstLetter
            }
        }

        // Fetch the data
        fetchUsers()
    }

    private fun fetchUsers() {
        db.collection("Users")
            .get()
            .addOnSuccessListener { documents ->
                userList.clear()
                for (document in documents) {
                    val user = document.toObject(UserProfile::class.java)
                    userList.add(user)
                }
                userAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load users: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
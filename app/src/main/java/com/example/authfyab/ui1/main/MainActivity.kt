package com.example.authfyab.ui1.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.authfyab.R
import com.example.authfyab.data.model.UserProfile
import com.example.authfyab.ui1.auth.SigninActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    // Declare your variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var btnLogout: MaterialButton // Added the logout button variable
    private val userList = mutableListOf<UserProfile>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Find the views by their IDs from your XML
        recyclerView = findViewById(R.id.recyclerViewUsers)
        btnLogout = findViewById(R.id.btn_logout) // Hooking up the button!

        // 2. Set the layout manager (this tells it to stack vertically)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 3. Attach the Adapter we created earlier
        userAdapter = UserAdapter(userList)
        recyclerView.adapter = userAdapter

        // 4. Set up the Logout Logic
        btnLogout.setOnClickListener {
            // Tell Firebase to log the user out
            FirebaseAuth.getInstance().signOut()

            // Send them back to the Login Screen
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)

            // Destroy the dashboard so they can't hit the "Back" button to return
            finish()
        }

        // 5. Fetch the data!
        fetchUsers()
    }

    private fun fetchUsers() {
        db.collection("Users")
            .get()
            .addOnSuccessListener { documents ->
                // Clear out the list so we don't get duplicates if we refresh
                userList.clear()

                for (document in documents) {
                    val user = document.toObject(UserProfile::class.java)
                    userList.add(user)
                }

                // Tell the adapter the data has arrived so it can draw the cards!
                userAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load users: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
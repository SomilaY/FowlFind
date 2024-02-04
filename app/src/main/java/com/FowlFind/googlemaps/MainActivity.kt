package com.FowlFind.googlemaps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var createAccountButton: Button
    private lateinit var textViewAlreadyHaveAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        val auth = FirebaseAuth.getInstance()


        // Initialize your UI elements
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        createAccountButton = findViewById(R.id.createAccountButton)
        textViewAlreadyHaveAccount = findViewById(R.id.textViewAlreadyHaveAccount)

        // Set an onClickListener for the createAccountButton
        createAccountButton.setOnClickListener {
            // Get the text from EditText fields
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Check if the password is at least 8 characters
            if (password.length < 8) {
                // Display a password length error message
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a new user with Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign up success, update UI with the signed-in user's information
                        Toast.makeText(this, "Sign-up successful!", Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        // You can now use 'user' to access the current user's details
                    } else {
                        // If sign up fails, display a message to the user.
                        Toast.makeText(this, "Sign-up failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }


        // Set an onClickListener for the "Already have an account? Sign in" TextView
        textViewAlreadyHaveAccount.setOnClickListener {
            val intent = Intent(this, signin::class.java)
            startActivity(intent)
        }
    }
}
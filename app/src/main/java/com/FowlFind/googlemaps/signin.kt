package com.FowlFind.googlemaps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class signin : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var donthaveaccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        // Initialize Firebase Auth
        val auth = FirebaseAuth.getInstance()

        // Initialize your UI elements
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.LoginButton)
        donthaveaccount = findViewById(R.id.DontHaveAccount)


        // Set an onClickListener for the loginButton
        loginButton.setOnClickListener {
            Log.d("LoginActivity", "Login button clicked")

            // Get the text from EditText fields
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            Log.d("LoginActivity", "auth: $auth")
            Log.d("LoginActivity", "emailEditText: $emailEditText")
            Log.d("LoginActivity", "passwordEditText: $passwordEditText")
            Log.d("LoginActivity", "Email: $email")
            Log.d("LoginActivity", "Password: $password")

            // Sign in with Firebase Auth
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("LoginActivity", "signInWithEmail:success")
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        // You can now use 'user' to access the current user's details
                        val intent = Intent(this, MapsActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }
        }


        donthaveaccount.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

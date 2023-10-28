package com.example.gdsc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException

class SignUpActivity : AppCompatActivity() {

    lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val loginButton = findViewById<ImageView>(R.id.btn_login)
        val name = findViewById<EditText>(R.id.name)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.pass)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Initialize Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // LogIn Button work
        loginButton.setOnClickListener {

            //Getting data
            val nameStr = name.text.toString()
            val emailStr = email.text.toString()
            val passwordStr = password.text.toString()
            val confirmPasswordStr = confirmPassword.text.toString()

            // Checking password same or not
            if (passwordStr == confirmPasswordStr) {
                // Passwords match, proceed with registration
                auth.createUserWithEmailAndPassword(emailStr, passwordStr)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registration successful
                            val user = task.result?.user
                            Toast.makeText(this, "User Registered", Toast.LENGTH_SHORT).show()
                            val openLogInActivity = Intent(this, LogInActivity::class.java)
                            startActivity(openLogInActivity)
                        } else {
                            // Registration failed
                            Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Passwords do not match
                Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
            }
        }

        val googleSignInButton = findViewById<Button>(R.id.button_google)
        googleSignInButton.setOnClickListener {
            // Start Google Sign-In process
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        val signInText = findViewById<TextView>(R.id.log)
        // for Entering in LogInActivity
        signInText.setOnClickListener {
            val nameStr = name.text.toString()
            val openSignInActivity = Intent(this, LogInActivity::class.java)
            intent.putExtra("USER_NAME",nameStr)

            startActivity(openSignInActivity)
        }
    }

    // Handle Google Sign-In result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign-In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign-In failed
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Authenticate with Firebase using Google Sign-In credentials
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Google Sign-In is successful
                    val user = task.result?.user
                    val userName = user?.displayName // Get the user's name
                    // Create an intent to start the AfterLoginActivity
                    val intent = Intent(applicationContext,AfterLoginActivity::class.java)

                    // Pass the user's name as an extra to the AfterLoginActivity
                    intent.putExtra("USER_NAME", userName)

                    // Start the AfterLoginActivity
                    startActivity(intent)

                    // Finish the current activity (optional)
                    finish()
                    // You can handle the user's registration as needed, e.g., storing additional user data in the database.
                } else {
                    // Google Sign-In failed
                    Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        const val RC_SIGN_IN = 9001 // Request code for Google Sign-In
    }
}

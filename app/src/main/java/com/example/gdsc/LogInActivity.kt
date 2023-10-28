package com.example.gdsc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LogInActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        val loginButton = findViewById<ImageView>(R.id.btn_login)
        val name = findViewById<EditText>(R.id.name)
        val userpass = findViewById<EditText>(R.id.password)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        // Initialize Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        databaseReference = FirebaseDatabase.getInstance().getReference("Users")


        // After login button click
        loginButton.setOnClickListener{
            val namestr = name.text.toString()
            val pass = userpass.text.toString()

        // Checking if text field is empty or not
        if(namestr.isNotEmpty() && pass.isNotEmpty()){
            readData(namestr,pass)
        } else{
            Toast.makeText(this, "Please enter Email and password", Toast.LENGTH_SHORT).show()
        }

        }
        val googleSignInButton = findViewById<Button>(R.id.button_google)
        googleSignInButton.setOnClickListener {
            // Start Google Sign-In process
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, SignUpActivity.RC_SIGN_IN)
        }

        val signIntext = findViewById<TextView>(R.id.register)
        signIntext.setOnClickListener {
            val openSignUpActivity = Intent(this, SignUpActivity::class.java)
            startActivity(openSignUpActivity)
        }

    }

    // Handle Google Sign-In result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SignUpActivity.RC_SIGN_IN) {
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

    private fun readData(name : String,pass : String){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.child(name).get().addOnSuccessListener{
            if(it.exists()){
                val storedpass = it.child("password").value
                if (pass == storedpass) {
                    val intent = Intent(applicationContext,AfterLoginActivity::class.java)

                    intent.putExtra("USER_NAME", name)

                    // Start the AfterLoginActivity
                    startActivity(intent)
                   Toast.makeText(this,"You successfully Logged In",Toast.LENGTH_LONG).show()
                } else {
                    // Password doesn't match
                    Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed, Error in DB", Toast.LENGTH_SHORT).show()
        }
    }

}

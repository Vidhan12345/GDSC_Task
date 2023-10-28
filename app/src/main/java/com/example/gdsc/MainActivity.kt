package com.example.gdsc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val signIn = findViewById<AppCompatButton>(R.id.sign_in)
        val login = findViewById<AppCompatButton>(R.id.log_in)

        signIn.setOnClickListener{
            val intent = Intent(applicationContext,SignUpActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener{
            val intent = Intent(applicationContext,LogInActivity::class.java)
            startActivity(intent)
        }
    }
}
package com.example.gdsc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class AfterLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_login)
        val userName = intent.getStringExtra("USER_NAME")

        val welcomeMessage = findViewById<TextView>(R.id.message)

        if (userName != null) {
            welcomeMessage.text = "Welcome, $userName!"
        } else {
            welcomeMessage.text = "Welcome!"
        }
    }
}
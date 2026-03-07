package com.example.sosautomationapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Redirect to SplashActivity which handles the routing logic
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }
}

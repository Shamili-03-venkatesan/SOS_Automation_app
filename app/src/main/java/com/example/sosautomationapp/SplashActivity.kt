package com.example.sosautomationapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = getSharedPreferences("SOS_PREF", MODE_PRIVATE)

        val isLoggedIn = pref.getBoolean("isLoggedIn", false)
        val isWelcomeShown = pref.getBoolean("isWelcomeShown", false)

        when {
            isLoggedIn -> {
                startActivity(Intent(this, HomeActivity::class.java))
            }
            !isWelcomeShown -> {
                startActivity(Intent(this, WelcomeActivity::class.java))
            }
            else -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        finish()
    }
}

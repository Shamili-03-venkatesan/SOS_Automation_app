package com.example.sosautomationapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val btnContinue: ImageButton = findViewById(R.id.btnContinue) // ✅ FIX: ID corrected

        btnContinue.setOnClickListener {
            val pref = getSharedPreferences("SOS_PREF", MODE_PRIVATE)
            pref.edit().putBoolean("isWelcomeShown", true).apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

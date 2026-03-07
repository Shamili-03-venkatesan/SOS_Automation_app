package com.example.sosautomationapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 ALREADY LOGGED IN CHECK
        val pref = getSharedPreferences("SOS_PREF", MODE_PRIVATE)
        if (pref.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val etName = findViewById<EditText>(R.id.etName)
        val etAge = findViewById<EditText>(R.id.etage)
        val etMobile = findViewById<EditText>(R.id.etMobile)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etGuardianName = findViewById<EditText>(R.id.etGuardianName)
        val etGuardianMobile = findViewById<EditText>(R.id.etGuardianMobile)
        val etNative = findViewById<EditText>(R.id.etNative)

        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {

            val name = etName.text.toString().trim()
            val age = etAge.text.toString().trim()
            var mobile = etMobile.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val guardianName = etGuardianName.text.toString().trim()
            var guardianMobile = etGuardianMobile.text.toString().trim()
            val nativePlace = etNative.text.toString().trim()

            // 🔴 VALIDATION
            when {
                name.isEmpty() -> {
                    etName.error = "Name is required"
                    etName.requestFocus()
                    return@setOnClickListener
                }
                age.isEmpty() -> {
                    etAge.error = "Age is required"
                    etAge.requestFocus()
                    return@setOnClickListener
                }
                mobile.length < 10 -> {
                    etMobile.error = "Enter valid mobile number"
                    etMobile.requestFocus()
                    return@setOnClickListener
                }
                guardianName.isEmpty() -> {
                    etGuardianName.error = "Guardian name is required"
                    etGuardianName.requestFocus()
                    return@setOnClickListener
                }
                guardianMobile.length < 10 -> {
                    etGuardianMobile.error = "Enter valid guardian mobile"
                    etGuardianMobile.requestFocus()
                    return@setOnClickListener
                }
            }

            // 🔹 AUTO +91 ADD
            if (!mobile.startsWith("+91")) mobile = "+91$mobile"
            if (!guardianMobile.startsWith("+91")) guardianMobile = "+91$guardianMobile"

            // 🔴 SAME NUMBER CHECK
            if (mobile == guardianMobile) {
                Toast.makeText(
                    this,
                    "Guardian number must be different from your number",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // ✅ SAVE DATA + LOGIN FLAG
            val sharedPref = getSharedPreferences("SOS_PREF", MODE_PRIVATE)
            sharedPref.edit().apply {
                putString("name", name)
                putString("age", age)
                putString("mobile", mobile)
                putString("email", email)
                putString("guardianName", guardianName)
                putString("guardianMobile", guardianMobile)
                putString("native", nativePlace)

                // 🔹 IMPORTANT
                putBoolean("isLoggedIn", true)

                apply()
            }

            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

            // ✅ GO TO HOME
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}

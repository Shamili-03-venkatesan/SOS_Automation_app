package com.example.sosautomationapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etMobile: EditText
    private lateinit var etEmail: EditText
    private lateinit var etGuardianName: EditText
    private lateinit var etGuardianMobile: EditText
    private lateinit var etNative: EditText
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // 🔹 Bind Views
        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etMobile = findViewById(R.id.etMobile)
        etEmail = findViewById(R.id.etEmail)
        etGuardianName = findViewById(R.id.etGuardianName)
        etGuardianMobile = findViewById(R.id.etGuardianMobile)
        etNative = findViewById(R.id.etNative)
        btnSave = findViewById(R.id.btnSave)
        btnLogout = findViewById(R.id.btnLogout)

        val sharedPref = getSharedPreferences("SOS_PREF", MODE_PRIVATE)

        // 🔹 Load existing data into EditTexts
        etName.setText(sharedPref.getString("name", ""))
        etAge.setText(sharedPref.getString("age", ""))
        etMobile.setText(sharedPref.getString("mobile", ""))
        etEmail.setText(sharedPref.getString("email", ""))
        etGuardianName.setText(sharedPref.getString("guardianName", ""))
        etGuardianMobile.setText(sharedPref.getString("guardianMobile", ""))
        etNative.setText(sharedPref.getString("native", ""))

        // 🔹 Save Action
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val age = etAge.text.toString().trim()
            val mobile = etMobile.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val gName = etGuardianName.text.toString().trim()
            val gMobile = etGuardianMobile.text.toString().trim()
            val native = etNative.text.toString().trim()

            if (name.isEmpty() || mobile.isEmpty() || gMobile.isEmpty()) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            } else {
                sharedPref.edit().apply {
                    putString("name", name)
                    putString("age", age)
                    putString("mobile", mobile)
                    putString("email", email)
                    putString("guardianName", gName)
                    putString("guardianMobile", gMobile)
                    putString("native", native)
                    apply()
                }
                Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔴 Logout Action
        btnLogout.setOnClickListener {
            sharedPref.edit()
                .putBoolean("isLoggedIn", false)
                .putBoolean("isWelcomeShown", false)
                .apply()

            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

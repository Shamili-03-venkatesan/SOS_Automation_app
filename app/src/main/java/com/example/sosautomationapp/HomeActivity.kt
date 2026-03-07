package com.example.sosautomationapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.ImageButton
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.gms.location.LocationServices
import java.io.File

class HomeActivity : AppCompatActivity() {

    private lateinit var photoUri: Uri
    private lateinit var sosMessage: String
    private lateinit var drawerLayout: DrawerLayout

    private var name = ""
    private var guardianName = ""
    private var nativePlace = ""
    private var guardianMobile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.navView)
        val btnMenu: ImageButton = findViewById(R.id.btnMenu)

        // 🔹 Open Drawer
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 🔹 Drawer Item Selection
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                R.id.nav_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                }
                R.id.nav_logout -> {
                    logoutUser()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val pref = getSharedPreferences("SOS_PREF", MODE_PRIVATE)
        name = pref.getString("name", "") ?: ""
        guardianName = pref.getString("guardianName", "") ?: ""
        guardianMobile = pref.getString("guardianMobile", "") ?: ""
        nativePlace = pref.getString("native", "") ?: ""

        val btnSOS = findViewById<Button>(R.id.btnSOS)

        btnSOS.setOnClickListener {
            if (!ensureLocationEnabled()) return@setOnClickListener
            if (!hasPermission(Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
                return@setOnClickListener
            }
            openCamera()
        }
    }

    private fun logoutUser() {
        val sharedPref = getSharedPreferences("SOS_PREF", MODE_PRIVATE)
        sharedPref.edit()
            .putBoolean("isLoggedIn", false)
            .putBoolean("isWelcomeShown", false)
            .apply()

        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // 📷 CAMERA
    private fun openCamera() {
        val imageFile = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "SOS_${System.currentTimeMillis()}.jpg"
        )
        photoUri = FileProvider.getUriForFile(this, "$packageName.provider", imageFile)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(intent, 201)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 201 && resultCode == Activity.RESULT_OK) {
            fetchLiveLocation()
        }
        if (requestCode == 501) {
            openWhatsApp()
        }
    }
    private fun fetchLiveLocation() {
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 301)
            return
        }
        val client = LocationServices.getFusedLocationProviderClient(this)
        client.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location ->
            val mapUrl = "https://maps.google.com/?q=${location.latitude},${location.longitude}"
            sosMessage = """
🚨 SOS ALERT 🚨
I am in Danger
Name: $name
Guardian: $guardianName
Guardian Mobile number : $guardianMobile
Native place: $nativePlace
 MY Current Location: $mapUrl
            """.trimIndent()
            openSMS()
        }
    }

    private fun openSMS() {
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$guardianMobile")
            putExtra("sms_body", sosMessage)
        }
        startActivityForResult(smsIntent, 501)
    }

    private fun openWhatsApp() {
        // Remove all non-digit characters from the phone number
        val cleanNumber = guardianMobile.filter { it.isDigit() }
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            setPackage("com.whatsapp")
            type = "image/*"
            putExtra(Intent.EXTRA_TEXT, sosMessage)
            putExtra(Intent.EXTRA_STREAM, photoUri)
            // This 'jid' extra tells WhatsApp which contact to open directly
            putExtra("jid", "$cleanNumber@s.whatsapp.net")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Try WhatsApp Business as a fallback
            try {
                intent.setPackage("com.whatsapp.w4b")
                startActivity(intent)
            } catch (e2: Exception) {
                Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ensureLocationEnabled(): Boolean {
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please turn ON Location", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return false
        }
        return true
    }

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}

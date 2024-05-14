package com.example.wavesoffood.AdminActivities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.ActivityAdminSplashBinding

class AdminSplashActivity : AppCompatActivity() {

    lateinit var binding: ActivityAdminSplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSplashBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_admin_splash)

        Handler().postDelayed({
            startActivity(Intent(this@AdminSplashActivity, AdminSignUpActivity::class.java))
            finish()
        },3000)

    }
}
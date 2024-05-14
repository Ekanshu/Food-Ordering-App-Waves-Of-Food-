package com.example.wavesoffood.AdminActivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wavesoffood.databinding.ActivityAdminNewUserBinding

class AdminNewUserActivity : AppCompatActivity() {

    lateinit var binding: ActivityAdminNewUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminNewUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }


    }
}
package com.example.wavesoffood.AdminActivities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Adapters.DeliveryItemAdapter
import com.example.wavesoffood.databinding.ActivityOutForDeliveryBinding

class OutForDeliveryActivity : AppCompatActivity() {

    lateinit var binding: ActivityOutForDeliveryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutForDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val arrlstCustomerNames = arrayListOf("Rohit Sharma", "Virat Kohli", "MS Dhoni", "Alzari Joseph", "Kumara Sangakara")
        val arrlstPaymentStatus = arrayListOf("received", "pending", "notReceived", "received", "pending")
        val adapter = DeliveryItemAdapter(this, arrlstCustomerNames, arrlstPaymentStatus)
        binding.rvDelivery.layoutManager = LinearLayoutManager(this)
        binding.rvDelivery.adapter = adapter

        binding.backArrow.setOnClickListener {
            startActivity(Intent(this@OutForDeliveryActivity, AdminMainActivity::class.java))
            finish()
        }

    }
}
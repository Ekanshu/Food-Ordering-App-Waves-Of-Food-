package com.example.wavesoffood

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wavesoffood.Model.OrderInfo
import com.example.wavesoffood.databinding.ActivityOrderDetailedBinding

class OrderDetailedActivity : AppCompatActivity() {

    private val binding: ActivityOrderDetailedBinding by lazy {
        ActivityOrderDetailedBinding.inflate(layoutInflater)
    }

    private lateinit var orderInfo: OrderInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        orderInfo = intent.getSerializableExtra("orderInfo") as OrderInfo

        var orderedItems = "\n"
        var rate = "\n"
        var quantity = "\n"
        var amount = "\n"
        for(dishName in orderInfo.orderedItems!!){
            orderedItems += dishName.dishName+"\n\n"
            rate += "$"+dishName.dishPrice+"\n\n"
            quantity += "x "+dishName.dishQuantity+"\n\n"
            amount += "= $"+dishName.dishPrice!!.toInt()*dishName.dishQuantity!!.toInt()+"\n\n"
        }
        binding.tvOrderedItems2.text = orderedItems
        binding.tvItemRate2.text = rate
        binding.tvItemQuantity2.text = quantity
        binding.tvItemTotalPrice2.text = amount
        binding.tvTotalAmount2.text = "$"+orderInfo.billAmount

        binding.etOrderedBy.setText(orderInfo.orderedBy.toString())
        binding.etOrderedOn.setText(intent.getStringExtra("orderTime"))
        binding.etDeliveryAddress.setText(orderInfo.deliveryAddress)

        binding.btnBackArrow.setOnClickListener {
            onBackPressed()
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }


    }
}
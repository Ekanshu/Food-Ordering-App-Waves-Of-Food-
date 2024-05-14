package com.example.wavesoffood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.wavesoffood.databinding.ActivityLocationBinding
import com.example.wavesoffood.databinding.ActivityLoginBinding

class LocationActivity : AppCompatActivity() {

    val binding: ActivityLocationBinding by lazy {
        ActivityLocationBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val locationList = arrayOf("Karnal", "Kurukshetra", "Panipat", "Ambala", "Yamunanagar", "Kaithal", "Jind")
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,locationList)
        val autoCompleteTextView = binding.lstOfLocations
        autoCompleteTextView.setAdapter(adapter)


    }
}
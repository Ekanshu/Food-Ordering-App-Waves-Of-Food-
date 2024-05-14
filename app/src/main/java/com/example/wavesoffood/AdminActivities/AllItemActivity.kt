package com.example.wavesoffood.AdminActivities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Adapters.AllMenuAdapter
import com.example.wavesoffood.AdminActivities.Model.FoodItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.example.wavesoffood.databinding.ActivityAllItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class AllItemActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    var arrlstFoodItems: ArrayList<FoodItem> = arrayListOf()

    val binding: ActivityAllItemBinding by lazy {
        ActivityAllItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        fetchItems()

        binding.backArrow.setOnClickListener {
            startActivity(Intent(this@AllItemActivity, AdminMainActivity::class.java))
        }


    }

    private fun fetchItems(){
        db.reference.child("Menu")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrlstFoodItems.clear()
                    for(foodSnapshot in snapshot.children){
                        val foodItem = foodSnapshot.getValue(FoodItem::class.java)
                        foodItem?.let {
                            arrlstFoodItems.add(it)
                        }
                    }
                    setAdapter()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("foody", "Hii i am foody.")
                }
            })
    }

    private fun setAdapter() {
        val adapter = AllMenuAdapter(this, arrlstFoodItems)
        binding.rvAllItems.layoutManager = LinearLayoutManager(this)
        binding.rvAllItems.adapter = adapter

    }

}
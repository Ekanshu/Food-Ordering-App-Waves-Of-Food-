package com.example.wavesoffood.AdminActivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Adapters.PendingOrdersAdapter
import com.example.wavesoffood.Model.OrderInfo
import com.example.wavesoffood.databinding.ActivityPendingOrdersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingOrdersActivity : AppCompatActivity() {

    private val binding: ActivityPendingOrdersBinding by lazy {
        ActivityPendingOrdersBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var currUser: FirebaseUser

    private var arrlstPendingOrders = arrayListOf<OrderInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        currUser = auth.currentUser!!

        // Retrieve Pending Orders
        retrievePendingOrders()



        binding.backArrow.setOnClickListener {
            finish()
        }
    }

    private fun retrievePendingOrders() {
        db.reference.child("PendingOrders").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(orderSnap in snapshot.children){
                    val order = orderSnap.getValue(OrderInfo::class.java)
                    arrlstPendingOrders.add(order!!)
                }
                setAdapter(arrlstPendingOrders)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setAdapter(arrlstPendingOrders: ArrayList<OrderInfo>) {
        val adapter = PendingOrdersAdapter(this, arrlstPendingOrders)
        binding.rvPendingOrders.layoutManager = LinearLayoutManager(this)
        binding.rvPendingOrders.adapter = adapter
    }
}
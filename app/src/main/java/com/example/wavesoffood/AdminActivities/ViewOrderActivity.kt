package com.example.wavesoffood.AdminActivities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wavesoffood.AdminActivities.Model.AdminUser
import com.example.wavesoffood.Model.CustomerUser
import com.example.wavesoffood.Model.OrderInfo
import com.example.wavesoffood.databinding.ActivityViewOrderBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class ViewOrderActivity : AppCompatActivity() {

    private val binding: ActivityViewOrderBinding by lazy {
        ActivityViewOrderBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var currUser: FirebaseUser
    private lateinit var user: AdminUser
    var signInMethod = ""

    private lateinit var orderInfo: OrderInfo
    private lateinit var orderRef: DatabaseReference

    lateinit var builder : AlertDialog.Builder

    lateinit var userRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initializations
        orderInfo = intent.getSerializableExtra("order") as OrderInfo
        val orderKey = intent.getStringExtra("orderKey")

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        currUser = auth.currentUser!!

        orderRef = db.reference.child("PendingOrders").child(orderKey!!)


        builder = AlertDialog.Builder(this)
        builder.setTitle("Accept Alert")
        builder.setMessage("Are you sure you want to Accept this Order?")
        builder.setPositiveButton("Accept"){ dialog, which->
            val currTimeStamp = System.currentTimeMillis()
            val date = Date(currTimeStamp)
            val acceptedTime = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(date)
            orderInfo.acceptedOn = currTimeStamp
            orderInfo.isAccepted = true
            userRef.child("InProgressOrders").child(acceptedTime+"_"+orderInfo.customerEMail!!.split("@")[0]).setValue(orderInfo)
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        db.reference.child("PendingOrders").child(orderKey).removeValue()
                            .addOnCompleteListener {
                                if(it.isSuccessful){
                                    db.reference.child("Users").addListenerForSingleValueEvent(object: ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for(userSnap in snapshot.children){
                                                val userEmail = userSnap.getValue(CustomerUser::class.java)!!.eMail
                                                if(orderInfo.customerEMail.equals(userEmail)){
                                                    
                                                }
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })
                                }else{
                                    dialog.dismiss()
                                    Toast.makeText(this, "An Error Occured.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        dialog.dismiss()
                        Toast.makeText(this, "Order Accepted", Toast.LENGTH_SHORT).show()
                    }else{
                        dialog.dismiss()
                        Toast.makeText(this, "Could not Accepted.", Toast.LENGTH_SHORT).show()
                    }
                }

        }
        builder.setNegativeButton("Cancel"){ dialog, which ->
            dialog.dismiss()
        }

        fetchUserInfo(currUser)


        binding.etOrderedBy.setText(orderInfo.orderedBy.toString())
        binding.etDeliveryAddress.setText(orderInfo.deliveryAddress)

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

        val timestamp = orderInfo?.orderedOn!!.toLong()
        val sdf = SimpleDateFormat("'at' h:mm a 'on' EEEE, MMMM d, yyyy")
        val date = Date(timestamp)
        val orderTime = sdf.format(date)
        binding.etOrderedOn.setText(orderTime)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAccept.setOnClickListener {
            builder.show()
        }


    }

    private fun fetchUserInfo(currUser: FirebaseUser) {
        val providers = currUser.providerData
        for (profile in providers) {
            when (profile.providerId) {
                EmailAuthProvider.PROVIDER_ID -> {
                    // Email and password authentication
                    signInMethod = "EmailAndPassword"
                }

                GoogleAuthProvider.PROVIDER_ID -> {
                    // Google authentication
                    signInMethod = "Google"
                }
            }
        }
        userRef = db.reference.child("Users").child("Admin_"+signInMethod+"_"+currUser.email!!.split("@")[0]+"_"+currUser.uid)
        userRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(AdminUser::class.java)!!
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}
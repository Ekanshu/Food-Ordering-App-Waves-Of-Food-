package com.example.wavesoffood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.wavesoffood.BottomSheets.CongratsBottomSheetFragment
import com.example.wavesoffood.Model.CustomerUser
import com.example.wavesoffood.Model.Dish
import com.example.wavesoffood.Model.OrderInfo
import com.example.wavesoffood.databinding.ActivityPayOutBinding
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

class PayOutActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var currUser: FirebaseUser
    lateinit var userRef: DatabaseReference

    var arrlst = mutableListOf<Dish>()

    lateinit var userName: String
    lateinit var userEmail: String
    lateinit var billAmount: String
    var signInMethod = ""

    private lateinit var binding: ActivityPayOutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        currUser = auth.currentUser!!

        // Intent Things
        billAmount = intent.getStringExtra("billAmount")!!
        val recievedList = intent.getSerializableExtra("arrlstCartItems") as? ArrayList<Dish>
        arrlst = recievedList!!.toMutableList()

        // Get SignInMethod
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

        // Get User
        db.reference.child("Users").child("Customer_"+signInMethod+"_"+currUser.email!!.split("@")[0]+"_"+currUser.uid)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userRef = db.reference.child("Users").child("Customer_"+signInMethod+"_"+currUser.email!!.split("@")[0]+"_"+currUser.uid)
                    val user = snapshot.getValue(CustomerUser::class.java)

                    // Retrieve Cart Items
                    retrieveOrderInfo(user!!)

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        binding.btnPlaceMyOrder.setOnClickListener {
            if(binding.etAddress.text.isEmpty()){
                Toast.makeText(this, "Fill the Delivery Address first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(binding.etPhone.text.isEmpty()){
                Toast.makeText(this, "Fill the Phone Number first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val deliveryAddress = binding.etAddress.text.toString()
            val deliveryNumber = binding.etPhone.text.toString()
            val orderInfo = OrderInfo(userName, userEmail, deliveryAddress, deliveryNumber, null, billAmount, ArrayList(arrlst), false, null, false, false)

            val timestamp = System.currentTimeMillis()
            val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
            val date = Date(timestamp)
            val orderTime = sdf.format(date)

            orderInfo.orderedOn = timestamp

            userRef.child("OrderHistory").child(orderTime).setValue(orderInfo)
                .addOnCompleteListener { task->
                    if(task.isSuccessful){

                        userRef.child("cartItems").removeValue()
                            .addOnCompleteListener {
                                if(it.isSuccessful){
                                    db.reference.child("PendingOrders").child(orderTime+"_"+userEmail.split("@")[0]).setValue(orderInfo)
                                        .addOnCompleteListener {
                                            if(it.isSuccessful){
                                                Toast.makeText(this, "Order Successfully Placed.", Toast.LENGTH_SHORT).show()
                                            }else{
                                                Toast.makeText(this, "Some Error Occured.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }else{
                                    Toast.makeText(this, "Some Error Occured.", Toast.LENGTH_SHORT).show()
                                }
                            }

                        val congratsDialog = CongratsBottomSheetFragment()
                        congratsDialog.show(supportFragmentManager, "Congrats")
                    }else{
                        Toast.makeText(this, "Order could not be Placed.", Toast.LENGTH_SHORT).show()
                    }
                }

        }

    }

    private fun retrieveOrderInfo(user: CustomerUser) {
        userName = user.userName.toString()
        userEmail = user.eMail.toString()

        binding.etUserName.setText(userName)
        binding.etTotalAmounts.setText("$"+billAmount)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        currUser = auth.currentUser!!
    }
}
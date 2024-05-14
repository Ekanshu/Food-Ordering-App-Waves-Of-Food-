package com.example.wavesoffood.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Adapters.BuyAgainAdapter
import com.example.wavesoffood.Adapters.OrderHistoryAdapter
import com.example.wavesoffood.Model.CustomerUser
import com.example.wavesoffood.Model.Dish
import com.example.wavesoffood.Model.OrderInfo
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.FragmentHistoryBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var currUser: FirebaseUser
    lateinit var userRef: DatabaseReference

    lateinit var userName: String
    lateinit var userEmail: String
    var signInMethod = ""

    private var arrlstOrderHistory: ArrayList<OrderInfo> = arrayListOf()

val binding: FragmentHistoryBinding by lazy {
    FragmentHistoryBinding.inflate(layoutInflater)
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        currUser = auth.currentUser!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        // RecyclerView
//        val arrlstHistoryItems = ArrayList<Dish>()
//        arrlstHistoryItems.add(Dish(R.drawable.menu1, "Burger", "$5", 0))
//        arrlstHistoryItems.add(Dish(R.drawable.menu2, "Sandwich", "$7", 0))
//        arrlstHistoryItems.add(Dish(R.drawable.menu3, "Momo", "$8", 0))
//        arrlstHistoryItems.add(Dish(R.drawable.menu4, "Chowmine", "$10", 0))
//        val layoutManager = LinearLayoutManager(context)
//        val adapter = BuyAgainAdapter(context as Context, arrlstHistoryItems)
//        binding.rvPreviouslyBuy.layoutManager = layoutManager
//        binding.rvPreviouslyBuy.adapter = adapter




        return binding.root
    }

    private fun retrieveOrderInfo(user: CustomerUser) {
        userName = user.userName.toString()
        userEmail = user.eMail.toString()

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        currUser = auth.currentUser!!

        // Retrieve Order History
        userRef.child("OrderHistory").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(orderSnap in snapshot.children){
                    val thisOrder = orderSnap.getValue(OrderInfo::class.java)
                    arrlstOrderHistory.add(thisOrder!!)
                }
                setAdapter(arrlstOrderHistory)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun setAdapter(arrlstOrderHistory: ArrayList<OrderInfo>) {
        binding.rvOrderHistory.layoutManager = LinearLayoutManager(context)
        val adapter = OrderHistoryAdapter(context as Context, arrlstOrderHistory)
        binding.rvOrderHistory.adapter = adapter
    }

}
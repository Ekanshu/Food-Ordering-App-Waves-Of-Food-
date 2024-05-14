package com.example.wavesoffood.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Adapters.CartItemAdapter
import com.example.wavesoffood.Model.CustomerUser
import com.example.wavesoffood.Model.Dish
import com.example.wavesoffood.PayOutActivity
import com.example.wavesoffood.databinding.FragmentCartBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment() {
val binding: FragmentCartBinding by lazy {
    FragmentCartBinding.inflate(layoutInflater)
}

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var currUser: FirebaseUser
    var arrlstCartItems = mutableListOf<Dish>()

    var signInMethod: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        currUser = auth.currentUser!!

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
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(CustomerUser::class.java)

                    // Retrieve Cart Items
                    retrieveCartItems(user!!)


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        binding.btnProceed.setOnClickListener {
            val intent = Intent(context, PayOutActivity::class.java)
            val billAmount = (binding.tvBill.text).split("$")[1]
            intent.putExtra("billAmount", billAmount)
            intent.putExtra("arrlstCartItems", ArrayList(arrlstCartItems))
            startActivity(intent)
        }

        return binding.root
    }

    private fun retrieveCartItems(user: CustomerUser) {
        db.reference.child("Users").child("Customer_"+signInMethod+"_"+user.eMail!!.split("@")[0]+"_"+user.userId).child("cartItems").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                arrlstCartItems = mutableListOf()
                for(cartItemSnap in snapshot.children){
                    val cartItem = cartItemSnap.getValue(Dish::class.java)
                    arrlstCartItems.add(cartItem!!)
                }
                setAdapter(arrlstCartItems)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setAdapter(arrlstCartItems: MutableList<Dish>) {
        binding.rvAvailItems.layoutManager = LinearLayoutManager(context)
        val adapter = CartItemAdapter(context as Context, ArrayList(arrlstCartItems), binding.tvBill)
        binding.rvAvailItems.adapter = adapter
    }

}
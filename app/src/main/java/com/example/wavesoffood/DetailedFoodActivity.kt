package com.example.wavesoffood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.wavesoffood.AdminActivities.Model.FoodItem
import com.example.wavesoffood.Model.CustomerUser
import com.example.wavesoffood.Model.Dish
import com.example.wavesoffood.databinding.ActivityDetailedFoodBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailedFoodActivity : AppCompatActivity() {
val binding: ActivityDetailedFoodBinding by lazy {
    ActivityDetailedFoodBinding.inflate(layoutInflater)
}

    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser
    lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        db = FirebaseDatabase.getInstance()

        val foodName = intent.getStringExtra("FoodName")
        val foodImage = intent.getStringExtra("FoodImage")
        val foodDesc = intent.getStringExtra("FoodDesc")
        val foodIngredients = intent.getStringExtra("FoodIngredients")
        val foodPrice = intent.getStringExtra("FoodPrice")

        //
        val providers = user.providerData
        var signInMethod = ""
        for (profile in providers) {
            when (profile.providerId) {
                EmailAuthProvider.PROVIDER_ID -> {
                    // Email and password authentication
                    signInMethod = "EmailAndPassword"
//                    binding.imgSignInMethod.setImageResource(R.drawable.mail)
                }
                GoogleAuthProvider.PROVIDER_ID -> {
                    // Google authentication
                    signInMethod = "Google"
//                    binding.imgSignInMethod.setImageResource(R.drawable.google)
                }
            }
        }

        binding.detailedFoodName.text = foodName
        Glide.with(this).load(foodImage).placeholder(R.drawable.add_image).into(binding.detailedFoodImage)
        binding.tvShortDescription.text = foodDesc
        binding.tvFoodIngredients.text = foodIngredients

        binding.btnBackArrow.setOnClickListener {
            finish()
        }

        binding.btnAddToCart.setOnClickListener {
            var eMail : String?
            var userId :String?

            db.reference.child("Users").child("Customer_"+signInMethod+"_"+user.email!!.split("@")[0]+"_"+user.uid)
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val us = snapshot.getValue(CustomerUser::class.java)
                        eMail = us?.eMail
                        userId = us?.userId

                        db.reference.child("Users").child("Customer_"+signInMethod+"_"+user.email!!.split("@")[0]+"_"+user.uid).child("cartItems").addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var isAlreadyExist = false
                                for(cartItemSnap in snapshot.children){
                                    val cartItem = cartItemSnap.getValue(Dish::class.java)
                                    if(foodName.equals(cartItem!!.dishName)){
                                        Toast.makeText(applicationContext, "Already added to cart", Toast.LENGTH_SHORT).show()
                                        isAlreadyExist = true
                                    }
                                }
                                if(!isAlreadyExist){
                                    val dish = Dish(foodImage,foodName,foodPrice, 1)
//                                    val foodItemCartId = db.reference.child("Customer_"+signInMethod+"_"+eMail!!.split("@")[0]+"_"+userId).child("cartItems").push().key
                                    db.reference.child("Users").child("Customer_"+signInMethod+"_"+eMail!!.split("@")[0]+"_"+userId).child("cartItems").child(foodName+"").setValue(dish)
                                        .addOnSuccessListener {
                                            Toast.makeText(applicationContext, "Item added to cart successfully.", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnCanceledListener {
                                            Toast.makeText(applicationContext, "Item couldn't be added to cart.", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })


        }




    }

}
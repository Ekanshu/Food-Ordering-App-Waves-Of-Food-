package com.example.wavesoffood.Adapters

import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.Model.Dish
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.SampleCartItemBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import java.util.logging.Handler

class CartItemAdapter(
    val context: Context,
    val arrlstAvailItems: ArrayList<Dish>,
    val tvBill: TextView
): RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var db : FirebaseDatabase = FirebaseDatabase.getInstance()
    var user: FirebaseUser = auth.currentUser!!
    var signInMethod = ""
    companion object{
        private var itemQuantities: IntArray = intArrayOf()
        lateinit var cartItemRef: DatabaseReference
        var billAmount = 0
        lateinit var progressDialog: ProgressDialog
    }
    init {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Removing")
        progressDialog.setMessage("Please wait for some time...")
        progressDialog.setCancelable(false)

        billAmount = initialBillAmount()
        fetchUserInfo(user)
        itemQuantities = IntArray(arrlstAvailItems.size){1}
        cartItemRef = db.reference.child("Users").child("Customer_"+signInMethod+"_"+user.email!!.split("@")[0]+"_"+user.uid).child("cartItems")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SampleCartItemBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrlstAvailItems.get(position)
        Glide.with(context).load(item.dishImage).placeholder(R.drawable.add_image).into(holder.binding.imgDish)
        holder.binding.tvDishName.text = item.dishName
        holder.binding.tvItemQuantity.text = item.dishQuantity.toString()
        holder.binding.tvDishPrice.text = "$"+item.dishPrice+" x 1"+" = $"+item.dishPrice

        holder.binding.btnMinus.setOnClickListener {
            decreaseQuantity(holder, item, position)
        }
        holder.binding.btnPlus.setOnClickListener {
            increaseQuantity(holder, item, position)
        }
        holder.binding.btnRemove.setOnClickListener { 
            progressDialog.show()    
            deleteItem(item, position) 
        }

    }

    override fun getItemCount(): Int = arrlstAvailItems.size

    private fun decreaseQuantity(holder: ViewHolder, item: Dish, position: Int){
        if(itemQuantities[position]>1){
            itemQuantities[position]--

            arrlstAvailItems.get(position).dishQuantity = itemQuantities[position]

            holder.binding.tvItemQuantity.text = itemQuantities[position].toString()

            val itemTotalPrice = item.dishPrice?.toInt()?.times(itemQuantities[position])
            holder.binding.tvDishPrice.text = "$"+item.dishPrice+" x "+ itemQuantities[position]+" = $"+itemTotalPrice

            billAmount = billAmount?.minus(item.dishPrice!!.toInt())!!
            tvBill.setText("Bill: $"+ billAmount.toString())
        }
    }
    private fun increaseQuantity(holder: ViewHolder, item: Dish, position: Int){
        if(itemQuantities[position]<10){
            itemQuantities[position]++
            holder.binding.tvItemQuantity.text = itemQuantities[position].toString()

            arrlstAvailItems.get(position).dishQuantity = itemQuantities[position]

            val itemTotalPrice = item.dishPrice?.toInt()?.times(itemQuantities[position])
            holder.binding.tvDishPrice.text = "$"+item.dishPrice+" x "+ itemQuantities[position]+" = $"+itemTotalPrice

            billAmount = billAmount?.plus(item.dishPrice!!.toInt())!!
            tvBill.setText("Bill: $"+ billAmount.toString())
        }
    }

    private fun deleteItem(item: Dish, position: Int){

        val posRet = position
        getUniqueKeyAtPosition(item, posRet){ item, uniqueKey->
            if(uniqueKey!=null){
                removeItem(item, position, uniqueKey)
            }else{
                progressDialog.dismiss()
                Toast.makeText(context, "An error occurred.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeItem(item: Dish, position: Int, uniqueKey: String) {
        cartItemRef.child(uniqueKey).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                val itemTotalPrice = item.dishPrice?.toInt()?.times(itemQuantities[position])
                billAmount = billAmount!!.minus(itemTotalPrice!!)
                tvBill.setText("Bill: $"+ billAmount.toString())

                itemQuantities = itemQuantities.filterIndexed{ index, i -> index!=position }.toIntArray()
                arrlstAvailItems.removeAt(position)

                notifyItemRemoved(position)
                notifyItemRangeChanged(position, arrlstAvailItems.size)
                progressDialog.dismiss()
            }else{
                progressDialog.dismiss()
                Toast.makeText(context, "Item Removes Successfully.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUniqueKeyAtPosition(item: Dish, posRet: Int, onComplete:(Dish,String?) -> Unit) {
        cartItemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var uniqueKey: String? = null
                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if(index==posRet){
                        uniqueKey = dataSnapshot.key
                        return@forEachIndexed
                    }
                }
                onComplete(item, uniqueKey)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun fetchUserInfo(user: FirebaseUser) {
        val providers = user.providerData
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
    }

    private fun initialBillAmount(): Int{
        var billAmount = 0
        for(item in arrlstAvailItems){
            billAmount += item.dishPrice!!.toInt()
        }
        tvBill.text = "Bill: $"+billAmount.toString()
        return billAmount
    }

    class ViewHolder(val binding: SampleCartItemBinding): RecyclerView.ViewHolder(binding.root) {


    }
}
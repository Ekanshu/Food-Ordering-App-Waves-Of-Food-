package com.example.wavesoffood.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.AdminActivities.ViewOrderActivity
import com.example.wavesoffood.Model.OrderInfo
import com.example.wavesoffood.databinding.SamplePendingOrderBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class PendingOrdersAdapter(private val context: Context, private val arrlstPendingOrders: ArrayList<OrderInfo>): RecyclerView.Adapter<PendingOrdersAdapter.viewHolder>() {


    var db = FirebaseDatabase.getInstance()
    companion object{
        lateinit var pendingOrdersRef: DatabaseReference
    }
    init {
        pendingOrdersRef = db.reference.child("PendingOrders")
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(SamplePendingOrderBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val order = arrlstPendingOrders.get(position)
        holder.binding.tvCustomerName.text = order.orderedBy
        holder.binding.tvTotalAmount.text = "$"+order.billAmount
        holder.binding.btnViewOrder.setOnClickListener {
            getOrderKey(position){ orderKey->
                pendingOrdersRef.child(orderKey).addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val order = snapshot.getValue(OrderInfo::class.java)
                            val intent = Intent(context, ViewOrderActivity::class.java)
                            intent.putExtra("order", order)
                            intent.putExtra("orderKey", orderKey)
                            context.startActivity(intent)
                        }else{
                            Toast.makeText(context, "Order not exists in pending orders.", Toast.LENGTH_SHORT).show()
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }

    private fun getOrderKey(position: Int, onComplete: (String) -> Unit) {
        pendingOrdersRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var orderKey = ""
                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if(index==position){
                        orderKey = dataSnapshot.key!!
                        return@forEachIndexed
                    }
                }
                onComplete(orderKey)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun getItemCount() = arrlstPendingOrders.size

    inner class viewHolder(val binding: SamplePendingOrderBinding): RecyclerView.ViewHolder(binding.root)

}
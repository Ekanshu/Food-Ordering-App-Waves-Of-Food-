package com.example.wavesoffood.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.Model.OrderInfo
import com.example.wavesoffood.OrderDetailedActivity
import com.example.wavesoffood.databinding.SampleOrderHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date

class OrderHistoryAdapter(
    val context: Context,
    val arrlstOrderHistory: ArrayList<OrderInfo>
): RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var db : FirebaseDatabase = FirebaseDatabase.getInstance()
//    var user: FirebaseUser = auth.currentUser!!
//    var signInMethod = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SampleOrderHistoryBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = arrlstOrderHistory.get(position)
        holder.binding.tvOrderPrice.text = "$"+order.billAmount
        val timestamp = order.orderedOn!!.toLong()
        val sdf = SimpleDateFormat("'at' h:mm a 'on' EEEE, MMMM d, yyyy")
        val date = Date(timestamp)
        val orderTime = sdf.format(date)
        holder.binding.tvOrderTime.text = orderTime

        holder.binding.rowFood.setOnClickListener {
            val intent = Intent(context, OrderDetailedActivity::class.java)
            intent.putExtra("orderInfo", arrlstOrderHistory.get(position))
            intent.putExtra("orderTime", orderTime)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = arrlstOrderHistory.size

    class ViewHolder(val binding: SampleOrderHistoryBinding): RecyclerView.ViewHolder(binding.root) {}

}
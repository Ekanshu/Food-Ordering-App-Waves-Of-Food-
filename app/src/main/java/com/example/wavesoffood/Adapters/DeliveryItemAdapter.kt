package com.example.wavesoffood.Adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.databinding.SampleDeliveryItemBinding

class DeliveryItemAdapter(private val context: Context, private val arrlstCustomerNames: ArrayList<String>, private val arrlstPaymentStatus: ArrayList<String>): RecyclerView.Adapter<DeliveryItemAdapter.viewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryItemAdapter.viewholder {
        return viewholder(SampleDeliveryItemBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: DeliveryItemAdapter.viewholder, position: Int) {
        holder.binding.tvCustomerName.text = arrlstCustomerNames[position]
        holder.binding.tvPaymentStatus.text = arrlstPaymentStatus[position]
        val colorMap = mapOf(
            "received" to Color.GREEN,
            "notReceived" to Color.RED,
            "pending" to Color.YELLOW
        )
        holder.binding.tvPaymentStatus.setTextColor(colorMap[arrlstPaymentStatus[position]]?:Color.BLACK)
        holder.binding.cvDeliveryStatus.backgroundTintList = ColorStateList.valueOf(colorMap[arrlstPaymentStatus[position]]?:Color.RED)
    }

    override fun getItemCount() = arrlstCustomerNames.size

    inner class viewholder(val binding: SampleDeliveryItemBinding): RecyclerView.ViewHolder(binding.root) {

    }
}
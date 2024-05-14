package com.example.wavesoffood.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.Model.Dish
import com.example.wavesoffood.databinding.SampleBuyAgainBinding

class BuyAgainAdapter(val context: Context, val arrlstHistoryItems: ArrayList<Dish>): RecyclerView.Adapter<BuyAgainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SampleBuyAgainBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrlstHistoryItems.get(position)
//        holder.binding.imgDish.setImageResource(item.dishImage)
        holder.binding.tvDishName.text = item.dishName
        holder.binding.tvPrice.text = item.dishPrice
    }

    override fun getItemCount(): Int = arrlstHistoryItems.size

    class ViewHolder(val binding: SampleBuyAgainBinding): RecyclerView.ViewHolder(binding.root) {

    }
}
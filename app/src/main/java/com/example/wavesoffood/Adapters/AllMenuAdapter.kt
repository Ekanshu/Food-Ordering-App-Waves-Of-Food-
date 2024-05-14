package com.example.wavesoffood.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.AdminActivities.Model.FoodItem
import com.example.wavesoffood.databinding.SampleCartItemBinding

class AllMenuAdapter(private val context: Context, private val arrlstFoodItem: ArrayList<FoodItem>): RecyclerView.Adapter<AllMenuAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SampleCartItemBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvDishName.text = arrlstFoodItem[position].itemName
        holder.binding.tvDishPrice.text = arrlstFoodItem[position].itemPrice
        Glide.with(context).load(arrlstFoodItem[position].itemImage).into(holder.binding.imgDish)
    }

    override fun getItemCount(): Int = arrlstFoodItem.size

    inner class ViewHolder(val binding: SampleCartItemBinding): RecyclerView.ViewHolder(binding.root){
    }
}
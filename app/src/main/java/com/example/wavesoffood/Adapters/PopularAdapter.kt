package com.example.wavesoffood.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.DetailedFoodActivity
import com.example.wavesoffood.Model.Dish
import com.example.wavesoffood.databinding.SamplePopularItemBinding

class PopularAdapter(val context: Context, val arrlstDishes: ArrayList<Dish>):
    RecyclerView.Adapter<PopularAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SamplePopularItemBinding.inflate(LayoutInflater.from(context)))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrlstDishes.get(position)
//        holder.binding.imgDish.setImageResource(item.dishImage)
        holder.binding.tvDishName.text = item.dishName
        holder.binding.tvPrice.text = item.dishPrice

        holder.binding.rowFood.setOnClickListener {
            val intent = Intent(context, DetailedFoodActivity::class.java)
            intent.putExtra("FoodName", item.dishName)
            intent.putExtra("FoodImage", item.dishImage)
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return arrlstDishes.size
    }
    class ViewHolder(val binding: SamplePopularItemBinding): RecyclerView.ViewHolder(binding.root) {

    }
}
package com.example.wavesoffood.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.AdminActivities.Model.FoodItem
import com.example.wavesoffood.DetailedFoodActivity
import com.example.wavesoffood.Model.Dish
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.SampleMenuItemBinding

class MenuItemAdapter(val context: Context, val arrlstMenuItems: ArrayList<FoodItem>): RecyclerView.Adapter<MenuItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemAdapter.ViewHolder {
        return ViewHolder(SampleMenuItemBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: MenuItemAdapter.ViewHolder, position: Int) {
        val item = arrlstMenuItems.get(position)
        holder.binding.tvDishName.text = item.itemName
        holder.binding.tvPrice.text = item.itemPrice
        Glide.with(context).load(item.itemImage).placeholder(R.drawable.add_image).into(holder.binding.imgDish)

        holder.binding.rowFood.setOnClickListener {
            val intent = Intent(context, DetailedFoodActivity::class.java)
            intent.putExtra("FoodName", item.itemName)
            intent.putExtra("FoodImage", item.itemImage)
            intent.putExtra("FoodDesc", item.itemDesc)
            intent.putExtra("FoodIngredients", item.itemIngrediants)
            intent.putExtra("FoodPrice", item.itemPrice)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = arrlstMenuItems.size

    inner class ViewHolder(val binding: SampleMenuItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

}
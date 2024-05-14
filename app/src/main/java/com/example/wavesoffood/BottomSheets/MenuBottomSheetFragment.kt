package com.example.wavesoffood.BottomSheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Adapters.MenuItemAdapter
import com.example.wavesoffood.AdminActivities.Model.FoodItem
import com.example.wavesoffood.Model.Dish
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.FragmentMenuBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuBottomSheetFragment : BottomSheetDialogFragment() {

    val binding: FragmentMenuBottomSheetBinding by lazy {
        FragmentMenuBottomSheetBinding.inflate(layoutInflater)
    }
    lateinit var layoutManager: LinearLayoutManager
    var arrlstMenuItems: MutableList<FoodItem> = mutableListOf()

    lateinit var db : FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseDatabase.getInstance()

        retreiveFoodItems()



        binding.btnBackArrow.setOnClickListener {
            dismiss()
        }



        return binding.root
    }

    private fun retreiveFoodItems() {
        db.reference.child("Menu").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnap in snapshot.children){
                    val foodItem = foodSnap.getValue(FoodItem::class.java)
                    if (foodItem != null) {
                        arrlstMenuItems.add(foodItem)
                    }
                }
                setAdapter()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setAdapter() {
        layoutManager = LinearLayoutManager(context)
        binding.rvMenuItems.layoutManager = layoutManager
        val adapter = MenuItemAdapter(context as Context, ArrayList(arrlstMenuItems))
        binding.rvMenuItems.adapter = adapter
    }
}
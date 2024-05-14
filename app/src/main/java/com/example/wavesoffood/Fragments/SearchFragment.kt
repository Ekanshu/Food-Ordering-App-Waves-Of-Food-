package com.example.wavesoffood.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Adapters.MenuItemAdapter
import com.example.wavesoffood.AdminActivities.Model.FoodItem
import com.example.wavesoffood.Model.Dish
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.FragmentSearchBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {
val binding: FragmentSearchBinding by lazy {
    FragmentSearchBinding.inflate(layoutInflater)
}

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var currUser: FirebaseUser

//lateinit var layoutManager: LinearLayoutManager
var arrlstFilteredMenuItems: ArrayList<FoodItem> = arrayListOf()
var  arrlstMenuItems: ArrayList<FoodItem> = arrayListOf()
lateinit var adapter: MenuItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Initializations
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        currUser = auth.currentUser!!

        retrieveMenuItems(){ arrlstMenuItems->
            setUpSearchView()
        }

        // Setup SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
//                    filterMenuItems(arrlstMenuItems, query as String)
                arrlstFilteredMenuItems.clear()
                for (dish in arrlstMenuItems){
                    Toast.makeText(context, dish.itemName, Toast.LENGTH_SHORT).show()
                }
                arrlstMenuItems.forEachIndexed { index, dish ->
                    if (dish.itemName!!.contains(query as String,ignoreCase = true)) {
                        arrlstFilteredMenuItems.add(dish)
                    }
                    setAdapter(arrlstFilteredMenuItems)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMenuItems(arrlstMenuItems, newText as String)
                return true
            }

        })

        return binding.root
    }

    private fun setUpSearchView() {

    }

    private fun retrieveMenuItems(onComplete:(ArrayList<FoodItem>)->Unit) {
        db.reference.child("Menu").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnap in snapshot.children){
                    val foodItem = foodSnap.getValue(FoodItem::class.java)
                    if (foodItem != null) {
                        arrlstMenuItems.add(foodItem)
                    }
                }
                arrlstFilteredMenuItems = arrlstMenuItems
                setAdapter(arrlstFilteredMenuItems)

                onComplete(arrlstMenuItems)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setAdapter(arrlst: ArrayList<FoodItem>) {
        binding.rvMenuItems.layoutManager = LinearLayoutManager(context)
        binding.rvMenuItems.adapter = MenuItemAdapter(context as Context, arrlst)
    }

    private fun filterMenuItems(arrlstMenuItems: ArrayList<FoodItem>, query: String){
//        val arrlstFilteredMenuItems = arrlstMenuItems.filter {
//            it.itemImage?.contains(query, ignoreCase = true) == true
//        } as ArrayList<FoodItem>
//        setAdapter(arrlstFilteredMenuItems)

//        for(dish in arrlstMenuItems){
//            Toast.makeText(context, dish.itemName, Toast.LENGTH_SHORT).show()
//        }
        arrlstFilteredMenuItems.clear()
        for (dish in arrlstMenuItems){
            Toast.makeText(context, dish.itemName, Toast.LENGTH_SHORT).show()
        }
        arrlstMenuItems.forEachIndexed { index, dish ->
            if (dish.itemName!!.contains(query,ignoreCase = true)) {
                arrlstFilteredMenuItems.add(dish)
            }
        }
        setAdapter(arrlstFilteredMenuItems)

    }

//    private fun showAllMenuItems(){
//        arrlstFilteredItems.clear()
//        arrlstFilteredItems.addAll(arrlstMenuItems)
//        adapter.notifyDataSetChanged()
//    }

}

package com.example.wavesoffood.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.wavesoffood.Adapters.MenuItemAdapter
import com.example.wavesoffood.Adapters.PopularAdapter
import com.example.wavesoffood.AdminActivities.Model.FoodItem
import com.example.wavesoffood.BottomSheets.MenuBottomSheetFragment
import com.example.wavesoffood.Model.Dish
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
lateinit var binding: FragmentHomeBinding

lateinit var auth: FirebaseAuth
lateinit var db: FirebaseDatabase

var arrlstPoPularItems : MutableList<FoodItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        fetchPopularItems()

        binding.btnViewMenu.setOnClickListener {
            val menuBottomSheet = MenuBottomSheetFragment()
            menuBottomSheet.show(parentFragmentManager, "MenuBottomSheet")
        }

        return binding.root
    }

    private fun fetchPopularItems() {
        db.reference.child("Menu").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnap in snapshot.children){
                    val thisFood = foodSnap.getValue(FoodItem::class.java)
                    if (thisFood != null) {
                        arrlstPoPularItems.add(thisFood)
                    }
                }
                randomPopularItems()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun randomPopularItems() {
        val shuffledItems = arrlstPoPularItems.indices.toList().shuffled()
        val arrlstShortShuffledItems = shuffledItems.take(6).map { arrlstPoPularItems[it] }
        setAdapter(arrlstShortShuffledItems)
    }

    private fun setAdapter(arrlstShortShuffledItems: List<FoodItem>) {
        // RecyclerView SetUp
        val layoutManager = LinearLayoutManager(context)
        val adapter = MenuItemAdapter(context as Context, ArrayList(arrlstShortShuffledItems))
        binding.rvPopularItems.layoutManager = layoutManager
        binding.rvPopularItems.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imgList = ArrayList<SlideModel>()
        imgList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imgList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imgList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))
        binding.imageSlider.setImageList(imgList, ScaleTypes.FIT)
        binding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                Toast.makeText(requireContext(), "Selected Item $position", Toast.LENGTH_SHORT).show()
            }
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }
        })


    }

}
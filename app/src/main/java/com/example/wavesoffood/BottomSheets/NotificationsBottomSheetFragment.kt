package com.example.wavesoffood.BottomSheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Adapters.NotificationAdapter
import com.example.wavesoffood.Model.Notification
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.FragmentNotificationsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NotificationsBottomSheetFragment : BottomSheetDialogFragment() {
val binding: FragmentNotificationsBottomSheetBinding by lazy {
    FragmentNotificationsBottomSheetBinding.inflate(layoutInflater)
}
lateinit var layoutManager: LinearLayoutManager
lateinit var arrlstNotifications: ArrayList<Notification>
lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        // Setup RecyclerView
        arrlstNotifications = arrayListOf()
        arrlstNotifications.add(Notification(R.drawable.sademoji, "Your order has been cancelled successfully.Your order has been cancelled successfully"))
        arrlstNotifications.add(Notification(R.drawable.truck, "Order has been taken by the driver."))
        arrlstNotifications.add(Notification(R.drawable.congrats, "Congrats your order placed."))
        layoutManager = LinearLayoutManager(context)
        binding.rvNotifications.layoutManager = layoutManager
        adapter = NotificationAdapter(context as Context, arrlstNotifications)
        binding.rvNotifications.adapter = adapter

        binding.btnBackArrow.setOnClickListener {
            dismiss()
        }


        return binding.root
    }

}
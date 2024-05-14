package com.example.wavesoffood.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.Model.Notification
import com.example.wavesoffood.databinding.SampleNotificationBinding

class NotificationAdapter (val context: Context, val arrlstNotifications: ArrayList<Notification>): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): ViewHolder {
        return ViewHolder(SampleNotificationBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrlstNotifications.get(position)
        holder.binding.notificationImage.setImageResource(item.notificationImage)
        holder.binding.notificationMessage.setText(item.notificationMessage)
    }

    override fun getItemCount(): Int = arrlstNotifications.size

    inner class ViewHolder(val binding: SampleNotificationBinding): RecyclerView.ViewHolder(binding.root)

}
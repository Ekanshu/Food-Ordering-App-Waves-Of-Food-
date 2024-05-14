package com.example.wavesoffood.AdminActivities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wavesoffood.databinding.ActivityAdminMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AdminMainActivity : AppCompatActivity() {

    lateinit var binding: ActivityAdminMainBinding
    lateinit var auth: FirebaseAuth
//    lateinit var currUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
//        currUser = auth.currentUser!!

        binding.cardAddMenu.setOnClickListener {
            startActivity(Intent(this@AdminMainActivity, AddItemActivity::class.java))
        }

        binding.cardAllItemsMenu.setOnClickListener {
            startActivity(Intent(this@AdminMainActivity, AllItemActivity::class.java))
        }

        binding.cardOrderDispatch.setOnClickListener {
            startActivity(Intent(this@AdminMainActivity, OutForDeliveryActivity::class.java))
        }

        binding.cardProfile.setOnClickListener {
            startActivity(Intent(this@AdminMainActivity, AdminProfileActivity::class.java))
        }

        binding.cardAddUser.setOnClickListener {
            startActivity(Intent(this@AdminMainActivity, AdminNewUserActivity::class.java))
        }

        binding.cardLogOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this@AdminMainActivity, AdminLoginActivity::class.java))
        }

        binding.rlPendingOrders.setOnClickListener {
            startActivity(Intent(this@AdminMainActivity, PendingOrdersActivity::class.java))
        }

    }
}
package com.example.wavesoffood.AdminActivities

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.wavesoffood.AdminActivities.Model.AdminUser
import com.example.wavesoffood.Model.CustomerUser
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.ActivityAdminProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityAdminProfileBinding

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var currUser: FirebaseUser

    lateinit var progressDialog: ProgressDialog

    lateinit var adminRef: DatabaseReference

    var signInMethod = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        currUser = auth.currentUser!!

        // Progress Dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Wait")
        progressDialog.setMessage("Please wait for some time...")
        progressDialog.setCancelable(false)

        fetchUserInfo(currUser)

        binding.etUserName.isEnabled = false
        binding.etAddress.isEnabled = false
        binding.etEmail.isEnabled = false
        binding.etPhone.isEnabled = false

        binding.backArrow.setOnClickListener {
            finish()
        }

        var isEditable = false
        binding.tvToggleEdit.setOnClickListener {
            isEditable = !isEditable
            binding.etAddress.isEnabled = isEditable
            binding.etPhone.isEnabled = isEditable
            if(isEditable){
                binding.etAddress.requestFocus()
            }
        }

        binding.btnSave.setOnClickListener {
            progressDialog.show()
            val resNumber = binding.etPhone.text.toString()

            adminRef.child("resNumber").setValue(resNumber)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this, "User Info Saved.", Toast.LENGTH_SHORT).show()
                    }else{
                        progressDialog.dismiss()
                        Toast.makeText(this, "Info could not be saved.", Toast.LENGTH_SHORT).show()
                    }

                }

            finish()
        }
    }

    private fun fetchUserInfo(user: FirebaseUser) {

        val eMailPart = user.email!!.split("@")
        val providers = user.providerData
        for (profile in providers) {
            when (profile.providerId) {
                EmailAuthProvider.PROVIDER_ID -> {
                    // Email and password authentication
                    signInMethod = "EmailAndPassword"
                    binding.imgSignInMethod.setImageResource(R.drawable.mail)
                }
                GoogleAuthProvider.PROVIDER_ID -> {
                    // Google authentication
                    signInMethod = "Google"
                    binding.imgSignInMethod.setImageResource(R.drawable.google)
                }
            }
        }

//        Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
        db.reference.child("Users").child("Admin_"+signInMethod+"_"+eMailPart[0]+"_"+user.uid)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    adminRef = db.reference.child("Users").child("Admin_"+signInMethod+"_"+eMailPart[0]+"_"+user.uid)
                    val thisUser = snapshot.getValue(AdminUser::class.java)
                    binding.etUserName.setText(thisUser!!.ownersName)
                    binding.etEmail.setText(user.email)
                    binding.etAddress.setText(thisUser.resAddress)
                    binding.etPhone.setText(thisUser.resNumber)
                    Glide.with(this@AdminProfileActivity).load(thisUser.adminImage).placeholder(R.drawable.user).into(binding.imgDisplayImage)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

}
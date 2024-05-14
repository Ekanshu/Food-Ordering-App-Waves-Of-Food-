package com.example.wavesoffood.Fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.wavesoffood.LoginActivity
import com.example.wavesoffood.Model.CustomerUser
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.FragmentProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var user: FirebaseUser
    lateinit var userRef: DatabaseReference

    lateinit var progressDialog: ProgressDialog

    var signInMethod = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        user = auth.currentUser!!

        // Progress Dialog
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Wait")
        progressDialog.setMessage("Please wait for some time...")
        progressDialog.setCancelable(false)

        fetchUserInfo(user)

        binding.rlLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

        binding.etUserName.isEnabled = false
        binding.etAddress.isEnabled = false
        binding.etEmail.isEnabled = false
        binding.etPhone.isEnabled = false
        var isEditable = false
        binding.tvToggleEdit.setOnClickListener {
            isEditable = !isEditable
            binding.etUserName.isEnabled = isEditable
            binding.etAddress.isEnabled = isEditable
            binding.etPhone.isEnabled = isEditable
            if(isEditable){
                binding.etUserName.requestFocus()
            }
        }

        // Button Saved
        binding.btnSave.setOnClickListener {
            progressDialog.show()
            val userName = binding.etUserName.text.toString()
            val address = binding.etAddress.text.toString()
            val phone = binding.etPhone.text.toString()

            userRef.child("userName").setValue(userName)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        userRef.child("address").setValue(address).addOnCompleteListener {
                            if(it.isSuccessful){
                                userRef.child("phone").setValue(phone).addOnCompleteListener {
                                    if(it.isSuccessful){
                                        progressDialog.dismiss()
                                        Toast.makeText(context, "User Info Saved.", Toast.LENGTH_SHORT).show()
                                    }else{
                                        progressDialog.dismiss()
                                        Toast.makeText(context, "Info could not be saved.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }else{
                                progressDialog.dismiss()
                                Toast.makeText(context, "Info could not be saved.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        progressDialog.dismiss()
                        Toast.makeText(context, "Info could not be saved.", Toast.LENGTH_SHORT).show()
                    }

                }

        }


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun fetchUserInfo(user: FirebaseUser) {
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

        db.reference.child("Users").child("Customer_"+signInMethod+"_"+user.email!!.split("@")[0]+"_"+user.uid)
            .addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userRef = db.reference.child("Users").child("Customer_"+signInMethod+"_"+user.email!!.split("@")[0]+"_"+user.uid)
                val us = snapshot.getValue(CustomerUser::class.java)
                binding.etUserName.setText(us!!.userName)
                binding.etEmail.setText(user.email)
                binding.etAddress.setText(us!!.address)
                binding.etPhone.setText(us!!.phone)
                Glide.with(activity!!).load(us.userImage).placeholder(R.drawable.user).into(binding.imgDisplayImage)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

}
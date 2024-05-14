package com.example.wavesoffood.AdminActivities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.wavesoffood.AdminActivities.Model.AdminUser
import com.example.wavesoffood.MainActivity
import com.example.wavesoffood.Model.CustomerUser
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.ActivityAdminSignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class AdminSignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminSignUpBinding

    var ownerName: String = ""
    var resName: String = ""
    var resLocation = ""
    var  email: String = ""
    var password: String = ""

    lateinit var auth: FirebaseAuth
    var db = Firebase.database.reference

    lateinit var gsc: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions

    lateinit var progressDialog: ProgressDialog

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if(result.resultCode== Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful){
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                progressDialog.show()
                checkUserInCustomers(account.email!!, account, credential)


            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialization
        auth = FirebaseAuth.getInstance()

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Sign-Up")
        progressDialog.setMessage("Please wait for some time...")
        progressDialog.setCancelable(false)

        val listLocation = arrayOf("Karnal", "Panipat", "Kurukshetra", "Jind", "Ambala", "Sonipat", "Kaithal")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listLocation)
        binding.listOfLocation.setAdapter(adapter)

        binding.listOfLocation.setOnItemClickListener { parent, view, position, id ->
            resLocation = parent.getItemAtPosition(position) as String
        }

        binding.btnRegister.setOnClickListener {
            ownerName = binding.etOwnerName.text.toString()
            resName = binding.etResName.text.toString()
            email = binding.etEmail.text.toString().trim()
            password = binding.etPassword.text.toString()
            if(ownerName.isEmpty() || resName.isEmpty() || resLocation.isEmpty() || email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Fill all the fields First", Toast.LENGTH_SHORT).show()
            }else{
                progressDialog.show()
                checkUserInCustomers(email, null, null)
            }
        }

        binding.btnGoogle.setOnClickListener {
            val gsi = gsc.signInIntent
            launcher.launch(gsi)
        }

        binding.txtAHA.setOnClickListener {
            startActivity(Intent(this@AdminSignUpActivity, AdminLoginActivity::class.java))
        }

    }

    private fun checkUserInCustomers(eMail: String, account: GoogleSignInAccount?, credential: AuthCredential?){
        var isUserCustomer = false
        db.child("Users").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(snapChild in snapshot.children){
                    val usertype = snapChild.child("userType").getValue(String::class.java)
                    val email = snapChild.child("email").getValue(String::class.java)
                    if(email.equals(eMail) && usertype.equals("Customer")){
                        isUserCustomer = true
                    }
                }
                if(isUserCustomer){
                    progressDialog.dismiss()
                    Toast.makeText(this@AdminSignUpActivity, "Customer found registered with same email.", Toast.LENGTH_SHORT).show()
                }else{
                    if(!password.equals("")){
                        createEmailAdminUser(eMail, password)
                    }else{
                        createGoogleAdminUser(account, credential)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun createGoogleAdminUser(account: GoogleSignInAccount?, credential: AuthCredential?) {

//        db.child("Users").addListenerForSingleValueEvent(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                var userIsAdmin = false
//                for(childSnap in snapshot.children){
//                    val userId = childSnap.child("userId").getValue(String::class.java)
//                    val userType = childSnap.child("userType").getValue(String::class.java)
//                    if(userId==auth.uid && userType=="Admin"){
//                        userIsAdmin = true
//                        auth.signOut()
//                    }
//                }
//                if(userIsAdmin){
//                    progressDialog.dismiss()
//                    Toast.makeText(this@AdminSignUpActivity, "User is an Admin.", Toast.LENGTH_SHORT).show()
//                }else{
            auth.signInWithCredential(credential!!)
                    .addOnCompleteListener { task->
                        progressDialog.show()
                        if (task.isSuccessful){
                            val userId = task?.result?.user!!.uid
                            val ownersName = account?.displayName
                            val eMail = account?.email
                            val adminImage = account?.photoUrl.toString()
                            Toast.makeText(this , ownersName+" Admin created Successfully.", Toast.LENGTH_SHORT).show()

                            val newAdminUser = AdminUser(userId, ownersName, "", "",eMail, password, adminImage, "Admin", "Google")

                            saveGoogleAdminUserInfo(newAdminUser)

                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(this, "Could not Signed-In: "+task.exception, Toast.LENGTH_SHORT).show()
                        }
                    }

                            }

    private fun saveGoogleAdminUserInfo(newGoogleUser: AdminUser) {

        db.child("Users").child("Admin_Google_"+newGoogleUser.eMail!!.split("@")[0]+"_"+newGoogleUser.userId).setValue(newGoogleUser)
            .addOnCompleteListener {  task->
                if(task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this, "Admin User Info saved successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminMainActivity::class.java))
                    finish()
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(this, "Admin User Info saved successfully", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createEmailAdminUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task->
                if(task.isSuccessful){
                    saveEmailAdminInfo(task)
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(this, "Could not Registered "+task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveEmailAdminInfo(task: Task<AuthResult>){
        val userId = task.result.user!!.uid
        val newAdmin = AdminUser(userId, ownerName, resName, resLocation,"", email, password, "","Admin", "EmailAndPassword")
        db.child("Users").child("Admin_EmailAndPassword_"+email.split("@")[0]+"_"+userId).setValue(newAdmin)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this, "User Created Successfully.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@AdminSignUpActivity, AdminLoginActivity::class.java))
                    finish()
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(this@AdminSignUpActivity, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }



}
package com.example.wavesoffood.AdminActivities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.wavesoffood.AdminActivities.Model.AdminUser
import com.example.wavesoffood.MainActivity
import com.example.wavesoffood.R
import com.example.wavesoffood.SplashScreenActivity
import com.example.wavesoffood.databinding.ActivityAdminLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class AdminLoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminLoginBinding

    lateinit var auth: FirebaseAuth
    lateinit var db: DatabaseReference
    lateinit var currUser: FirebaseUser

    lateinit var gso: GoogleSignInOptions
    lateinit var gsc: GoogleSignInClient

    var  email: String = ""
    var password: String = ""

    lateinit var progressDialog: ProgressDialog

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if(result.resultCode== Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                progressDialog.show()
                checkUserInCustomers(account.email!!, account, credential)
            }else{
                Toast.makeText(this, "Could not Signed-In", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializations
        auth = FirebaseAuth.getInstance()
        db = Firebase.database.reference
        currUser = auth.currentUser!!

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Log-In")
        progressDialog.setMessage("Please wait for some time...")
        progressDialog.setCancelable(false)



        binding.btnLogin.setOnClickListener {
            email = binding.etEmail.text.toString().trim()
            password = binding.etPassword.text.toString()
            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Fill all the fields First", Toast.LENGTH_SHORT).show()
            }else{
                progressDialog.show()
                checkUserInCustomers(email, null, null)
//                loginAdmin(email, password)
            }
        }

        binding.tvCustomerPanel.setOnClickListener {
            startActivity(Intent(this@AdminLoginActivity, SplashScreenActivity::class.java))
            finish()
        }

        binding.btnGoogle.setOnClickListener {
            val googleSignInIntent = gsc.signInIntent
            launcher.launch(googleSignInIntent)
        }

        binding.txtDHA.setOnClickListener {
            startActivity(Intent(this@AdminLoginActivity, AdminSignUpActivity::class.java))
        }

    }

    override fun onStart() {
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser!=null){
            updateUi(currUser)
            finish()
        }
        super.onStart()
    }

    private fun updateUi(currUser: FirebaseUser) {
        startActivity(Intent(this@AdminLoginActivity, AdminMainActivity::class.java))
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
                    Toast.makeText(this@AdminLoginActivity, "Customer found registered with same email.", Toast.LENGTH_SHORT).show()
                }else{
                    if(!password.equals("")){
                        logInEmailAdminUser(eMail, password)
                    }else{
                        logInGoogleAdminUser(account, credential)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun logInEmailAdminUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task->
                if(task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this@AdminLoginActivity, "Signed-In", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@AdminLoginActivity, AdminMainActivity::class.java))
                    finish()
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(this, "Could not Log-in"+task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun logInGoogleAdminUser(account: GoogleSignInAccount?, credential: AuthCredential?) {
        auth.signInWithCredential(credential!!)
            .addOnCompleteListener { task->
                progressDialog.show()
                if (task.isSuccessful){
                    val userId = task?.result?.user!!.uid
                    val ownersName = account?.displayName
                    val eMail = account?.email
                    val adminImage = account?.photoUrl.toString()
                    Toast.makeText(this , ownersName+" Admin signed in Successfully.", Toast.LENGTH_SHORT).show()

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



}
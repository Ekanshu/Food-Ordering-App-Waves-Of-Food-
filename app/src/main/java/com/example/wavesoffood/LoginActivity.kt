package com.example.wavesoffood

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.wavesoffood.AdminActivities.AdminMainActivity
import com.example.wavesoffood.Model.CustomerUser
import com.example.wavesoffood.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding

    lateinit var auth: FirebaseAuth
    lateinit var currUser: FirebaseUser
    lateinit var db: FirebaseDatabase

    var eMail: String = ""
    var password: String = ""

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
                checkUserInAdmin(account.email!!, account, credential)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializations
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Log-In")
        progressDialog.setMessage("Please wait for some time...")
        progressDialog.setCancelable(false)


        binding.btnLogin.setOnClickListener {
            eMail = binding.etEmail.text.toString().trim()
            password = binding.etPassword.text.toString().trim()
            if(!(eMail.isEmpty() || password.isEmpty())){
                progressDialog.show()
                checkUserInAdmin(eMail, null, null)

            }else{
                Toast.makeText(this, "Please fill all the Fields.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGoogle.setOnClickListener {
            val gsi = gsc.signInIntent
            launcher.launch(gsi)
        }

        binding.txtDHA.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        if(auth.currentUser!=null){
            progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Fetching user Data")
            progressDialog.setMessage("Please wait for some time...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            currUser = auth.currentUser!!
            val userUid = auth.currentUser!!.uid
            db.reference.child("Users").addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(snapChild in snapshot.children){
                        val userId = snapChild.child("userId").getValue(String::class.java)
                        if(userId.equals(userUid)){
                            val userType = snapChild.child("userType").getValue(String::class.java)

                            if(userType=="Admin"){
                                updateUi(0)
                            }else if(userType=="Customer"){
                                updateUi( 1)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        super.onStart()
    }

    private fun updateUi(i: Int) {
        if(i==0){
            startActivity(Intent(this@LoginActivity, AdminMainActivity::class.java))
        }else if(i==1){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
        progressDialog.dismiss()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun checkUserInAdmin(eMail: String, account: GoogleSignInAccount?, credential: AuthCredential?){
        var isUserAdmin = false
        db.reference.child("Users").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for(snapChild in snapshot.children){
                    val usertype = snapChild.child("userType").getValue(String::class.java)
                    val email = snapChild.child("email").getValue(String::class.java)
                    if(email.equals(eMail) && usertype.equals("Admin")){
                        isUserAdmin = true
                    }
                }
                if(isUserAdmin){
                    progressDialog.dismiss()
                    Toast.makeText(this@LoginActivity, "Admin registered found with same email.", Toast.LENGTH_SHORT).show()
                }else{
                    if(!password.equals("")){
                        logInEmailCustomerUser()
                    }else{
                        logInGoogleCustomerUser(account, credential)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun logInGoogleCustomerUser(account: GoogleSignInAccount?, credential: AuthCredential?) {
        db.reference.child("Users").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var isAlreadyRegistered = false
                for(userSnap in snapshot.children){
                    val user = userSnap.getValue(CustomerUser::class.java)
                    if(account!!.email!!.split("@")[0]== user!!.eMail!!.split("@")[0]){
                        isAlreadyRegistered = true
                        break
                    }
                }
                auth.signInWithCredential(credential!!)
                    .addOnCompleteListener { task->
                        if (task.isSuccessful){
                            if(isAlreadyRegistered){
                                progressDialog.dismiss()
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                                finish()
                            }else{
                                val uid = task?.result?.user!!.uid
                                val userName = account?.displayName
                                val email = account?.email
                                val image = account?.photoUrl.toString()
                                val newGoogleUser = CustomerUser(uid, userName,"", email, "","", image, "Customer", "Google")
                                saveGoogleUserInfo(newGoogleUser)
                            }
                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(applicationContext, "Could not Signed-In", Toast.LENGTH_SHORT).show()
                        }
                    }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun logInEmailCustomerUser() {
        auth.signInWithEmailAndPassword(eMail, password)
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this@LoginActivity, "Signed-In", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveGoogleUserInfo(newGoogleUser: CustomerUser) {
        db.reference.child("Users").child("Customer_Google_"+newGoogleUser.eMail!!.split("@")[0]+"_"+newGoogleUser.userId).setValue(newGoogleUser)
            .addOnCompleteListener {  task->
                if(task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this, "User Info saved", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(this, "User Info could not be saved", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
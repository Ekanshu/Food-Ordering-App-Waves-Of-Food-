package com.example.wavesoffood

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.wavesoffood.AdminActivities.AdminSplashActivity
import com.example.wavesoffood.Model.CustomerUser
import com.example.wavesoffood.databinding.ActivitySignUpBinding
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

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding

    lateinit var gsc: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions

    lateinit var auth: FirebaseAuth
    var db = Firebase.database.reference

    var userName: String = ""
    var eMail: String = ""
    var password: String = ""

    lateinit var progressDialog: ProgressDialog

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if(result.resultCode== Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful){
                progressDialog.show()
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                progressDialog.show()
                checkUserInAdmin(account.email!!, account, credential)
            }else{
                Toast.makeText(this, "Could not retrieved Account", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        gsc = GoogleSignIn.getClient(this, gso)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Register")
        progressDialog.setMessage("Please wait for some time...")
        progressDialog.setCancelable(false)

        binding.btnRegister.setOnClickListener {
            userName = binding.etUserName.text.toString()
            eMail = binding.etEmail.text.toString()
            password = binding.etPassword.text.toString()
            if( !(userName.isEmpty() || eMail.isEmpty() || password.isEmpty()) ){
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

        binding.txtAHA.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.tvAdminPanel.setOnClickListener {
            startActivity(Intent(this, AdminSplashActivity::class.java))
            finish()
        }

    }

    override fun onStart() {
        auth = FirebaseAuth.getInstance()
        super.onStart()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


    private fun checkUserInAdmin(eMail: String, account: GoogleSignInAccount?, credential: AuthCredential?){
        var isUserAdmin = false
        db.child("Users").addListenerForSingleValueEvent(object: ValueEventListener{
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
                    Toast.makeText(this@SignUpActivity, "Admin registered found with same email.", Toast.LENGTH_SHORT).show()
                }else{
                    if(!password.equals("")){
                        createEmailCustomerUser()
                    }else{
                        createGoogleCustomerUser(account, credential)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun createEmailCustomerUser() {
        auth.createUserWithEmailAndPassword(eMail, password)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    Toast.makeText(this@SignUpActivity, "User Created Successfully", Toast.LENGTH_SHORT).show()
                    val userUid = task.result.user!!.uid
                    val newUser = CustomerUser(userUid, userName,"", eMail, password, "",  "", "Customer", "Email")
                    db.child("Users").child("Customer_EmailAndPassword_"+eMail.split("@")[0]+"_"+userUid).setValue(newUser)
                        .addOnCompleteListener { task->
                            progressDialog.dismiss()
                            if(task.isSuccessful){
                                progressDialog.dismiss()
                                Toast.makeText(this@SignUpActivity, "User info saved successfully.", Toast.LENGTH_SHORT).show()
                                auth.signOut()
                                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                                finish()
                            }else{
                                progressDialog.dismiss()
                                Toast.makeText(this@SignUpActivity, "User info couldn't be saved.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(this@SignUpActivity, "Could not create User: "+task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createGoogleCustomerUser(account: GoogleSignInAccount?, credential: AuthCredential?) {
        db.child("Users").addListenerForSingleValueEvent(object: ValueEventListener{
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

//        auth.signInWithCredential(credential!!)
//            .addOnCompleteListener { task->
//                if (task.isSuccessful){
//                    val uid = task?.result?.user!!.uid
//                    val userName = account?.displayName
//                    val email = account?.email
//                    val image = account?.photoUrl.toString()
//                    val newGoogleUser = CustomerUser(uid, userName,"", email, "","", image, "Customer", "Google")
//                    saveGoogleUserInfo(newGoogleUser)
//                }else{
//                    progressDialog.dismiss()
//                    Toast.makeText(this, "Could not Signed-In", Toast.LENGTH_SHORT).show()
//                }
//            }
    }

    private fun saveGoogleUserInfo(newGoogleUser: CustomerUser) {
        db.child("Users").child("Customer_Google_"+newGoogleUser.eMail!!.split("@")[0]+"_"+newGoogleUser.userId).setValue(newGoogleUser)
            .addOnCompleteListener {  task->
                if(task.isSuccessful){
                    progressDialog.dismiss()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(this, "User Info saved", Toast.LENGTH_SHORT).show()
                }
            }

    }



}
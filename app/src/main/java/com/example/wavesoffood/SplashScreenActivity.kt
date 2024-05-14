package com.example.wavesoffood

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.wavesoffood.AdminActivities.AdminMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashScreenActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    lateinit var db: FirebaseDatabase

//    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

//        progressDialog = ProgressDialog(this)
//        progressDialog.setTitle("Register")
//        progressDialog.setMessage("Please wait for some time...")
//        progressDialog.setCancelable(false)

        if(auth.currentUser!=null){
            Handler().postDelayed({
                if(auth.currentUser!=null){

                    val userUid = auth.currentUser!!.uid
                    db.reference.child("Users").addListenerForSingleValueEvent(object: ValueEventListener {
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
            }, 3000)
        }else{
            Handler().postDelayed({
                val intent = Intent(this, StartActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000)
        }




    }

    private fun updateUi(i: Int) {
        if(i==0){
            startActivity(Intent(this@SplashScreenActivity, AdminMainActivity::class.java))
        }else if(i==1){
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        }
//        progressDialog.dismiss()
        finish()
    }
}
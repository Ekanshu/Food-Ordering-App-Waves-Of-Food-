package com.example.wavesoffood.AdminActivities

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wavesoffood.AdminActivities.Model.FoodItem
import com.example.wavesoffood.databinding.ActivityAddItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class AddItemActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddItemBinding

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase
    lateinit var storage: FirebaseStorage

    lateinit var progressBar: ProgressDialog

    lateinit var imageUri: Uri

    lateinit var itemName: String
    lateinit var itemPrice: String
    lateinit var itemDesc: String
    lateinit var itemIngrediants: String
    lateinit var itemImage: Uri

    val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if(uri != null){
            binding.selectedImage.setImageURI(uri)
            imageUri = uri
        }else{
            Toast.makeText(this, "Image not Selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadItem(newItem: FoodItem) {
            newItem.itemUid = db.reference.child("Menu").push().key
            storage.reference.child("Menu").child("foodImages/${newItem.itemUid}.jpg").putFile(imageUri)
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        storage.reference.child("Menu").child("foodImages/${newItem.itemUid}.jpg").downloadUrl
                            .addOnSuccessListener { uri->
                                    newItem.itemImage = uri.toString()
                                    db.reference.child("Menu").child(newItem.itemName+"_"+newItem.itemUid.toString()).setValue(newItem)
                                        .addOnCompleteListener {  task->
                                            if(task.isSuccessful){
                                                progressBar.dismiss()
                                                Toast.makeText(this, "Item Uploded Successfully", Toast.LENGTH_SHORT).show()
                                                binding.edtItemName.setText("")
                                                binding.edtItemPrice.setText("")
                                                binding.edtShortDesc.setText("")
                                                binding.edtIngredients.setText("")
                                                binding.selectedImage.setImageURI(null)
                                            }else{
                                                progressBar.dismiss()
                                                Toast.makeText(this, "Item could not be Uploded Successfully", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                            }
                    }else{
                        progressBar.dismiss()
                        Toast.makeText(this, "Item Could not Uploded Successfully", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        progressBar = ProgressDialog(this)
        progressBar.setTitle("Upload")
        progressBar.setMessage("Adding your Food Item to Server")
        progressBar.setCancelable(false)



        binding.txtSelectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnAddItem.setOnClickListener {
            progressBar.show()
            itemName = binding.edtItemName.text.toString()
            itemPrice = binding.edtItemPrice.text.toString()
            itemDesc = binding.edtShortDesc.text.toString()
            itemIngrediants = binding.edtIngredients.text.toString()
            itemImage = imageUri
            if(!(itemName.isEmpty() || itemPrice.isEmpty() || itemDesc.isEmpty() || itemIngrediants.isEmpty())){
                val newItem = FoodItem("", itemName, itemPrice, itemDesc, itemIngrediants, itemImage.toString())
                uploadItem(newItem)
            }else{
                progressBar.dismiss()
                Toast.makeText(this, "Please fill all the fields first", Toast.LENGTH_SHORT).show()
            }

        }

    }

}
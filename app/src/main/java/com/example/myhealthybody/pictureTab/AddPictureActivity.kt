package com.example.myhealthybody.pictureTab

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myhealthybody.R
import com.example.myhealthybody.databinding.ActivityAddPictureBinding
import com.example.myhealthybody.mainView.MyApplication
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddPictureActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddPictureBinding
    private lateinit var filePath: String
    private lateinit var imageUri: Uri
    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.all { permission -> permission.value }) {
            saveStore()
        } else {
            Toast.makeText(this, "permission denied...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.addToolbar
        setSupportActionBar(toolbar)

    }

    val requestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    )
    {
        if (it.resultCode === android.app.Activity.RESULT_OK) {

            imageUri = it.data?.data as Uri
            Glide
                .with(applicationContext)
                .load(it.data?.data)
                .apply(RequestOptions().override(250, 200))
                .centerCrop()
                .into(binding.addImageView)


            val cursor = contentResolver.query(
                it.data?.data as Uri,
                arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null
            )
            cursor?.moveToFirst().let {
                filePath = cursor?.getString(0) as String
            }
            Log.d("kim", "filePath : $filePath")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === R.id.menu_add_gallery) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLauncher.launch(intent)
        } else if (item.itemId === R.id.menu_add_save) {
            if (binding.addImageView.drawable !== null && binding.addEditView.text.isNotEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            "android.permission.READ_MEDIA_IMAGES"
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        saveStore()
                    } else {
                        permissionLauncher.launch(
                            arrayOf<String>(
                                "android.permission.READ_MEDIA_IMAGES"
                            )
                        )
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            "android.permission.READ_EXTERNAL_STORAGE"
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        saveStore()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                "android.permission.READ_EXTERNAL_STORAGE"
                            )
                        )
                    }
                }

            } else {
                Toast.makeText(this, "데이터가 모두 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //....................
    private fun saveStore() {
        //add............................
        val data = mapOf(
            "email" to MyApplication.email,
            "content" to binding.addEditView.text.toString(),
            "date" to dateToString(Date())
        )

        MyApplication.db.collection("news")
            .add(data)
            .addOnSuccessListener {
                uploadImage(it.id)
            }
            .addOnFailureListener {
                Log.d("kim", "data save error", it)
            }
    }

    private fun uploadImage(docId: String) {
        //add............................
        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("images/${docId}.jpg")

        imgRef.putFile(imageUri)
            .addOnSuccessListener {
                imgRef.downloadUrl.addOnSuccessListener { uri ->
                    // Firebase에 저장할 메타데이터 준비
                    val imageUrl = uri.toString()
                    val data = mapOf(
                        "email" to MyApplication.email,
                        "content" to binding.addEditView.text.toString(),
                        "date" to dateToString(Date()),
                        "imageUrl" to imageUrl

                    )
                    // FireStore에 이미지 메타데이터 저장
                    MyApplication.db.collection("news").document(docId).set(data)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Image and data saved successfully.",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }.addOnFailureListener { e ->
                            Log.w("AddPictureActivity", "Error adding document", e)
                            Toast.makeText(this, "Failed to save data.", Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener { e ->
                    Log.w("AddPictureActivity", "Error getting document", e)
                    Toast.makeText(this, "Failed to get image URL.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Log.w("AddPictureActivity", "Error uploading image", e)
                Toast.makeText(this, "Failed to upload image.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun dateToString(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val mNow = System.currentTimeMillis()
        val mDate = Date(mNow)
        Log.d("kim", "$mDate")
        return format.format(mDate)
    }
}
package com.example.myhealthybody.util

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

//fun myCheckPermission(activity: AppCompatActivity) {
//
//    val requestPermissionLauncher = activity.registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) {
//        if (it) {
//            Toast.makeText(activity, "권한 승인", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(activity, "권한 거부", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    if (ContextCompat.checkSelfPermission(
//            activity,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        ) !== PackageManager.PERMISSION_GRANTED
//    ) {
//        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//    }
//}
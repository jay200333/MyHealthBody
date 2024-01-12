package com.example.myhealthybody

import android.content.Intent
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.myhealthybody.databinding.ActivityMainBinding
import com.example.myhealthybody.util.myCheckPermission

class MainActivity : AppCompatActivity() {
    lateinit var mbinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mbinding.root)
        myCheckPermission(this)
    }

    override fun onStart() {
        super.onStart()
        if(MyApplication.checkAuth()) {
            val intent = Intent(this, MainViewActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
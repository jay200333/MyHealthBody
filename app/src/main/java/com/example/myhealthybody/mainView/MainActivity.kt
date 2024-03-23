package com.example.myhealthybody.mainView

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myhealthybody.databinding.ActivityMainBinding
import com.example.myhealthybody.login.view.LoginActivity


class MainActivity : AppCompatActivity() {
    private lateinit var mbinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mbinding.root)
    }

    override fun onStart() {
        super.onStart()
        if (MyApplication.checkAuth()) {
            val intent = Intent(this, MainViewActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
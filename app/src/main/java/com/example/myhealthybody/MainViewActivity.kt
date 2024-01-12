package com.example.myhealthybody

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myhealthybody.databinding.ActivityMainViewBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainViewActivity : AppCompatActivity() {
    lateinit var mvBinding : ActivityMainViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mvBinding = ActivityMainViewBinding.inflate(layoutInflater)
        setContentView(mvBinding.root)


        mvBinding.logoutBtn.setOnClickListener {
            // Firebase에서 로그아웃 처리
            MyApplication.auth.signOut()

            // Google 로그아웃 처리 (필요한 경우)
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleLoginClient = GoogleSignIn.getClient(this, gso)
            googleLoginClient.signOut()

            // 로그아웃 후 로그인 화면으로 이동
            val logOutIntent = Intent(this, LoginActivity::class.java)
            startActivity(logOutIntent)
            finish()
        }
    }
}
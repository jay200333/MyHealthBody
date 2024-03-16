package com.example.myhealthybody.settingTab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.myhealthybody.R
import com.example.myhealthybody.databinding.ActivityReauthenticationBinding
import com.example.myhealthybody.login.LoginActivity
import com.example.myhealthybody.mainView.MyApplication
import com.example.myhealthybody.util.AuthUtil
import com.example.myhealthybody.util.AuthUtil.withdrawal
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ActivityReauthentication : AppCompatActivity() {
    lateinit var binding: ActivityReauthenticationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReauthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.checkBtn.setOnClickListener {
            val pw = binding.signupPw.text.toString()
            val pw2 = binding.signupPw2.text.toString()
            if (pw.length >= 6 && pw2.length >= 6) {
                if (pw == pw2) {
                    Toast.makeText(baseContext, "비밀번호 일치", Toast.LENGTH_SHORT).show()
                    binding.reauthBtn.isEnabled = true
                } else {
                    Toast.makeText(baseContext, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(baseContext, "비밀번호를 6자리 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            binding.reauthBtn.setOnClickListener {
                // 이메일, 비밀번호 회원가입......................
                val email = binding.signupEmail.text.toString()
                val password = binding.signupPw.text.toString()
                val user = FirebaseAuth.getInstance().currentUser
                val credential = EmailAuthProvider.getCredential(email, password)
                user?.reauthenticate(credential)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        AuthUtil.withdrawal(user, this) { success, message ->
                            if (success) {
                                navigateToLoginAfterWithdrawal()
                            } else {
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            baseContext,
                            "재인증 실패: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun navigateToLoginAfterWithdrawal() {
        Toast.makeText(this, "탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
        val withdrawalIntent = Intent(this, LoginActivity::class.java)
        startActivity(withdrawalIntent)
        finish()
    }
}
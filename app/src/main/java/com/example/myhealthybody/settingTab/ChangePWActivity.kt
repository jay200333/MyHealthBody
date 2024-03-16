package com.example.myhealthybody.settingTab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myhealthybody.R
import com.example.myhealthybody.databinding.ActivityChangePwactivityBinding
import com.example.myhealthybody.login.LoginActivity
import com.example.myhealthybody.mainView.MyApplication
import com.example.myhealthybody.mainView.MyApplication.Companion.email
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class ChangePWActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangePwactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePwactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.checkBtn.setOnClickListener {
            val pw = binding.changePw.text.toString()
            val pw2 = binding.changePw2.text.toString()
            if (pw.length >= 6 && pw2.length >= 6) {
                if (pw == pw2) {
                    Toast.makeText(baseContext, "비밀번호 일치", Toast.LENGTH_SHORT).show()
                    binding.changePwBtn.isEnabled = true
                } else {
                    Toast.makeText(baseContext, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(baseContext, "비밀번호를 6자리 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            binding.changePwBtn.setOnClickListener {
                // 현재 로그인 되어있는 계정의 비밀번호 변경......................
                val user = FirebaseAuth.getInstance().currentUser
                val password = binding.changePw.text.toString()
                user?.updatePassword(password)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        logout()
                        Toast.makeText(
                            baseContext,
                            "비밀번호가 변경되었습니다. 재로그인 해주시기 바랍니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(baseContext, "비밀번호 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun logout() {
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
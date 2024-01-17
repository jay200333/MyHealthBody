package com.example.myhealthybody

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myhealthybody.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth // 파이어베이스 인증
    private lateinit var mdatabase: DatabaseReference // 실시간 데이터베이스
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(signupBinding.root)

        auth = FirebaseAuth.getInstance()
        mdatabase = FirebaseDatabase.getInstance().reference

        // 로그인 텍스트 클릭시 로그인 화면으로 전환
        signupBinding.loginTxt.setOnClickListener {
            finish()
        }

        signupBinding.checkBtn.setOnClickListener {
            val pw = signupBinding.signupPw.text.toString()
            val pw2 = signupBinding.signupPw2.text.toString()
            if (pw.length >= 6 && pw2.length >= 6) {
                if (pw == pw2) {
                    Toast.makeText(baseContext, "비밀번호 일치", Toast.LENGTH_SHORT).show()
                    signupBinding.signupBtn.isEnabled = true
                } else {
                    Toast.makeText(baseContext, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(baseContext, "비밀번호를 6자리 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            signupBinding.signupBtn.setOnClickListener {
                // 이메일, 비밀번호 회원가입......................
                val email = signupBinding.signupEmail.text.toString()
                val password = signupBinding.signupPw.text.toString()
                MyApplication.auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        signupBinding.signupEmail.text.clear()
                        signupBinding.signupPw.text.clear()
                        if (task.isSuccessful) {
                            MyApplication.auth.currentUser?.sendEmailVerification()
                                ?.addOnCompleteListener { sendTask ->
                                    if (sendTask.isSuccessful) {
                                        Toast.makeText(
                                            baseContext,
                                            "회원가입이 완료되었습니다." +
                                                    "전송된 메일을 확인해 주세요.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(baseContext, "메일 발송 실패", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                        } else {
                            Toast.makeText(baseContext, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
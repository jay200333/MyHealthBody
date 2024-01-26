package com.example.myhealthybody.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myhealthybody.mainView.MyApplication
import com.example.myhealthybody.R
import com.example.myhealthybody.databinding.ActivityPasswordBinding
import java.util.regex.Pattern

class PasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val passwordBinding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(passwordBinding.root)

        // 완료 버튼 클릭시 비밀번호 변경후 로그인 화면으로 이동
        passwordBinding.nextBtn.setOnClickListener {
            val emailText = passwordBinding.findPwId.text.toString().trim()
            if (emailText.isNotEmpty()) {
                if (checkEmailFormat(emailText)) {
                    send(emailText)
                } else {
                    Toast.makeText(baseContext, "이메일 형식으로 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkEmailFormat(emailText: String): Boolean {
        return Pattern.matches(getString(R.string.emailFormat), emailText)
    }

    private fun send(emailText: String) {
        MyApplication.auth.sendPasswordResetEmail(emailText)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "전송된 메일을 확인해주세요.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                // fix 여부 있음
                else {
                    Toast.makeText(this, "인증된 메일이 아닙니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
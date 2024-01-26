package com.example.myhealthybody.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.myhealthybody.mainView.MainViewActivity
import com.example.myhealthybody.mainView.MyApplication
import com.example.myhealthybody.R
import com.example.myhealthybody.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {

    // Google 로그인 결과를 처리하기 위한 ActivityResultLauncher
    private val requestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google 로그인 성공시, MainViewActivity로 이동
                val account = task.getResult(ApiException::class.java)
                val intent = Intent(this, MainViewActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: ApiException) {
                Toast.makeText(baseContext, "구글 로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        loginBinding.loginGoogleBtn.setOnClickListener {
            // 구글 로그인................
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleLoginIntent = GoogleSignIn.getClient(this, gso).signInIntent
            requestLauncher.launch(googleLoginIntent)
        }

        loginBinding.loginBtn.setOnClickListener {
            // 이메일, 비밀번호 로그인 .....................
            val email = loginBinding.inputLoginId.text.toString()
            val password = loginBinding.inputLoginPw.text.toString()
            Log.d("kim", "email: $email, password: $password")
            MyApplication.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    loginBinding.inputLoginId.text.clear()
                    loginBinding.inputLoginPw.text.clear()
                    if (task.isSuccessful) {
                        if (MyApplication.checkAuth()) {
                            MyApplication.email = email
                            val loginIntent = Intent(this, MainViewActivity::class.java)
                            startActivity(loginIntent)
                        } else {
                            Toast.makeText(
                                baseContext,
                                "전송된 메일로 이메일 인증이 되지 않았습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(baseContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // 비밀번호를 잊으셨나요 클릭시 비밀번호 찾는 화면으로 이동
        loginBinding.forgotPwTxt.setOnClickListener {
            val forgotIntent = Intent(this, PasswordActivity::class.java)
            startActivity(forgotIntent)
        }

        // 회원가입 클릭시 회원가입 화면으로 이동
        loginBinding.signupTxt.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }
    }
}
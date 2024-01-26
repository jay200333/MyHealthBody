package com.example.myhealthybody.setting

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myhealthybody.databinding.FragmentThreeBinding
import com.example.myhealthybody.login.LoginActivity
import com.example.myhealthybody.mainView.MyApplication
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class FragmentThree : Fragment() {
    private lateinit var mBinding: FragmentThreeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentThreeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.logoutBtn.setOnClickListener {
            // Firebase에서 로그아웃 처리
            MyApplication.auth.signOut()

            // Google 로그아웃 처리 (필요한 경우)
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleLoginClient = GoogleSignIn.getClient(requireActivity(), gso)
            googleLoginClient.signOut()

            // 로그아웃 후 로그인 화면으로 이동
            val logOutIntent = Intent(activity, LoginActivity::class.java)
            startActivity(logOutIntent)
            requireActivity().finish()
        }

    }
}
package com.example.myhealthybody.settingTab

import android.app.AlertDialog
import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.example.myhealthybody.R
import com.example.myhealthybody.databinding.FragmentSettingBinding
import com.example.myhealthybody.login.LoginActivity
import com.example.myhealthybody.mainView.MyApplication
import com.example.myhealthybody.mainView.MyApplication.Companion.email
import com.example.myhealthybody.util.AuthUtil
import com.example.myhealthybody.util.AuthUtil.withdrawal
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.play.core.integrity.p
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlin.concurrent.timerTask


class Setting : Fragment() {
    private lateinit var mBinding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSettingBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.currentId.text = FirebaseAuth.getInstance().currentUser?.email.toString()
        mBinding.unitLayout.setOnClickListener {
            if (mBinding.unitOptionsRadioGroup.visibility == View.GONE) {
                mBinding.unitOptionsRadioGroup.visibility = View.VISIBLE
                mBinding.unitArrow.setImageResource(R.drawable.baseline_expand_more_24)
            } else {
                mBinding.unitOptionsRadioGroup.visibility = View.GONE
                mBinding.unitArrow.setImageResource(R.drawable.baseline_expand_less_24)
            }
        }
        // 초기 무게 단위
        mBinding.unitOptionsRadioGroup.check(R.id.radio_kg)

        // 각 무게 단위 radio button 선택시 처리
        mBinding.unitOptionsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_kg -> {
                    showUnitChangeAlert()
                    Toast.makeText(context, "kg이 선택되었습니다.", Toast.LENGTH_SHORT).show()
                }

                R.id.radio_lb -> {
                    showUnitChangeAlert()
                    Toast.makeText(context, "lbs가 선택되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        mBinding.changepwBtn.setOnClickListener {
            changePW()
        }
        mBinding.logoutBtn.setOnClickListener {
            showLogoutAlert()
        }
        mBinding.withdrawalBtn.setOnClickListener {
            showWithdrawalAlert()
        }
    }

    private fun showUnitChangeAlert() {
        AlertDialog.Builder(requireContext()).run {
            setTitle("무게 단위 변경")
            setMessage("그 동안 입력했던 무게가 연산에 의해 소수점 자리로 표시될 수 있습니다.")
            setPositiveButton("확인", null)
            setNegativeButton("취소", null)
            show()
        }.setCanceledOnTouchOutside(true)
    }

    private fun changePW() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let { currentUser ->
            when {
                hasEmailProvider(currentUser) -> {
                    val pwIntent = Intent(activity, ChangePWActivity::class.java)
                    startActivity(pwIntent)
                    requireActivity().finish()
                }
                // 사용자가 Google 계정으로 로그인
                hasGoogleProvider(currentUser) -> {
                    Toast.makeText(
                        context,
                        "Google 계정은 Google 사이트에서 변경하실 수 있습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showLogoutAlert() {
        AlertDialog.Builder(requireContext()).run {
            setTitle("Log out")
            setMessage("로그아웃 상태에서는 기록된 운동결과를 확인 할 수 없습니다. 정말 로그아웃 하시겠습니까?")
            setPositiveButton("확인") { _, _ ->
                logout()
            }
            setNegativeButton("취소", null)
            show()
        }.setCanceledOnTouchOutside(true)
    }

    private fun showWithdrawalAlert() {
        val withdrawalAlertDialog =
            AlertDialog.Builder(requireContext()).apply {
                setTitle("계정 삭제하기")
                setMessage("계정을 삭제되면 계정에 귀속된 기록은 복원이 불가능합니다. 정말 계정을 삭제할까요?")
                setPositiveButton("네, 삭제할게요") { _, _ ->
                    reauthenticate()
                }
                setNegativeButton("더 사용할게요", null)
            }.create()
        withdrawalAlertDialog.show()
        withdrawalAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.red))
        withdrawalAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.blue))
        withdrawalAlertDialog.setCanceledOnTouchOutside(true)
    }

    private fun logout() {
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

    private fun reauthenticate() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let { currentUser ->
            when {
                hasEmailProvider(currentUser) -> {
                    Log.d("Auth", "User is signed in with email/password")
                    val reauthIntent = Intent(activity, ActivityReauthentication::class.java)
                    startActivity(reauthIntent)
                    requireActivity().finish()
                }
                // 사용자가 Google 계정으로 로그인
                hasGoogleProvider(currentUser) -> {
                    reauthenticateWithGoogle(currentUser)
                }
            }
        }
    }

    private fun hasEmailProvider(user: FirebaseUser): Boolean {
        return user.providerData.any { it.providerId == EmailAuthProvider.PROVIDER_ID }
    }

    private fun hasGoogleProvider(user: FirebaseUser): Boolean {
        return user.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }
    }

    private fun reauthenticateWithGoogle(user: FirebaseUser) {
        Log.d("Auth", "User is signed in with Google")
        GoogleSignIn.getLastSignedInAccount(requireContext())
            ?.let { googleAccount ->
                val credential =
                    GoogleAuthProvider.getCredential(googleAccount.idToken, null)
                user?.reauthenticate(credential)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        AuthUtil.withdrawal(user, requireContext()) { success, message ->
                            if (success) {
                                navigateToLoginAfterWithdrawal()
                            } else {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Google 계정 재인증 실패했습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun navigateToLoginAfterWithdrawal() {
        Toast.makeText(context, "탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
        val withdrawalIntent = Intent(context, LoginActivity::class.java)
        startActivity(withdrawalIntent)
        requireActivity().finish()
    }
}
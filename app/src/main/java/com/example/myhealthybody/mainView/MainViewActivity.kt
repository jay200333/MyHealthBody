package com.example.myhealthybody.mainView

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.myhealthybody.R
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.model.ExerciseViewModel
import com.example.myhealthybody.gallery.view.GalleryFragment
import com.example.myhealthybody.diary.view.DiaryFragment
import com.example.myhealthybody.mainView.adapter.ViewPagerAdapter
import com.example.myhealthybody.databinding.ActivityMainViewBinding
import com.example.myhealthybody.healthTraining.view.HealthTrainingFragment
import com.example.myhealthybody.setting.view.SettingFragment
import com.google.android.material.tabs.TabLayoutMediator

class MainViewActivity : AppCompatActivity() {
    private lateinit var mvBinding: ActivityMainViewBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var exercisesData: List<ExerciseData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mvBinding = ActivityMainViewBinding.inflate(layoutInflater)
        setContentView(mvBinding.root)

        ViewModelProvider(this)[ExerciseViewModel::class.java]
        setViewpagerInit()
        setTabInit()

        // 뒤로 가기 콜백
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showCloseAppAlert()
            }
        })
    }

    fun showCloseAppAlert() {
        AlertDialog.Builder(this@MainViewActivity).run {
            setMessage("앱을 종료하시겠습니까?")
            setPositiveButton("확인") { _, _ ->
                finishAffinity()
            }
            setNegativeButton("취소", null)
            show()
        }.setCanceledOnTouchOutside(false)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // 인텐트에서 "navigateTo" 값 가져오기
        val navigateTo = intent?.getStringExtra("navigateTo")
        val selectedDate = intent?.getStringExtra("selectedDate")
        val dataUpdated = intent?.getBooleanExtra("dataUpdated", false) ?: false
        if (navigateTo == "FragmentTwo") {
            mvBinding.mainViewPager.currentItem = 1
            val fragment = viewPagerAdapter.fragments[1] as DiaryFragment
            fragment.arguments = Bundle().apply {
                putString("selectedDate", selectedDate)
            }
            if (dataUpdated) {
                fragment?.refreshData()
            }
        }
    }

    private fun setViewpagerInit() {
        val fragmentList = listOf(
            HealthTrainingFragment(),
            DiaryFragment(),
            GalleryFragment(),
            SettingFragment()
        )
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPagerAdapter.fragments.addAll(fragmentList)
        mvBinding.mainViewPager.adapter = viewPagerAdapter
    }

    private fun setTabInit() {
        TabLayoutMediator(mvBinding.mainViewTabs, mvBinding.mainViewPager) { tab, pos ->
            tab.text = pos.toString()
            when (pos) {
                0 -> {
                    tab.text = "훈련"
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.baseline_menu_book_24)
                }

                1 -> {
                    tab.text = "운동 일지"
                    tab.icon =
                        ContextCompat.getDrawable(this, R.drawable.baseline_calendar_month_24)
                }

                2 -> {
                    tab.text = "갤러리"
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.baseline_image_24)
                }

                3 -> {
                    tab.text = "설정"
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.baseline_settings_24)
                }
            }
        }.attach()
    }
}
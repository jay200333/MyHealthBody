package com.example.myhealthybody.mainView

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.ExerciseViewModel
import com.example.myhealthybody.pictureTab.FragmentThree
import com.example.myhealthybody.diaryTab.FragmentTwo
import com.example.myhealthybody.mainView.adapter.ViewPagerAdapter
import com.example.myhealthybody.databinding.ActivityMainViewBinding
import com.example.myhealthybody.healthTab.FragmentOne
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
    }

    private fun setViewpagerInit() {
        val fragmentList = listOf(
            FragmentOne(),
            FragmentTwo(),
            FragmentThree()
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
                }

                1 -> {
                    tab.text = "운동 일지"
                }

                2 -> {
                    tab.text = "오운완"
                }
            }
        }.attach()
    }
}
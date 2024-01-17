package com.example.myhealthybody

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myhealthybody.databinding.ActivityMainViewBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainViewActivity : AppCompatActivity() {
    private lateinit var mvBinding: ActivityMainViewBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mvBinding = ActivityMainViewBinding.inflate(layoutInflater)
        setContentView(mvBinding.root)
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
                    tab.text = "공백"
                }

                2 -> {
                    tab.text = "설정"
                }
            }
        }.attach()
    }
}
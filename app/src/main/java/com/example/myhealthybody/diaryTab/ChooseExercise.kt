package com.example.myhealthybody.diaryTab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.ExerciseViewModel
import com.example.myhealthybody.R
import com.example.myhealthybody.databinding.ActivityChooseExerciseBinding
import com.example.myhealthybody.diaryTab.adapter.ChooseExerciseRecyclerAdapter
import com.example.myhealthybody.diaryTab.adapter.OnCheckboxChangeCallback
import com.google.android.material.tabs.TabLayout
import java.io.Serializable

class ChooseExercise : AppCompatActivity(), OnCheckboxChangeCallback {
    private lateinit var binding: ActivityChooseExerciseBinding
    private lateinit var chooseExerciseRecyclerView: RecyclerView
    private lateinit var chooseExerciseRecyclerAdapter: ChooseExerciseRecyclerAdapter
    private lateinit var viewModel: ExerciseViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseExerciseBinding.inflate(layoutInflater)
        chooseExerciseRecyclerView = binding.chooseExerciseRecycler
        setContentView(binding.root)
        setupTabs()

        viewModel = ViewModelProvider(this)[ExerciseViewModel::class.java]
        viewModel.loadExercises()

        viewModel.exerciseData.observe(this) { exercises ->
            setUpRecyclerView(exercises)
        }
        binding.countChooseBtn.setOnClickListener {
            val selectedExercises = viewModel.checkedExercises.value ?: return@setOnClickListener
            val intent = Intent(this, SelectSetActivity::class.java)
            intent.putExtra("selectedExercises", selectedExercises as Serializable)
            startActivity(intent)
        }
    }

    private fun setupTabs() {
        val exerciseTargets = resources.getStringArray(R.array.exercise_targets)
        val tabLayout = binding.chooseTab
        exerciseTargets.forEach { target ->
            tabLayout.addTab(tabLayout.newTab().setText(target))
        }

        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val selectedTarget = tab?.text.toString()
                chooseExerciseRecyclerAdapter.filterByTarget(selectedTarget)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun setUpRecyclerView(exerciseData: List<ExerciseData>) {
        chooseExerciseRecyclerAdapter = ChooseExerciseRecyclerAdapter(exerciseData, viewModel, this)
        chooseExerciseRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = chooseExerciseRecyclerAdapter
        }
    }

    override fun onCheckedChange(isChecked: Boolean, checkedCount: Int) {
        // 체크박스 변경에 따라 텍스트 업데이트
        if (checkedCount == 0) {
            binding.countChooseBtn.text = "운동을 선택해주세요!"
            binding.countChooseBtn.isEnabled = false
        } else {
            binding.countChooseBtn.text = "${checkedCount}개의 운동 추가하기"
            binding.countChooseBtn.isEnabled = true
        }
    }
}
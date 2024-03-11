package com.example.myhealthybody.diaryTab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.model.ExerciseViewModel
import com.example.myhealthybody.R
import com.example.myhealthybody.databinding.ActivityChooseExerciseBinding
import com.example.myhealthybody.diaryTab.adapter.ChooseExerciseRecyclerAdapter
import com.example.myhealthybody.diaryTab.adapter.OnCheckboxChangeCallback
import com.google.android.material.chip.Chip
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
        val selectedDate = intent.getStringExtra("selectedDate")
        setContentView(binding.root)
        setupTabs()

        viewModel = ViewModelProvider(this)[ExerciseViewModel::class.java]
        viewModel.loadExercises()

        viewModel.exerciseData.observe(this) { exercises ->
            setUpRecyclerView(exercises)
        }
        viewModel.checkedCount.observe(this) { count ->
            updateCheckedCount(count)
        }
        binding.countChooseBtn.setOnClickListener {
            val selectedExercises = viewModel.checkedExercises.value ?: return@setOnClickListener
            val intent = Intent(this, SelectSetActivity::class.java)
            intent.putExtra("selectedExercises", selectedExercises as Serializable)
            intent.putExtra("selectedDate", selectedDate)
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
        chooseExerciseRecyclerAdapter =
            ChooseExerciseRecyclerAdapter(exerciseData, viewModel, this)
        chooseExerciseRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = chooseExerciseRecyclerAdapter
        }
    }

    override fun onCheckedChange(
        isChecked: Boolean,
        exerciseData: ExerciseData
    ) {
        runOnUiThread {
            if (isChecked) {
                val chip = Chip(this).apply {
                    text = exerciseData.name
                    tag = exerciseData.id
                    isCloseIconVisible = true
                    setOnCloseIconClickListener {
                        // chip의 x 버튼이 눌린 경우
                        binding.chooseChipGroup.removeView(this)
                        chooseExerciseRecyclerAdapter.uncheckExercise(exerciseData.id)
                    }
                }
                binding.chooseChipGroup.addView(chip)
            } else {
                val chipToRemove =
                    binding.chooseChipGroup.findViewWithTag<Chip>(exerciseData.id)
                chipToRemove?.let {
                    binding.chooseChipGroup.removeView(it)
                }
            }
        }
    }

    // 체크된 아이템 개수를 업데이트하고 버튼의 텍스트를 변경하는 메소드
    private fun updateCheckedCount(checkedCount: Int) {
        binding.countChooseBtn.text = if (checkedCount > 0) {
            "${checkedCount}개의 운동 추가하기"
        } else {
            "운동을 선택해주세요!"
        }
        binding.countChooseBtn.isEnabled = checkedCount > 0
    }
}
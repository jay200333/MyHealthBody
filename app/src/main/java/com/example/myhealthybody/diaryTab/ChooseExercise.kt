package com.example.myhealthybody.diaryTab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.ExerciseData
import com.example.myhealthybody.ExerciseViewModel
import com.example.myhealthybody.R
import com.example.myhealthybody.databinding.ActivityChooseExerciseBinding
import com.example.myhealthybody.diaryTab.adapter.ChooseExerciseRecyclerAdapter
import com.example.myhealthybody.diaryTab.adapter.OnCheckboxChangeCallback
import com.example.myhealthybody.healthTab.FragmentOneItemActivity

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

        viewModel = ViewModelProvider(this)[ExerciseViewModel::class.java]
        viewModel.loadExercises()

        viewModel.exerciseData.observe(this) { exercises ->
            setUpRecyclerView(exercises)
        }
    }

    private fun setUpRecyclerView(exerciseData: List<ExerciseData>) {
        chooseExerciseRecyclerAdapter = ChooseExerciseRecyclerAdapter(exerciseData, this)
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
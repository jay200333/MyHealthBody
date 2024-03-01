package com.example.myhealthybody.diaryTab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.model.ExerciseViewModel
import com.example.myhealthybody.databinding.ActivitySelectSetBinding
import com.example.myhealthybody.diaryTab.adapter.SelectSetAdapter
import com.example.myhealthybody.diaryTab.adapter.TotalWeightUpdateListener
import com.example.myhealthybody.model.EditData
import com.example.myhealthybody.model.SelectSetData

class SelectSetActivity : AppCompatActivity(), TotalWeightUpdateListener {
    private lateinit var binding: ActivitySelectSetBinding
    private lateinit var viewModel: ExerciseViewModel
    private lateinit var selectSetRecyclerView: RecyclerView
    private lateinit var selectSetAdapter: SelectSetAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectSetBinding.inflate(layoutInflater)
        selectSetRecyclerView = binding.showSetRecycler
        setContentView(binding.root)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this)[ExerciseViewModel::class.java]

        // 어댑터 초기화
        val selectedExercises =
            intent.getSerializableExtra("selectedExercises") as? List<ExerciseData> ?: return
        // selectedExercises를 SelectSetData 리스트로 변환
        val selectSetItems = selectedExercises.map { SelectSetData(it, EditData()) }.toMutableList()
        selectSetAdapter = SelectSetAdapter(selectSetItems, viewModel, this)
        binding.showSetRecycler.apply {
            layoutManager = LinearLayoutManager(this@SelectSetActivity)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = selectSetAdapter

        }
    }

    override fun onTotalWeightUpdated(totalWeight: Int) {
        binding.totalVolume.text = "$totalWeight kg"
    }
}
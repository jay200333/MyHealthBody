package com.example.myhealthybody.diaryTab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.ExerciseViewModel
import com.example.myhealthybody.databinding.ActivitySelectSetBinding
import com.example.myhealthybody.diaryTab.adapter.SelectSetAdapter
import com.example.myhealthybody.model.EditData
import com.example.myhealthybody.model.SelectSetData

class SelectSetActivity : AppCompatActivity() {
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
        selectSetAdapter = SelectSetAdapter(selectSetItems, viewModel)
        binding.showSetRecycler.apply {
            layoutManager = LinearLayoutManager(this@SelectSetActivity)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = selectSetAdapter

        }
    }
}

//        viewModel.checkedExercises.observe(this) { checkedExercises ->
//            setUpRecyclerView(checkedExercises)
//            selectSetAdapter.updateData(checkedExercises)
//        }

//    private fun setUpRecyclerView(checkedExercises: List<ExerciseData>) {
//        if (!::selectSetAdapter.isInitialized) {
//            selectSetAdapter = SelectSetAdapter(checkedExercises.toMutableList(), viewModel)
//            selectSetRecyclerView.apply {
//                layoutManager = LinearLayoutManager(this@SelectSetActivity)
//                addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
//                adapter = selectSetAdapter
//            }
//        } else {
//            selectSetAdapter.updateData(checkedExercises)
//        }
//    }
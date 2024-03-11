package com.example.myhealthybody.healthTab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.util.RecyclerDecoration
import com.example.myhealthybody.databinding.ActivityFragmentOneItemBinding
import com.example.myhealthybody.healthTab.adapter.FragmentOneClickedRecyclerAdapter

class FragmentOneItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFragmentOneItemBinding
    private lateinit var instructionRecyclerView: RecyclerView
    private lateinit var instructionRecyclerAdapter: FragmentOneClickedRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentOneItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra("itemExerciseGif")
        if (url != null) {
            binding.gifWebview.settings.useWideViewPort = true
            binding.gifWebview.settings.loadWithOverviewMode = true
            binding.gifWebview.loadUrl(url)
        }
        val exerciseName = intent.getStringExtra("itemExerciseName")
        binding.exerciseName.text = exerciseName

        val exerciseTarget = intent.getStringExtra("itemExerciseTarget")
        binding.exerciseTarget.text = exerciseTarget

        val exerciseEquipment = intent.getStringExtra("itemExerciseEquipment")
        binding.exerciseEquipment.text = exerciseEquipment

        val exerciseInstruction =
            intent.getStringArrayListExtra("itemExerciseInstruction") ?: arrayListOf()
        // 데이터 확인 (디버깅을 위해 로그 출력)
        Log.d("kim", "Instructions: $exerciseInstruction")
        instructionRecyclerView = binding.exerciseInstruction
        instructionRecyclerView.layoutManager = LinearLayoutManager(this)
        instructionRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        instructionRecyclerView.addItemDecoration(RecyclerDecoration(this))
        instructionRecyclerAdapter = FragmentOneClickedRecyclerAdapter(exerciseInstruction)
        instructionRecyclerView.adapter = instructionRecyclerAdapter

        // 완료 버튼 클릭시 이전 페이지로 이동
        binding.completeBtn.setOnClickListener {
            finish()
        }
    }
}
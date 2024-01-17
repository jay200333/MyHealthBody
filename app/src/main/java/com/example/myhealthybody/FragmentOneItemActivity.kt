package com.example.myhealthybody

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.example.myhealthybody.databinding.ActivityFragmentOneItemBinding

class FragmentOneItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFragmentOneItemBinding
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
        val instructionListView = binding.exerciseInstruction
        val instructionAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, exerciseInstruction)
        instructionListView.adapter = instructionAdapter

        // 완료 버튼 클릭시 이전 페이지로 이동
        binding.completeBtn.setOnClickListener {
            finish()
        }


    }
}
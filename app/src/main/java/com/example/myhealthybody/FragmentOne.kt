package com.example.myhealthybody

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.databinding.FragmentOneBinding

class FragmentOne : Fragment() {
    private lateinit var mBinding: FragmentOneBinding
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseRepository: ExerciseRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentOneBinding.inflate(inflater, container, false)
        recyclerView = mBinding.fragmentOneRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        exerciseRepository = ExerciseRepository()
        exerciseRepository.loadExercises { exerciseData, error ->
            activity?.runOnUiThread {
                if (error != null) {
                    Log.d("kim", "데이터를 불러오는 과정에서 오류가 발생하였습니다.")

                } else if (exerciseData != null) {
                    // 성공적으로 데이터를 받아온 경우
                    Log.d("kim", "데이터를 성공적으로 불러왔습니다.")
                    recyclerAdapter = RecyclerAdapter(exerciseData) { exercise ->
                        // 아이템 클릭 이벤트 처리
                        Log.d("kim", "${exercise.name}이 클릭됨")
                        val intent = Intent(requireContext(), FragmentOneItemActivity::class.java)
                        putData(intent, exercise)
                        startActivity(intent)
                    }
                    recyclerView.adapter = recyclerAdapter
                }
            }
        }
        return mBinding.root
    }

    private fun putData(intent: Intent, exercise: ExerciseData) {
        intent.putExtra("itemExerciseName", exercise.name)
        intent.putExtra("itemExerciseTarget", exercise.target)
        intent.putExtra("itemExerciseId", exercise.id)
        intent.putExtra("itemExerciseGif", exercise.gifUrl)
        intent.putExtra("itemExerciseEquipment", exercise.equipment)
        intent.putStringArrayListExtra(
            "itemExerciseInstruction",
            ArrayList(exercise.instructions)
        )
    }
}
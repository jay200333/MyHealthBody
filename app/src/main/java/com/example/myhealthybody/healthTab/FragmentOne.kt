package com.example.myhealthybody.healthTab

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.ExerciseData
import com.example.myhealthybody.ExerciseRepository
import com.example.myhealthybody.ExerciseViewModel
import com.example.myhealthybody.RecyclerDecoration
import com.example.myhealthybody.databinding.FragmentOneBinding
import com.example.myhealthybody.healthTab.adapter.RecyclerAdapter
import com.example.myhealthybody.mainView.MainViewActivity

class FragmentOne : Fragment() {
    private lateinit var mBinding: FragmentOneBinding
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ExerciseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentOneBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = mBinding.fragmentOneRecyclerView

        // ViewModel 초기화 및 데이터 로드
        viewModel = ViewModelProvider(requireActivity())[ExerciseViewModel::class.java]
        viewModel.loadExercises()

        // 데이터와 에러 관찰
        viewModel.exerciseData.observe(viewLifecycleOwner) { exercises ->
            setupRecyclerView(exercises)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            showError(errorMessage)
        }
    }

    private fun setupRecyclerView(exercisesData: List<ExerciseData>) {
        recyclerAdapter = RecyclerAdapter(exercisesData) { exercise ->
            onExerciseClicked(exercise)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(RecyclerDecoration(requireContext()))
            adapter = recyclerAdapter
        }
    }

    private fun onExerciseClicked(exercise: ExerciseData) {
        Log.d("kim", "${exercise.name}이 클릭됨")
        val intent = Intent(requireContext(), FragmentOneItemActivity::class.java)
        putData(intent, exercise)
        startActivity(intent)
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

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
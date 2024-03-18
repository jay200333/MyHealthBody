package com.example.myhealthybody.diaryTab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.model.ExerciseViewModel
import com.example.myhealthybody.databinding.ActivitySelectSetBinding
import com.example.myhealthybody.diaryTab.adapter.SelectSetAdapter
import com.example.myhealthybody.diaryTab.adapter.TotalWeightUpdateListener
import com.example.myhealthybody.mainView.MainViewActivity
import com.example.myhealthybody.model.EditData
import com.example.myhealthybody.model.SelectSetData
import com.example.myhealthybody.model.SetItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SelectSetActivity : AppCompatActivity(), TotalWeightUpdateListener {
    private lateinit var binding: ActivitySelectSetBinding
    private lateinit var viewModel: ExerciseViewModel
    private lateinit var selectSetRecyclerView: RecyclerView
    private lateinit var selectSetAdapter: SelectSetAdapter
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")
    private var userId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectSetBinding.inflate(layoutInflater)
        selectSetRecyclerView = binding.showSetRecycler
        setContentView(binding.root)

        userId = FirebaseAuth.getInstance().currentUser?.uid

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
        val selectedDate = intent.getStringExtra("selectedDate")
        selectedDate?.let {
            loadWorkoutDataForDate(it)
        }

        binding.setCompleteBtn.setOnClickListener {
            val allExercises = selectSetAdapter.getAllExerciseData()
            saveWorkoutData(selectedDate, allExercises)
            val intent = Intent(this, MainViewActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("navigateTo", "FragmentTwo")
            }
            startActivity(intent)
        }
    }

    private fun saveWorkoutData(date: String?, exercises: List<ExerciseData>) {
        val uid = userId ?: let {
            Toast.makeText(baseContext, "아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (date == null) {
            Toast.makeText(baseContext, "날짜가 지정되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 전체 볼륨
        val sessionTotalWeight = exercises.sumOf { it.setTotalWeight }
        val dateRef = usersRef.child(uid).child("workouts").child(date)
        exercises.forEach { exercise ->
            val exerciseId = dateRef.child("exercises").push().key ?: return
            val exerciseMap = exercise.toMap().toMutableMap()
            exerciseMap["setTotalWeight"] = exercise.setTotalWeight
            dateRef.child("exercises").child(exerciseId).setValue(exerciseMap)
        }

        val sessionInfo = mapOf(
            "date" to date,
            "sessionTotalWeight" to sessionTotalWeight
        )
        dateRef.child("sessionInfo").setValue(sessionInfo)
    }

    private fun loadWorkoutDataForDate(date: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseDatabase.getInstance().getReference("users/$uid/workouts/$date/exercises")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val exercises = mutableListOf<ExerciseData>()
                    snapshot.children.forEach { child ->
                        child.getValue(ExerciseData::class.java)?.let { exercises.add(it) }
                    }
                    // 데이터가 조회되면 어댑터에 전달
                    //updateRecyclerView(exercises)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        applicationContext,
                        "데이터 로드 실패: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun ExerciseData.toMap(): Map<String, Any> {
        return mapOf(
            "bodyPart" to bodyPart,
            "equipment" to equipment,
            "gifUrl" to gifUrl,
            "id" to id,
            "name" to name,
            "target" to target,
            "secondaryMuscles" to secondaryMuscles,
            "instructions" to instructions,
            "setItems" to setItems.map { it.toMap() }
        )
    }

    fun SetItem.toMap(): Map<String, Any> {
        return mapOf(
            "setCount" to setCount,
            "weight" to weight,
            "tryCount" to tryCount
        )
    }

    override fun onTotalWeightUpdated(totalWeight: Int) {
        binding.totalVolume.text = "$totalWeight kg"
    }
}

//    private fun updateRecyclerView(exercises: List<ExerciseData>) {
//        // SelectSetAdapter 인스턴스가 이미 있으면 데이터 세트 업데이트, 없으면 새로 생성
//        if (::selectSetAdapter.isInitialized) {
//            selectSetAdapter.updateDataSetWithExerciseData(exercises)
//        } else {
//            val selectSetItems = exercises.map { SelectSetData(it, EditData()) }.toMutableList()
//            selectSetAdapter = SelectSetAdapter(selectSetItems, viewModel, this)
//            selectSetRecyclerView.adapter = selectSetAdapter
//        }
//    }
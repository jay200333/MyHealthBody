package com.example.myhealthybody.diary.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.databinding.ActivitySelectSetBinding
import com.example.myhealthybody.diary.view.adapter.EditExerciseAdapter
import com.example.myhealthybody.diary.view.adapter.TotalWeightUpdateListener
import com.example.myhealthybody.mainView.MainViewActivity
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.model.ExerciseViewModel
import com.example.myhealthybody.model.SessionInfo
import com.example.myhealthybody.model.SetItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditExerciseActivity : AppCompatActivity(), TotalWeightUpdateListener {
    private lateinit var binding: ActivitySelectSetBinding
    private lateinit var viewModel: ExerciseViewModel
    private lateinit var editSetRecyclerView: RecyclerView
    private lateinit var editSetAdapter: EditExerciseAdapter
    private lateinit var sessionInfo: SessionInfo
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")
    private var userId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectSetBinding.inflate(layoutInflater)
        editSetRecyclerView = binding.showSetRecycler
        setContentView(binding.root)

        userId = FirebaseAuth.getInstance().currentUser?.uid

        viewModel = ViewModelProvider(this)[ExerciseViewModel::class.java]

        val selectedDate = intent.getStringExtra("selectedDate").toString()
        loadWorkoutDataForDate(selectedDate)
        loadDetailExerciseData(selectedDate)
        binding.setCompleteBtn.text = "편집 완료"
        binding.setCompleteBtn.setOnClickListener {
            val allExercises = editSetAdapter.getAllExerciseData()
            // 변경 사항 업데이트
            updateWorkoutData(selectedDate, allExercises)
            val intent = Intent(this, MainViewActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("navigateTo", "FragmentTwo")
                putExtra("selectedDate", selectedDate)
                putExtra("dataUpdated", true)
            }
            startActivity(intent)
        }
    }

    private fun updateWorkoutData(date: String?, exercises: List<ExerciseData>) {
        val uid = userId ?: let {
            Toast.makeText(baseContext, "아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (date == null) {
            Toast.makeText(baseContext, "날짜가 지정되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        exercises.forEach { exercise ->
            exercise.setTotalWeight = exercise.setItems.sumOf { it.weight * it.tryCount }
        }

        // 전체 볼륨
        val sessionTotalWeight = exercises.sumOf { it.setTotalWeight }
        val dateRef = usersRef.child(uid).child("workouts").child(date)
        // Firebase Database에서 현재 저장된 모든 운동 정보를 불러옵니다.
        dateRef.child("exercises").get().addOnSuccessListener { dataSnapshot ->
            val firebaseExercises = dataSnapshot.children.mapNotNull { it.key }.toSet()
            val localExerciseIds = exercises.mapNotNull { it.firebaseExerciseId }.toSet()

            // Firebase에는 있지만 로컬에는 없는 운동 정보를 찾아 삭제합니다.
            val exercisesToDelete = firebaseExercises - localExerciseIds
            exercisesToDelete.forEach { exerciseId ->
                dateRef.child("exercises").child(exerciseId).removeValue()
            }

            // 로컬에서 변경된 운동 정보를 Firebase에 업데이트합니다.
            exercises.forEach { exercise ->
                exercise.firebaseExerciseId?.let { firebaseExerciseId ->
                    dateRef.child("exercises").child(firebaseExerciseId).setValue(exercise.toMap())
                }
            }
        }
        // 세션 정보 업데이트
        val updates = hashMapOf<String, Any>().apply {
            this["sessionInfo"] = mapOf("date" to date, "sessionTotalWeight" to sessionTotalWeight)
        }
        dateRef.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(baseContext, "운동 데이터가 업데이트 되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    baseContext,
                    "업데이트 실패: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun ExerciseData.toMap(): Map<String, Any> {
        return mapOf(
            "bodyPart" to bodyPart,
            "equipment" to equipment,
            "gifUrl" to gifUrl,
            "id" to id,
            "name" to name,
            "target" to target,
            "secondaryMuscles" to secondaryMuscles,
            "instructions" to instructions,
            "setItems" to setItems.map { it.toMap() },
            "setTotalWeight" to setTotalWeight
        )
    }

    private fun SetItem.toMap(): Map<String, Any> {
        return mapOf(
            "setCount" to setCount,
            "weight" to weight,
            "tryCount" to tryCount
        )
    }

    private fun loadWorkoutDataForDate(date: String) {
        val uid = userId ?: let {
            Toast.makeText(baseContext, "아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val dateRef = usersRef.child(uid).child("workouts").child(date)
        dateRef.child("sessionInfo").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sessionInfo = snapshot.getValue(SessionInfo::class.java) ?: return
                binding.totalVolume.text = sessionInfo.sessionTotalWeight.toString() + "kg"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    "EditExerciseActivity 데이터 로드 실패: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun loadDetailExerciseData(date: String) {
        val uid = userId ?: let {
            Toast.makeText(baseContext, "아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val dateRef = usersRef.child(uid).child("workouts").child(date)
        dateRef.child("exercises").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exercises = mutableListOf<ExerciseData>()
                snapshot.children.forEach { childSnapshot ->
                    val exercise = childSnapshot.getValue(ExerciseData::class.java)
                    exercise?.firebaseExerciseId = childSnapshot.key
                    exercise?.let { exercises.add(it) }
                }
                editSetAdapter =
                    EditExerciseAdapter(exercises, sessionInfo, this@EditExerciseActivity)
                editSetRecyclerView.adapter = editSetAdapter
                editSetRecyclerView.layoutManager = LinearLayoutManager(this@EditExerciseActivity)
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

    override fun onTotalWeightUpdated(totalWeight: Int) {
        binding.totalVolume.text = "$totalWeight kg"
    }
}
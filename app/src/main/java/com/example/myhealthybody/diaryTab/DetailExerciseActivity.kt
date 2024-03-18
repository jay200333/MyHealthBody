package com.example.myhealthybody.diaryTab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.R
import com.example.myhealthybody.databinding.ActivityDetailExerciseBinding
import com.example.myhealthybody.diaryTab.adapter.DetailExerciseAdapter
import com.example.myhealthybody.mainView.MyApplication
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.model.PictureData
import com.example.myhealthybody.model.SessionInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.Query
import okhttp3.internal.format

class DetailExerciseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailExerciseBinding
    private lateinit var detailExerciseView: RecyclerView
    private lateinit var adapter: DetailExerciseAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailExerciseView = binding.detailExercise

        val selectedDate = intent.getStringExtra("selectedDate")
        binding.detailDate.text = selectedDate.toString() + ", 운동 요약"
        val formattedDate = formatDate(selectedDate.toString())

        loadDetailSession(formattedDate)
        loadDetailExerciseData(formattedDate)
    }

    private fun formatDate(date: String): String {
        return date.split("년 ", "월 ", "일").let { "${it[0]}-${it[1]}-${it[2]}" }
    }

    private fun loadDetailSession(formattedDate: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dateRef =
            FirebaseDatabase.getInstance().getReference("users/$uid/workouts/$formattedDate")

        dateRef.child("sessionInfo").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sessionInfo = snapshot.getValue(SessionInfo::class.java)
                sessionInfo?.let {
                    binding.detailTotalWeight.text =
                        sessionInfo.sessionTotalWeight.toString() + "kg"
                }
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

    private fun loadDetailExerciseData(date: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dateRef =
            FirebaseDatabase.getInstance().getReference("users/$uid/workouts/$date")
        dateRef.child("exercises").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exercises = mutableListOf<ExerciseData>()
                snapshot.children.forEach { childSnapshot ->
                    childSnapshot.getValue(ExerciseData::class.java)?.let { exercises.add(it) }
                }
                binding.detailExerciseCount.text = exercises.size.toString() + "개"
                adapter = DetailExerciseAdapter(exercises)
                detailExerciseView.adapter = adapter
                detailExerciseView.layoutManager = LinearLayoutManager(this@DetailExerciseActivity)
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
}
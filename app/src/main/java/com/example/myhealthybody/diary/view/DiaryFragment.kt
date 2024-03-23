package com.example.myhealthybody.diary.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.databinding.FragmentDiaryBinding
import com.example.myhealthybody.diary.view.adapter.ExerciseDataAdapter
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.util.RecyclerDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.ZoneId

class DiaryFragment : Fragment() {
    private lateinit var mBinding: FragmentDiaryBinding

    private val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
    private lateinit var dailyInformationView: RecyclerView
    private lateinit var calendarView: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDiaryBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendarView = mBinding.calendar
        val defaultDate = today.toString()
        dailyInformationView = mBinding.fragmentTwoRecyclerview

        val adapter = ExerciseDataAdapter(emptyList(), requireContext()) { _, _ ->
            val intent = Intent(requireContext(), DetailExerciseActivity::class.java)
            intent.putExtra("selectedDate", mBinding.setdateTxt.text.toString())
            startActivity(intent)
        }
        dailyInformationView.adapter = adapter
        dailyInformationView.layoutManager = LinearLayoutManager(context)
        dailyInformationView.addItemDecoration(RecyclerDecoration(requireContext()))


        val currentDate = formatDate(defaultDate)
        mBinding.setdateTxt.text = currentDate
        loadExercisesForDate(defaultDate, adapter)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // 날짜 선택 시 이벤트 처리
            val clickedDay =
                "${year}-${String.format("%02d", month + 1)}-${String.format("%02d", dayOfMonth)}"
            mBinding.setdateTxt.text = formatDate(clickedDay)
            loadExercisesForDate(clickedDay, adapter)
        }

        mBinding.exerciseChoiceBtn.setOnClickListener {
            val chooseExerciseActivityIntent = Intent(requireContext(), ChooseExerciseActivity::class.java)
            val selectedDate = mBinding.setdateTxt.text.toString()
            val formattedDate =
                selectedDate.split("년 ", "월 ", "일").let { "${it[0]}-${it[1]}-${it[2]}" }
            chooseExerciseActivityIntent.putExtra("selectedDate", formattedDate)
            startActivity(chooseExerciseActivityIntent)
        }

        mBinding.exerciseEditBtn.setOnClickListener {
            val selectedDate = mBinding.setdateTxt.text.toString()
            val formattedDate =
                selectedDate.split("년 ", "월 ", "일").let { "${it[0]}-${it[1]}-${it[2]}" }
            val intent = Intent(requireContext(), EditExerciseActivity::class.java).apply {
                putExtra("selectedDate", formattedDate)
            }
            startActivity(intent)
        }

        mBinding.exerciseDeleteBtn.setOnClickListener {
            val selectedDate = mBinding.setdateTxt.text.toString()
            val formattedDate =
                selectedDate.split("년 ", "월 ", "일").let { "${it[0]}-${it[1]}-${it[2]}" }
            showDeleteBtnAlert(formattedDate)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun showDeleteBtnAlert(formattedDate: String) {
        AlertDialog.Builder(requireContext()).run {
            setTitle("모든 기록 삭제")
            setMessage("해당 일의 모든 온동과 세트 정보를 삭제합니다.")
            setPositiveButton("확인") { _, _ ->
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setPositiveButton
                // 해당 날짜의 운동 정보 삭제
                FirebaseDatabase.getInstance()
                    .getReference("users/$userId/workouts/$formattedDate").removeValue()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 삭제 성공 시 RecyclerView 업데이트
                            (dailyInformationView.adapter as? ExerciseDataAdapter)?.updateDataSet(
                                emptyList()
                            )
                            dailyInformationView.visibility = View.GONE
                            mBinding.performExerciseTxt.visibility = View.GONE
                            mBinding.exerciseChoiceBtn.visibility = View.VISIBLE
                            mBinding.exerciseDeleteBtn.visibility = View.GONE
                            mBinding.exerciseEditBtn.visibility = View.GONE
                        }
                    }
                Toast.makeText(requireContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("취소", null)
            show()
        }.setCanceledOnTouchOutside(false)
    }

    private fun loadExercisesForDate(date: String, adapter: ExerciseDataAdapter) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase.getInstance().getReference("users/$userId/workouts/$date/exercises")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val exercises = mutableListOf<ExerciseData>()
                    snapshot.children.forEach { childSnapshot ->
                        childSnapshot.getValue(ExerciseData::class.java)?.let { exercises.add(it) }
                    }
                    if (exercises.isNotEmpty()) {
                        adapter.updateDataSet(exercises)
                        dailyInformationView.visibility = View.VISIBLE
                        mBinding.performExerciseTxt.visibility = View.VISIBLE
                        mBinding.exerciseChoiceBtn.visibility = View.GONE
                        mBinding.exerciseDeleteBtn.visibility = View.VISIBLE
                        mBinding.exerciseEditBtn.visibility = View.VISIBLE
                    } else {
                        // 데이터가 없을 경우 처리
                        dailyInformationView.visibility = View.GONE
                        mBinding.performExerciseTxt.visibility = View.GONE
                        mBinding.exerciseChoiceBtn.visibility = View.VISIBLE
                        mBinding.exerciseDeleteBtn.visibility = View.GONE
                        mBinding.exerciseEditBtn.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error fetching data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun formatDate(date: String): String {
        return date.split("-").let { "${it[0]}년 ${it[1]}월 ${it[2]}일" }
    }

    fun refreshData() {
        // 데이터를 새로고침합니다.
        val selectedDate = arguments?.getString("selectedDate") ?: return
        if (selectedDate.isNotEmpty()) {
            loadExercisesForDate(
                selectedDate,
                dailyInformationView.adapter as ExerciseDataAdapter
            )
        }
    }
}
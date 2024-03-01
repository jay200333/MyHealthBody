package com.example.myhealthybody.diaryTab

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.databinding.FragmentTwoBinding
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class FragmentTwo : Fragment() {
    private lateinit var mBinding: FragmentTwoBinding
    private lateinit var calendarView: CalendarView
    private lateinit var dailyInformationView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentTwoBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA)
        calendarView = mBinding.calendar
        calendarView.date = today.timeInMillis
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH)
        val day = today.get(Calendar.DAY_OF_MONTH)
        dailyInformationView = mBinding.fragmentTwoRecyclerview
        mBinding.setdateTxt.text = "${year}년 ${month + 1}월 ${day}일"
        calendarView.setOnDateChangeListener { view, year, month, day ->
            mBinding.setdateTxt.text = "${year}년 ${month + 1}월 ${day}일"
        }

        mBinding.exerciseChoiceBtn.setOnClickListener {
            val chooseExerciseIntent = Intent(requireContext(), ChooseExercise::class.java)
            startActivity(chooseExerciseIntent)
        }
    }
}
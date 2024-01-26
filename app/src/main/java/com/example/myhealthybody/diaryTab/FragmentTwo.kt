package com.example.myhealthybody.diaryTab

import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.ExerciseViewModel
import com.example.myhealthybody.databinding.FragmentTwoBinding

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
        calendarView = mBinding.calendar
        dailyInformationView = mBinding.fragmentTwoRecyclerview
        calendarView.setOnDateChangeListener { view, year, month, day ->
            mBinding.setdateTxt.text = "$year" + "년" + "${month + 1}" + "월" + "$day" + "일"
        }

        mBinding.exerciseChoiceBtn.setOnClickListener {
            val chooseExerciseIntent = Intent(requireContext(), ChooseExercise::class.java)
            startActivity(chooseExerciseIntent)
        }
    }
}
package com.example.myhealthybody

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.example.myhealthybody.databinding.FragmentOneItemBinding

class ViewHolder(private val binding: FragmentOneItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(exercise: ExerciseData, onItemClicked: (ExerciseData) -> Unit) {
        binding.exerciseName.text = exercise.name
        binding.exerciseTarget.text = exercise.target
        val exerciseWebview = binding.exerciseImg
        exerciseWebview.settings.useWideViewPort = true
        exerciseWebview.settings.loadWithOverviewMode = true
        exerciseWebview.loadUrl(exercise.gifUrl)
        itemView.setOnClickListener { onItemClicked(exercise) }
    }

}
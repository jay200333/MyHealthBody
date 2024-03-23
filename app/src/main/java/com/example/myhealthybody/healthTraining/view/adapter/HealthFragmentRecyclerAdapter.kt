package com.example.myhealthybody.healthTraining.view.adapter

import com.bumptech.glide.Glide
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.FragmentHealthTrainingItemBinding

class RecyclerAdapter(
    private var exercises: List<ExerciseData>,
    private val onItemClicked: (ExerciseData) -> Unit
) :
    BaseAdapter<ExerciseData, FragmentHealthTrainingItemBinding>(exercises, FragmentHealthTrainingItemBinding::inflate) {
    private var filteredExercises: List<ExerciseData> = exercises

    fun filterByTarget(target: String) {
        filteredExercises = if (target == "All") {
            exercises
        } else {
            exercises.filter { it.target == target }
        }
        notifyDataSetChanged()
    }

    override fun getViewHolder(binding: FragmentHealthTrainingItemBinding): BaseViewHolder =
        FirstFragmentViewHolder(binding)

    override fun getItemCount(): Int = filteredExercises.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(filteredExercises[position], position)
    }

    inner class FirstFragmentViewHolder(private val binding: FragmentHealthTrainingItemBinding) :
        BaseViewHolder(binding) {
        override fun bind(item: ExerciseData, position: Int) {
            binding.exerciseName.text = item.name
            binding.exerciseTarget.text = item.target

            // gif 이미지를 bitmap 이미지로 변환해서 출력
            val exerciseImgview = binding.exerciseImg
            Glide.with(itemView.context)
                .asBitmap()
                .load(item.gifUrl)
                .into(exerciseImgview)
            itemView.setOnClickListener { onItemClicked(item) }
        }
    }
}

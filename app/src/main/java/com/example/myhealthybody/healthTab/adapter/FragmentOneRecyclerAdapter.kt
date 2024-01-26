package com.example.myhealthybody.healthTab.adapter

import com.bumptech.glide.Glide
import com.example.myhealthybody.ExerciseData
import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.FragmentOneItemBinding

class RecyclerAdapter(
    exercises: List<ExerciseData>,
    private val onItemClicked: (ExerciseData) -> Unit
) :
    BaseAdapter<ExerciseData, FragmentOneItemBinding>(exercises, FragmentOneItemBinding::inflate) {
    override fun getViewHolder(binding: FragmentOneItemBinding): BaseViewHolder =
        FirstFragmentViewHolder(binding)

    inner class FirstFragmentViewHolder(private val binding: FragmentOneItemBinding) :
        BaseViewHolder(binding) {
        override fun bind(item: ExerciseData) {
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

package com.example.myhealthybody.diaryTab.adapter

import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.FragmentTwoRecyclerItemBinding
import com.example.myhealthybody.model.ExerciseData

class ExerciseDataAdapter(
    items: List<ExerciseData>
) : BaseAdapter<ExerciseData, FragmentTwoRecyclerItemBinding>(
    items,
    FragmentTwoRecyclerItemBinding::inflate
) {

    override fun getViewHolder(binding: FragmentTwoRecyclerItemBinding): BaseViewHolder {
        return ExerciseViewHolder(binding)
    }

    inner class ExerciseViewHolder(private val binding: FragmentTwoRecyclerItemBinding) :
        BaseViewHolder(binding) {
        override fun bind(item: ExerciseData, position: Int) {
            with(binding) {
                exerciseCntNumber.text = (position + 1).toString()
                exerciseName.text = item.name
                exerciseTarget.text = item.target + " |"
                exerciseSetCount.text = item.setItems.size.toString() + "세트"
            }
        }
    }
}
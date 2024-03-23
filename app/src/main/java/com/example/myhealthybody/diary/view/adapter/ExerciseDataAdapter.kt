package com.example.myhealthybody.diary.view.adapter

import android.content.Context
import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.FragmentDiaryRecyclerItemBinding
import com.example.myhealthybody.model.ExerciseData

class ExerciseDataAdapter(
    items: List<ExerciseData>,
    private val context: Context,
    private val onExerciseClicked: (ExerciseData, String) -> Unit
) : BaseAdapter<ExerciseData, FragmentDiaryRecyclerItemBinding>(
    items,
    FragmentDiaryRecyclerItemBinding::inflate
) {

    override fun getViewHolder(binding: FragmentDiaryRecyclerItemBinding): BaseViewHolder {
        return ExerciseViewHolder(binding)
    }

    inner class ExerciseViewHolder(private val binding: FragmentDiaryRecyclerItemBinding) :
        BaseViewHolder(binding) {
        override fun bind(item: ExerciseData, position: Int) {
            with(binding) {
                exerciseCntNumber.text = (position + 1).toString()
                exerciseName.text = item.name
                exerciseTarget.text = item.target + " |"
                exerciseSetCount.text = item.setItems.size.toString() + "세트"
                root.setOnClickListener {
                    onExerciseClicked(item, bindingAdapterPosition.toString())
                }
            }
        }
    }
}
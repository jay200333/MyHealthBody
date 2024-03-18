package com.example.myhealthybody.diaryTab.adapter

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.FragmentTwoRecyclerItemBinding
import com.example.myhealthybody.diaryTab.SelectSetActivity
import com.example.myhealthybody.model.ExerciseData
import java.io.Serializable

class ExerciseDataAdapter(
    items: List<ExerciseData>,
    private val context: Context,
    private val onExerciseClicked: (ExerciseData, String) -> Unit
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
                root.setOnClickListener {
                    onExerciseClicked(item, bindingAdapterPosition.toString())
                }
            }
        }
    }
}
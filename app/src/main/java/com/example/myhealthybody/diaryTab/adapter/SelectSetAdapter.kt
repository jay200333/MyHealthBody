package com.example.myhealthybody.diaryTab.adapter

import android.content.Intent
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.ExerciseViewModel
import com.example.myhealthybody.model.SetItem
import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.ExerciseSetItemBinding
import com.example.myhealthybody.healthTab.FragmentOneItemActivity
import com.example.myhealthybody.model.EditData
import com.example.myhealthybody.model.EditTextLine
import com.example.myhealthybody.model.SelectSetData

class SelectSetAdapter(
    var exercises: MutableList<SelectSetData>,
    private val viewModel: ExerciseViewModel
) :
    BaseAdapter<SelectSetData, ExerciseSetItemBinding>(
        exercises,
        ExerciseSetItemBinding::inflate
    ) {
    override fun getViewHolder(binding: ExerciseSetItemBinding): BaseViewHolder =
        SelectSetViewHolder(binding)

    inner class SelectSetViewHolder(private val binding: ExerciseSetItemBinding) :
        BaseViewHolder(binding) {

        init {
            binding.layoutContainer.removeAllViews()
            binding.infoExerciseImg.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = exercises[position]
                    val intent = Intent(itemView.context, FragmentOneItemActivity::class.java)
                    putData(intent, clickedItem)
                    itemView.context.startActivity(intent)
                }
            }
        }

        override fun bind(item: SelectSetData) {
            binding.exerciseName.text = item.selectData.name
            binding.exerciseTarget.text = item.selectData.target + " | "


        }
    }

    private fun putData(intent: Intent, exercise: SelectSetData) {
        intent.putExtra("itemExerciseName", exercise.selectData.name)
        intent.putExtra("itemExerciseTarget", exercise.selectData.target)
        intent.putExtra("itemExerciseId", exercise.selectData.id)
        intent.putExtra("itemExerciseGif", exercise.selectData.gifUrl)
        intent.putExtra("itemExerciseEquipment", exercise.selectData.equipment)
        intent.putStringArrayListExtra(
            "itemExerciseInstruction",
            ArrayList(exercise.selectData.instructions)
        )
    }
}
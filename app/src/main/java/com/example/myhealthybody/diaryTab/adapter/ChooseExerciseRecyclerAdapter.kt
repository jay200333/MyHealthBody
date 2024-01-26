package com.example.myhealthybody.diaryTab.adapter

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myhealthybody.ExerciseData
import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.ChooseExerciseItemBinding
import com.example.myhealthybody.healthTab.FragmentOneItemActivity

interface OnCheckboxChangeCallback {
    fun onCheckedChange(isChecked: Boolean, position: Int)
}

class ChooseExerciseRecyclerAdapter(
    val exercises: List<ExerciseData>,
    private val checkboxChangeCallback: OnCheckboxChangeCallback
) :
    BaseAdapter<ExerciseData, ChooseExerciseItemBinding>(
        exercises,
        ChooseExerciseItemBinding::inflate
    ) {
    private val checkedItems = mutableSetOf<Int>()
    override fun getViewHolder(binding: ChooseExerciseItemBinding): BaseViewHolder =
        ChooseExerciseViewHolder(binding)

    inner class ChooseExerciseViewHolder(private val binding: ChooseExerciseItemBinding) :
        BaseViewHolder(binding) {
        init {
            binding.checkboxExercise.setOnCheckedChangeListener { _, isChecked ->
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    if (isChecked) {
                        checkedItems.add(position)
                    } else {
                        checkedItems.remove(position)
                    }
                    checkboxChangeCallback.onCheckedChange(isChecked, checkedItems.size)
                }
            }
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

        override fun bind(item: ExerciseData) {
            binding.checkboxExercise.isChecked = checkedItems.contains(absoluteAdapterPosition)
            binding.exerciseName.text = item.name
            val exerciseImg = binding.exerciseImg
            Glide.with(itemView.context)
                .asBitmap()
                .load(item.gifUrl)
                .into(exerciseImg)
        }
    }

    private fun putData(intent: Intent, exercise: ExerciseData) {
        intent.putExtra("itemExerciseName", exercise.name)
        intent.putExtra("itemExerciseTarget", exercise.target)
        intent.putExtra("itemExerciseId", exercise.id)
        intent.putExtra("itemExerciseGif", exercise.gifUrl)
        intent.putExtra("itemExerciseEquipment", exercise.equipment)
        intent.putStringArrayListExtra(
            "itemExerciseInstruction",
            ArrayList(exercise.instructions)
        )
    }
}
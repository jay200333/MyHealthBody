package com.example.myhealthybody.diaryTab.adapter

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.model.ExerciseViewModel
import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.ChooseExerciseItemBinding
import com.example.myhealthybody.healthTab.FragmentOneItemActivity

interface OnCheckboxChangeCallback {
    fun onCheckedChange(isChecked: Boolean, exerciseData: ExerciseData)
}

class ChooseExerciseRecyclerAdapter(
    val exercises: List<ExerciseData>,
    private val viewModel: ExerciseViewModel,
    private val checkboxChangeCallback: OnCheckboxChangeCallback
) :
    BaseAdapter<ExerciseData, ChooseExerciseItemBinding>(
        exercises,
        ChooseExerciseItemBinding::inflate
    ) {
    private var filteredExercises: List<ExerciseData> = exercises
    private val checkedItems = mutableSetOf<String>()

    fun filterByTarget(target: String) {
        filteredExercises = if (target == "All") {
            exercises
        } else {
            exercises.filter { it.target == target }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = filteredExercises.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(filteredExercises[position])
    }

    override fun getViewHolder(binding: ChooseExerciseItemBinding): BaseViewHolder =
        ChooseExerciseViewHolder(binding)

    inner class ChooseExerciseViewHolder(private val binding: ChooseExerciseItemBinding) :
        BaseViewHolder(binding) {
        override fun bind(item: ExerciseData) {
            binding.checkboxExercise.setOnCheckedChangeListener(null)
            binding.checkboxExercise.isChecked = checkedItems.contains(item.id)

            binding.checkboxExercise.setOnCheckedChangeListener { _, isChecked ->
                //val exercise = filteredExercises[adapterPosition]
                if (isChecked) {
                    checkedItems.add(item.id)
                } else {
                    checkedItems.remove(item.id)
                }
                checkboxChangeCallback.onCheckedChange(isChecked, item)
                viewModel.setCheckedExercises(checkedItems.mapNotNull { id -> exercises.find { it.id == id } })
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
            binding.checkboxExercise.isChecked = checkedItems.contains(item.id)
            binding.exerciseName.text = item.name
            val exerciseImg = binding.exerciseImg
            Glide.with(itemView.context)
                .asBitmap()
                .load(item.gifUrl)
                .into(exerciseImg)
        }
    }

    fun uncheckExercise(exerciseId: String) {
        val index = exercises.indexOfFirst { it.id == exerciseId }
        if (index != -1) {
            checkedItems.remove(exerciseId)
            notifyItemChanged(index)
            viewModel.setCheckedExercises(checkedItems.mapNotNull { id -> exercises.find { it.id == id } })
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
package com.example.myhealthybody.diary.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.model.ExerciseViewModel
import com.example.myhealthybody.R
import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.ExerciseSetItemBinding
import com.example.myhealthybody.databinding.SetItemOneLineBinding
import com.example.myhealthybody.healthTraining.view.HealthItemClickedActivity
import com.example.myhealthybody.model.EditTextLine
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.model.SelectSetData
import com.example.myhealthybody.model.SetItem

interface TotalWeightUpdateListener {
    fun onTotalWeightUpdated(totalWeight: Int)
}

class SelectSetAdapter(
    var exercises: MutableList<SelectSetData>,
    private val viewModel: ExerciseViewModel,
    private val totalWeightUpdateListener: TotalWeightUpdateListener
) :
    BaseAdapter<SelectSetData, ExerciseSetItemBinding>(
        exercises,
        ExerciseSetItemBinding::inflate
    ) {
    private var totalWeightSum = 0
    override fun getViewHolder(binding: ExerciseSetItemBinding): BaseViewHolder {
        return SelectSetViewHolder(binding)
    }

    inner class SelectSetViewHolder(private val binding: ExerciseSetItemBinding) :
        BaseViewHolder(binding) {

        private var setCount = 1 // 초기 세트 수

        init {
            binding.layoutContainer.removeAllViews()
            binding.setAddBtn.setOnClickListener {
                addSetView()
            }
            binding.setDeleteBtn.setOnClickListener {
                removeLastSetView()
            }
            binding.infoExerciseImg.setOnClickListener {
                val position = absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = exercises[position]
                    val intent = Intent(itemView.context, HealthItemClickedActivity::class.java)
                    putData(intent, clickedItem)
                    itemView.context.startActivity(intent)
                }
            }
        }

        private fun addSetView() {
            val setViewBinding = SetItemOneLineBinding.inflate(
                LayoutInflater.from(binding.root.context),
                binding.layoutContainer,
                false
            )

            // 새로운 세트 추가 시 기본값을 0으로 설정
            val defaultEditTextLine = EditTextLine("0", "0")
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val item = exercises[position]
                item.editData.editTextLines.add(defaultEditTextLine)
            }
            setViewBinding.setCnt.text = (binding.layoutContainer.childCount + 1).toString()
            setViewBinding.weight.addTextChangedListener { editable ->
                val weight = editable.toString()
                updateSetData(setViewBinding, weight, setViewBinding.count.text.toString())
                updateTotalWeight()
            }
            setViewBinding.count.addTextChangedListener { editable ->
                val count = editable.toString()
                updateSetData(setViewBinding, setViewBinding.weight.text.toString(), count)
                updateTotalWeight()
            }
            binding.layoutContainer.addView(setViewBinding.root)
        }

        private fun updateSetData(binding: SetItemOneLineBinding, weight: String, count: String) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val item = exercises[position]
                // 세트 뷰의 인덱스를 기반으로 해당 세트 데이터를 업데이트합니다.
                val setIndex = binding.root.parent.let { parent ->
                    if (parent is ViewGroup) parent.indexOfChild(binding.root) else -1
                }
                // 새로운 세트를 추가하거나 기존 세트를 업데이트
                if (setIndex != -1 && setIndex < item.editData.editTextLines.size) {
                    // 기존 세트 데이터를 업데이트
                    item.editData.editTextLines[setIndex] = EditTextLine(weight, count)
                } else {
                    // 새 세트를 추가
                    item.editData.editTextLines.add(EditTextLine(weight, count))
                }
                updateTotalWeightSum()
            }
        }

        private fun removeLastSetView() {
            val childCount = binding.layoutContainer.childCount
            if (childCount > 0) {
                //binding.layoutContainer.removeViewAt(childCount - 1)
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = exercises[position]
                    if (childCount <= item.editData.editTextLines.size) {
                        item.editData.editTextLines.removeAt(childCount - 1)
                    }
                    binding.layoutContainer.removeViewAt(childCount - 1)
                }
                updateTotalWeight()
                updateTotalWeightSum()
            }
        }

        private fun updateTotalWeightSum() {
            totalWeightSum = 0
            for (exercise in exercises) {
                for (line in exercise.editData.editTextLines) {
                    val weight = line.kg.toIntOrNull() ?: 0
                    val count = line.count.toIntOrNull() ?: 0
                    totalWeightSum += weight * count
                }
            }
            totalWeightUpdateListener.onTotalWeightUpdated(totalWeightSum)
        }

        private fun updateTotalWeight() {
            var totalWeight = 0
            for (i in 0 until binding.layoutContainer.childCount) {
                val setView = binding.layoutContainer.getChildAt(i) as ViewGroup
                val weightEditText = setView.findViewById<EditText>(R.id.weight)
                val weight = weightEditText.text.toString().toIntOrNull() ?: 0
                val countEditText = setView.findViewById<EditText>(R.id.count)
                val count = countEditText.text.toString().toIntOrNull() ?: 0
                totalWeight += (weight * count)
            }
            binding.totalWeight.text = "총 볼륨 " + totalWeight.toString() + "kg"
        }

        override fun bind(item: SelectSetData, position: Int) {
            binding.exerciseName.text = item.selectData.name
            binding.exerciseTarget.text = item.selectData.target + " | "
            binding.layoutContainer.removeAllViews()
            addSetView()
        }
    }

    fun getAllExerciseData(): List<ExerciseData> {
        return exercises.map { selectedSetData ->
            val setItems =
                selectedSetData.editData.editTextLines.mapIndexed { index, editTextLine ->
                    SetItem(
                        setCount = index + 1,
                        weight = editTextLine.kg.toIntOrNull() ?: 0,
                        tryCount = editTextLine.count.toIntOrNull() ?: 0
                    )
                }.toMutableList()
            // 세트 항목의 총 무계를 계산
            val totalWeight = setItems.sumOf { it.weight * it.tryCount }
            selectedSetData.selectData.copy(setItems = setItems, setTotalWeight = totalWeight)
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
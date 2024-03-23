package com.example.myhealthybody.diary.view.adapter

import android.app.AlertDialog
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.R
import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.ExerciseSetItemBinding
import com.example.myhealthybody.databinding.SetItemOneLineBinding
import com.example.myhealthybody.healthTraining.view.HealthItemClickedActivity
import com.example.myhealthybody.model.ExerciseData
import com.example.myhealthybody.model.SessionInfo
import com.example.myhealthybody.model.SetItem

class EditExerciseAdapter(
    private val exercises: MutableList<ExerciseData>,
    sessionInfo: SessionInfo?,
    private val totalWeightUpdateListener: TotalWeightUpdateListener
) : BaseAdapter<ExerciseData, ExerciseSetItemBinding>(
    exercises, ExerciseSetItemBinding::inflate
) {
    fun getAllExerciseData() = exercises
    override fun getViewHolder(binding: ExerciseSetItemBinding): BaseViewHolder {
        return EditSetViewHolder(binding)
    }

    inner class EditSetViewHolder(private val binding: ExerciseSetItemBinding) :
        BaseViewHolder(binding) {


        init {
            binding.setAddBtn.setOnClickListener {
                addSetView(0, 0)
            }
            binding.setDeleteBtn.setOnClickListener {
                removeLastSetView()
            }
            binding.layoutContainer.removeAllViews()
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

        private fun addSetView(weight: Int = 0, reps: Int = 0) {
            val inflater = LayoutInflater.from(binding.root.context)
            val setBinding = SetItemOneLineBinding.inflate(
                inflater,
                binding.layoutContainer,
                false
            )

            if (weight == 0) {
                setBinding.weight.hint = "0"
            } else {
                setBinding.weight.setText(weight.toString())
            }
            if (reps == 0) {
                setBinding.count.hint = "0"
            } else {
                setBinding.count.setText(reps.toString())
            }

            setBinding.setCnt.text = (binding.layoutContainer.childCount + 1).toString()

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    updateExerciseDataWithSetItems()
                    updateTotalWeight()
                    updateTotalWeightSum()
                }
            }
            setBinding.weight.addTextChangedListener(textWatcher)
            setBinding.count.addTextChangedListener(textWatcher)
            binding.layoutContainer.addView(setBinding.root)
            updateExerciseDataWithSetItems()
            updateTotalWeight()
            updateTotalWeightSum()
        }

        private fun updateExerciseDataWithSetItems() {
            val updatedSetItems = mutableListOf<SetItem>()
            for (i in 0 until binding.layoutContainer.childCount) {
                val setView = binding.layoutContainer.getChildAt(i) as ViewGroup
                val weight = setView.findViewById<EditText>(R.id.weight).text.toString().toIntOrNull() ?: 0
                val reps = setView.findViewById<EditText>(R.id.count).text.toString().toIntOrNull() ?: 0
                updatedSetItems.add(SetItem(setCount = i + 1, weight = weight, tryCount = reps))
            }
            // 현재 ExerciseData의 setItems 업데이트
            if (adapterPosition != RecyclerView.NO_POSITION) {
                exercises[adapterPosition].setItems = updatedSetItems
                totalWeightUpdateListener.onTotalWeightUpdated(updatedSetItems.sumOf { it.weight * it.tryCount })
            }
        }

        private fun updateTotalWeight() {
            var totalWeight = 0
            for (i in 0 until binding.layoutContainer.childCount) {
                val setView = binding.layoutContainer.getChildAt(i) as ViewGroup
                val weight = setView.findViewById<EditText>(R.id.weight).text.toString().toIntOrNull() ?: 0
                val reps = setView.findViewById<EditText>(R.id.count).text.toString().toIntOrNull() ?: 0
                totalWeight += weight * reps
            }
            // 이곳에서 총 무게를 UI에 업데이트
            binding.totalWeight.text = "총 볼륨: $totalWeight" + "kg"
            exercises[adapterPosition].setTotalWeight = totalWeight
        }

        private fun removeLastSetView() {
            val childCount = binding.layoutContainer.childCount
            if (childCount > 0) {
                if (childCount == 1) {
                    alertRemoveExerciseData()
                } else {
                    binding.layoutContainer.removeViewAt(childCount - 1)
                    updateSetItemsAfterRemoving()
                }
            }
        }

        private fun alertRemoveExerciseData() {
            AlertDialog.Builder(itemView.context).apply {
                setTitle("세트 삭제")
                setMessage("마지막 세트를 삭제하면 이 운동이 목록에서 제거됩니다. 삭제하시겠습니까?")
                setPositiveButton("확인") { dialog, _ ->
                    // 사용자가 확인을 클릭했을 때 세트와 운동 정보 삭제
                    binding.layoutContainer.removeViewAt(0)
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        exercises.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                        updateTotalWeightSum()
                    }
                    dialog.dismiss()
                }
                setNegativeButton("취소") { dialog, _ ->
                    // 취소 시 아무것도 하지 않음
                    dialog.dismiss()
                }
                show()
            }
        }

        private fun updateSetItemsAfterRemoving() {
            val updatedSetItems = mutableListOf<SetItem>()
            for (i in 0 until binding.layoutContainer.childCount) {
                val setView = binding.layoutContainer.getChildAt(i) as ViewGroup
                val weight =
                    setView.findViewById<EditText>(R.id.weight).text.toString().toIntOrNull() ?: 0
                val reps =
                    setView.findViewById<EditText>(R.id.count).text.toString().toIntOrNull() ?: 0
                updatedSetItems.add(SetItem(setCount = i + 1, weight = weight, tryCount = reps))
            }

            if (adapterPosition != RecyclerView.NO_POSITION) {
                exercises[adapterPosition].setItems = updatedSetItems
                updateTotalWeight()
                updateTotalWeightSum()
            }
        }

        private fun updateTotalWeightSum() {
            var totalWeightSum = 0
            exercises.forEach { exercise ->
                exercise.setItems.forEach { set ->
                    totalWeightSum += set.weight * set.tryCount
                }
            }
            totalWeightUpdateListener.onTotalWeightUpdated(totalWeightSum)
        }

        override fun bind(item: ExerciseData, position: Int) {
            with(binding) {
                exerciseTarget.text = item.target + " | "
                exerciseName.text = item.name
                totalWeight.text = "총 볼륨: " + item.setTotalWeight.toString() + "kg"

                layoutContainer.removeAllViews()

                item.setItems?.forEach { setItem ->
                    addSetView(setItem.weight, setItem.tryCount)
                }
            }
        }

        private fun putData(intent: Intent, exercise: ExerciseData) {
            intent.putExtra("itemExerciseName", exercise.name)
            intent.putExtra("itemExerciseTarget", exercise.target)
            intent.putExtra("itemExerciseId", exercise.id)
            intent.putExtra("itemExerciseEquipment", exercise.equipment)
            intent.putStringArrayListExtra(
                "itemExerciseInstruction",
                ArrayList(exercise.instructions)
            )
        }
    }
}
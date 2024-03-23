package com.example.myhealthybody.diary.view.adapter

import android.view.LayoutInflater
import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.DetailExerciseItemBinding
import com.example.myhealthybody.databinding.DetailExerciseOneLineBinding
import com.example.myhealthybody.model.ExerciseData

class DetailExerciseAdapter(items: List<ExerciseData>) :
    BaseAdapter<ExerciseData, DetailExerciseItemBinding>(
        items,
        DetailExerciseItemBinding::inflate
    ) {
    override fun getViewHolder(binding: DetailExerciseItemBinding): BaseViewHolder {
        return DetailExerciseViewHolder(binding)
    }

    inner class DetailExerciseViewHolder(private val binding: DetailExerciseItemBinding) :
        BaseViewHolder(binding) {
        override fun bind(item: ExerciseData, position: Int) {
            with(binding) {
                detailExerciseNumber.text = (position + 1).toString()
                detailExerciseName.text = item.name
                detailExerciseTarget.text = item.target + "   |"
                detailSetTotalWeight.text = item.setTotalWeight.toString() + "kg"

                detailLayoutContainer.removeAllViews()

                item.setItems?.forEach { setItem ->
                    val setItemBinding = DetailExerciseOneLineBinding.inflate(
                        LayoutInflater.from(binding.root.context),
                        binding.detailLayoutContainer,
                        false
                    )

                    setItemBinding.detailOneLineWeight.text = "${setItem.weight}" + "kg"
                    setItemBinding.detailOneLineCount.text = "${setItem.tryCount}" + "회"
                    setItemBinding.detailSetCnt.text = "${setItem.setCount}"

                    detailLayoutContainer.addView(setItemBinding.root)

                }
            }
        }
    }
}
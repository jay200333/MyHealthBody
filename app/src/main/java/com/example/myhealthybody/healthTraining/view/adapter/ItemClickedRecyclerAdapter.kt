package com.example.myhealthybody.healthTraining.view.adapter

import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.ActivityHealthfragmentInstructionItemBinding

class ItemClickedRecyclerAdapter(instruction: List<String>) :
    BaseAdapter<String, ActivityHealthfragmentInstructionItemBinding>(
        instruction,
        ActivityHealthfragmentInstructionItemBinding::inflate
    ) {
    override fun getViewHolder(binding: ActivityHealthfragmentInstructionItemBinding): BaseViewHolder =
        FragmentOneClickedViewHolder(binding)

    inner class FragmentOneClickedViewHolder(private val binding: ActivityHealthfragmentInstructionItemBinding) :
        BaseViewHolder(binding) {
        override fun bind(item: String, position: Int) {
            binding.instructionOrderNumber.text = (position + 1).toString()
            binding.instructionTxt.text = item
        }
    }
}
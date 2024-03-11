package com.example.myhealthybody.healthTab.adapter

import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.ActivityFragmentClickedInstructionBinding

class FragmentOneClickedRecyclerAdapter(instruction: List<String>) :
    BaseAdapter<String, ActivityFragmentClickedInstructionBinding>(
        instruction,
        ActivityFragmentClickedInstructionBinding::inflate
    ) {
    override fun getViewHolder(binding: ActivityFragmentClickedInstructionBinding): BaseViewHolder =
        FragmentOneClickedViewHolder(binding)

    inner class FragmentOneClickedViewHolder(private val binding: ActivityFragmentClickedInstructionBinding) :
        BaseViewHolder(binding) {
        override fun bind(item: String, position: Int) {
            binding.instructionTxt.text = item
        }
    }
}
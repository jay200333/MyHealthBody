package com.example.myhealthybody

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myhealthybody.databinding.FragmentOneItemBinding

class RecyclerAdapter(
    private val exercises: List<ExerciseData>,
    private val onItemClicked: (ExerciseData) -> Unit
) :
    RecyclerView.Adapter<ViewHolder>() {
    private lateinit var mBinding: FragmentOneItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mBinding =
            FragmentOneItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.bind(exercise, onItemClicked)
    }

    override fun getItemCount(): Int = exercises.size
}

package com.example.myhealthybody.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T, VB : ViewBinding>(
    private var items: List<T>,
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB
) : RecyclerView.Adapter<BaseAdapter<T, VB>.BaseViewHolder>() {

    abstract inner class BaseViewHolder(private val binding: VB) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(item: T, position: Int)
    }

    fun updateDataSet(newItems: List<T>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = bindingInflater.invoke(inflater, parent, false)
        return getViewHolder(binding)
    }

    abstract fun getViewHolder(binding: VB): BaseViewHolder

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(items[position], position)
    }
}
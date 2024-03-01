package com.example.myhealthybody.pictureTab.adapter

import com.bumptech.glide.Glide
import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.PictureItemBinding
import com.example.myhealthybody.mainView.MyApplication
import com.example.myhealthybody.model.PictureData
import com.example.myhealthybody.model.PictureViewModel

class PictureRecyclerAdapter(
    pictureInfo: List<PictureData>
) : BaseAdapter<PictureData, PictureItemBinding>(pictureInfo, PictureItemBinding::inflate) {
    override fun getViewHolder(binding: PictureItemBinding): BaseViewHolder =
        ThirdFragmentViewHolder(binding)

    inner class ThirdFragmentViewHolder(private val binding: PictureItemBinding) :
        BaseViewHolder(binding) {
        override fun bind(item: PictureData) {
            binding.apply {
                pictureItemEmailView.text = item.email
                pictureItemDateView.text = item.date
                pictureItemContentView.text = item.content
            }

            val imgRef = MyApplication.storage.reference.child("images/${item.docId}.jpg")
            imgRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Glide.with(itemView.context).load(task.result)
                        .into(binding.pictureItemImageView)
                }
            }
        }
    }
}
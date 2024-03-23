package com.example.myhealthybody.gallery.view.adapter

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import com.bumptech.glide.Glide
import com.example.myhealthybody.adapter.BaseAdapter
import com.example.myhealthybody.databinding.PictureImageZoomBinding
import com.example.myhealthybody.databinding.PictureItemBinding
import com.example.myhealthybody.mainView.MyApplication
import com.example.myhealthybody.gallery.model.PictureData

class PictureRecyclerAdapter(
    var pictureInfo: MutableList<PictureData>
) : BaseAdapter<PictureData, PictureItemBinding>(pictureInfo, PictureItemBinding::inflate) {
    override fun getViewHolder(binding: PictureItemBinding): BaseViewHolder =
        ThirdFragmentViewHolder(binding)

    fun addItem(newItem: PictureData) {
        pictureInfo.add(newItem)
        notifyItemInserted(pictureInfo.size - 1)
    }

    fun updateItems(newItems: MutableList<PictureData>) {
        this.pictureInfo.clear()
        this.pictureInfo.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ThirdFragmentViewHolder(private val binding: PictureItemBinding) :
        BaseViewHolder(binding) {
        override fun bind(item: PictureData, position: Int) {
            binding.apply {
                pictureItemEmailView.text = item.email
                pictureItemDateView.text = item.date
                pictureItemContentView.text = item.content
            }

            binding.pictureItemImageView.setOnClickListener {
                val dialog = Dialog(it.context)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                val zoomedImgViewBinding =
                    PictureImageZoomBinding.inflate(LayoutInflater.from(it.context))
                dialog.setContentView(zoomedImgViewBinding.root)

                Glide.with(it.context).load(item.imageUrl)
                    .into(zoomedImgViewBinding.zoomedImageView)
                dialog.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // 다이얼로그 배경을 검정색으로 설정
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

                // 확대된 이미지 클릭 시 다이얼로그 닫기
                zoomedImgViewBinding.zoomedImageView.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
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
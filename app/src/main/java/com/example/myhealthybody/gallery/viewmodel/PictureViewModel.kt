package com.example.myhealthybody.gallery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myhealthybody.gallery.model.PictureData

class PictureViewModel : ViewModel() {
    private val _pictures = MutableLiveData<PictureData>()
    val pictures: LiveData<PictureData> = _pictures

    fun updatePicture(pictureData: PictureData) {
        _pictures.value = pictureData
    }
}
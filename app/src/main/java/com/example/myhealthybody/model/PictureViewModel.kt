package com.example.myhealthybody.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myhealthybody.mainView.MyApplication
import com.google.firebase.firestore.Query

class PictureViewModel : ViewModel() {
    private val _pictures = MutableLiveData<List<PictureData>>()
    val pictures: LiveData<List<PictureData>> = _pictures

    fun fetchPictures() {
        MyApplication.db.collection("news").orderBy("date", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { result ->
                val itemList = result.toObjects(PictureData::class.java)
                _pictures.value = itemList
            }
    }
}
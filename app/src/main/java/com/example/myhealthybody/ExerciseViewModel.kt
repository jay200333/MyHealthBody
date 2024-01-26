package com.example.myhealthybody

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExerciseViewModel : ViewModel() {
    private val _exercisesData = MutableLiveData<List<ExerciseData>>()
    val exerciseData: LiveData<List<ExerciseData>> = _exercisesData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadExercises() {
        RetrofitInstance.api.getExercises().enqueue(object : Callback<List<ExerciseData>> {
            override fun onResponse(
                call: Call<List<ExerciseData>>,
                response: Response<List<ExerciseData>>
            ) {
                if (response.isSuccessful) {
                    _exercisesData.postValue(response.body())
                    Log.d("kim", "데이터를 불러오는데 성공했습니다.")
                } else {
                    _error.postValue("Response not successful")
                }
            }

            override fun onFailure(call: Call<List<ExerciseData>>, t: Throwable) {
                _error.postValue(t.message ?: "An unknown error occurred")
            }
        })
    }
}
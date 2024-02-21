package com.example.myhealthybody

import android.util.Log
import com.example.myhealthybody.model.ExerciseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExerciseRepository {
    fun loadExercises(callback: (List<ExerciseData>?, Throwable?) -> Unit) {
        RetrofitInstance.api.getExercises().enqueue(object : Callback<List<ExerciseData>> {
            override fun onResponse(
                call: Call<List<ExerciseData>>,
                response: Response<List<ExerciseData>>
            ) {
                if (response.isSuccessful) {
                    // 성공적으로 데이터를 받아온 경우
                    callback(response.body(), null)
                    Log.d("kim", "${response.body()?.size}")
                } else {
                    // 오류 응답 처리
                    callback(null, RuntimeException("Response not successful"))

                }
            }

            override fun onFailure(call: Call<List<ExerciseData>>, t: Throwable) {
                // 네트워크 오류 등의 실패 처리
                callback(null, t)
            }
        })
    }
}


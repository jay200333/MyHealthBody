package com.example.myhealthybody

import com.example.myhealthybody.model.ExerciseData
import retrofit2.http.GET
import retrofit2.Call

interface ExerciseDBService {
    @GET("exercises?limit=500")
    fun getExercises(): Call<List<ExerciseData>>
}
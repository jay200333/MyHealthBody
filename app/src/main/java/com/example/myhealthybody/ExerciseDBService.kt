package com.example.myhealthybody

import retrofit2.http.GET
import retrofit2.Call

interface ExerciseDBService {
    @GET("exercises?limit=100")
    fun getExercises(): Call<List<ExerciseData>>
}
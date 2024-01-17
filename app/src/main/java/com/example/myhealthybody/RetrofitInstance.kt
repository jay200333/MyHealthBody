package com.example.myhealthybody

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: ExerciseDBService by lazy {
        val okHttpClient = OkHttpClient.Builder().addInterceptor(ApiKeyInterceptor()).build()
        Retrofit.Builder()
            .baseUrl("https://exercisedb.p.rapidapi.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExerciseDBService::class.java)
    }
}
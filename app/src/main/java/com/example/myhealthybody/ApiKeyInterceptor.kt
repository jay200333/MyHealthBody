package com.example.myhealthybody

import androidx.multidex.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .addHeader("X-RapidAPI-Key", com.example.myhealthybody.BuildConfig.EXERCISE_DB_API_KEY)
            .build()
        return chain.proceed(newRequest)
    }
}
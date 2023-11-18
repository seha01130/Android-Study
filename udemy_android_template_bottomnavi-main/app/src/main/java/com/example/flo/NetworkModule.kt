package com.example.flo

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://edu-api-test.softsquared.com"

fun getRetrofit(): Retrofit {

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)  //괄호안에는 서버가 준 URL을 주면 됨
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit
}
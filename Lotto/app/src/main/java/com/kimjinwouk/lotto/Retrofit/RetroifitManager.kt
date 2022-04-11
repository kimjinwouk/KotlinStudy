package com.kimjinwouk.lotto.Retrofit.Interface

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroifitManager{
    private const val BASE_URL = "https://www.dhlottery.co.kr/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service:RetrofitService = retrofit.create(RetrofitService::class.java)
}
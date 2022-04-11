package com.kimjinwouk.lotto.Retrofit.Interface

import com.kimjinwouk.lotto.DTO.LottoNumber
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object RetroifitManager{
    private const val BASE_URL = "https://www.dhlottery.co.kr/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service:RetrofitService = retrofit.create(RetrofitService::class.java)
}
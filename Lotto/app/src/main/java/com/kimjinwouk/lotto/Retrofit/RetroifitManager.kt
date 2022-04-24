package com.kimjinwouk.lotto.Retrofit.Interface

import com.kimjinwouk.lotto.Kakao.KakaoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroifitManager{

    const val  KAKAO_BASE_URL = "https://dapi.kakao.com/"
    const val  API_KEY = "KakaoAK 4cfb1404af0f374bd5c3a2cc6dfc9cf6"

    private const val BASE_URL = "https://www.dhlottery.co.kr/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val KakaoRetorfit: Retrofit = Retrofit.Builder()
        .baseUrl(KAKAO_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val KakaoService: RetrofitService = KakaoRetorfit.create(RetrofitService::class.java)

    val service:RetrofitService = retrofit.create(RetrofitService::class.java)





}
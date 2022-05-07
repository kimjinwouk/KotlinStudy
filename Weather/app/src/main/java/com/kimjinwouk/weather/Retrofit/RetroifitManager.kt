package com.kimjinwouk.lotto.Retrofit.Interface

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetroifitManager{

    const val  BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"
    const val  API_KEY = "bYqLxONLsSDNETO0Zo7oupbn2HhwxsCJS9wVlYW3f9wjeMI3b1mhjqbPM88OPXvZfuZFD1pprktuATj/vF9SBQ=="



    private fun httpLoggingInterceptor(): HttpLoggingInterceptor? {
        val interceptor = HttpLoggingInterceptor { message ->
            Log.e(
                "MyGitHubData :",
                message + ""
            )
        }
        return interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    }


    var client = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor()).build()


    var gson = GsonBuilder().setLenient().create()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()


    val service:RetrofitService = retrofit.create(RetrofitService::class.java)



    const val  KAKAO_BASE_URL = "https://dapi.kakao.com/"
    const val  KAKA_API_KEY = "KakaoAK dac724e18a1e1eed5ccb01e96c380123"

    private val KakaoRetorfit: Retrofit = Retrofit.Builder()
        .baseUrl(KAKAO_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    val KakaoService: RetrofitService = KakaoRetorfit.create(RetrofitService::class.java)



}
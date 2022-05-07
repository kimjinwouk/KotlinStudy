package com.kimjinwouk.lotto.Retrofit.Interface


import com.kimjinwouk.weather.Data.Kakao.KakaoData
import com.kimjinwouk.weather.Data.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetrofitService{

    @GET("getUltraSrtFcst")
    fun getWeather(
        @Query("serviceKey") serviceKey : String,
        @Query("dataType") dataType : String,
        @Query("base_date") base_date : String,
        @Query("base_time") base_time : String,
        @Query("nx") nx : String,
        @Query("ny") ny : String,
        @Query("numOfRows") numOfRows : String,
    ): Call<WeatherData>



    @GET("v2/local/geo/coord2regioncode.json")
    fun getKakaoAddress(
        @Header("Authorization") key : String,
        @Query("x") x : Double,
        @Query("y") y : Double
    ): Call<KakaoData>



}
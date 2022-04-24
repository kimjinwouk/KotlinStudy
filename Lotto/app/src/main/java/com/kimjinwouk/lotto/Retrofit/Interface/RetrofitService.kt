package com.kimjinwouk.lotto.Retrofit.Interface

import com.kimjinwouk.lotto.DTO.LottoNumber
import com.kimjinwouk.lotto.Kakao.KakaoData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService{

    @GET("common.do?method=getLottoNumber&drwNo=931")
    fun getNumber(): Call<LottoNumber>

    @GET("common.do?method=getLottoNumber&drwNo={page}")
    fun getNumberdrwNo(@Path("page") page : String) : Call<LottoNumber>


    @GET("v2/local/search/keyword.json")
    fun getKakaoAddress(
        @Header("Authorization") key : String,
        @Query("query") address : String,
        @Query("x") x : String,
        @Query("y") y : String
    ): Call<KakaoData>

}
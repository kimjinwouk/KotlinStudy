package com.kimjinwouk.lotto.Retrofit.Interface

import com.kimjinwouk.lotto.DTO.LottoNumber
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitService{

    @GET("common.do?method=getLottoNumber&drwNo=931")
    fun getNumber(): Call<LottoNumber>

    @GET("common.do?method=getLottoNumber&drwNo={page}")
    fun getNumberdrwNo(@Path("page") page : String) : Call<LottoNumber>
}
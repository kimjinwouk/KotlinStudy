package com.kimjinwouk.map

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {
    @GET("https://run.mocky.io/v3/9968bd82-9235-424b-9f04-89136b768160")
    fun getHouseList(): Call<HouseDto>
}
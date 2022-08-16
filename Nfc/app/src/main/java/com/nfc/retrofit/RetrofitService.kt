package com.nfc.retrofit

import com.nfc.data.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface RetrofitService {
    @GET("version_2/?proc=ie_nfc_login")
    fun ie_nfc_login(
        @Query("_UserID") UserID: String,
        @Query("_Password") Password: String
    ): Call<List<User>>
    // Call -- 성공실패가 구분되어 떨어지고
    // Response -- 성공실패 구분되어지지않고 코드값으로 코딩해야된다.

    @GET("version_2/?proc=ie_nfc_getriderid")
    fun ie_nfc_getriderid(
        @Query("_Search") Search: String,
        @Query("_Type") Type: Int
    ): Call<List<Rider>>

    @GET("version_2/?proc=ie_nfc_savedata")
    fun ie_nfc_savedata(
        @Query("_RIDERID") RiderID: Int,
        @Query("_USERID") UserID: Int,
        @Query("_SerialNumber") SerialNumber: String,

    ): Call<List<ResultMessage>>


    @GET("version_2/?proc=ie_nfc_statistics")
    fun ie_nfc_statistics(
        ): Call<List<Statistics>>

    @GET("version_2/?proc=ie_nfc_profits")
    fun ie_nfc_profits(
        @Query("_DATE") RiderID: String,
    ): Call<List<Profits>>


}
package com.nfc.repository

import com.nfc.data.*
import retrofit2.Call
import retrofit2.Response

interface nfcRepository {

    suspend fun Login(Id : String, password : String) : Call<List<User>>
    suspend fun getRiderID(a : String, b : Int) : Call<List<Rider>>
    suspend fun getRiderList(a : String, b : Int) : Call<List<Rider>>
    suspend fun saveSerialNumber(RiderId :Int, UserId :Int, SerialNumber : String) : Call<List<ResultMessage>>
    suspend fun statistics() : Call<List<Statistics>>
    suspend fun profits(date : String) : Call<List<Profits>>



}
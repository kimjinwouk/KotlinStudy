package com.kimjinwouk.lotto.DTO

import com.google.gson.annotations.SerializedName

data class LottoNumber(

        @SerializedName("drwNo")
        var drwNo : Int,

        @SerializedName("drwtNo1")
        var No1 : Int,

        @SerializedName("drwtNo2")
        var No2 : Int,

        @SerializedName("drwtNo3")
        var No3 : Int,

        @SerializedName("drwtNo4")
        var No4 : Int,

        @SerializedName("drwtNo5")
        var No5 : Int,

        @SerializedName("drwtNo6")
        var No6 : Int
)

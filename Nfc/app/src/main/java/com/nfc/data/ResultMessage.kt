package com.nfc.data

import com.google.gson.annotations.SerializedName

data class ResultMessage(
    @SerializedName("ResultMsg")
    var ResultMsg: String = "0",
    @SerializedName("ResultCode")
    var ResultCode: String = "0"
)

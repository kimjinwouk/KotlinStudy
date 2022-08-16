package com.nfc.data

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("USERID")
    var USERID: String = "0"
)

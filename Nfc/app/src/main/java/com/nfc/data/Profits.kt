package com.nfc.data

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Profits(
    @SerializedName("ODate")
    var ODate: String,
    @SerializedName("Cash")
    var Cash: String = "0",
    @SerializedName("Type")
    var Type: String = "0"
)

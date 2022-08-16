package com.nfc.data

import com.google.gson.annotations.SerializedName

data class Statistics(
    @SerializedName("Nid")
    var Nid: String = "0",
    @SerializedName("RiderCount")
    var RiderCount: String = "0",

)

package com.nfc.data

import com.google.gson.annotations.SerializedName

data class Rider(
    @SerializedName("RiderID")
    var RiderID: String = "0",
    @SerializedName("RName")
    var RName: String = "0",
    @SerializedName("RPda")
    var RPda: String = "0",
    @SerializedName("RSituation")
    var RSituation: String = "0",
    @SerializedName("Misu")
    var Misu: String = "0"
)

package com.cjapps.prop.data.network.models

import com.google.gson.annotations.SerializedName

data class TickerValueResponse(
    @SerializedName("tickerValues")
    val tickerValues: List<TickerValue>
)

data class TickerValue(
    @SerializedName("ticker")
    val ticker: String,
    @SerializedName("value")
    val value: String
)

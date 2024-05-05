package com.cjapps.prop.data.network.models

import com.google.gson.annotations.SerializedName

data class AppDataResponse(
    @SerializedName("minimumBuildVersion")
    val minimumBuildVersion: Int
)

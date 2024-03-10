package com.cjapps.prop.data.network.models

import com.google.gson.annotations.SerializedName

data class AppData(
    @SerializedName("minimumBuildVersion")
    val minimumBuildVersion: Int
)

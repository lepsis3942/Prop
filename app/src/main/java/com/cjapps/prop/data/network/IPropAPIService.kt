package com.cjapps.prop.data.network

import com.cjapps.prop.data.network.models.AppData
import retrofit2.Response
import retrofit2.http.GET

interface IPropAPIService {
    @GET("/appData")
    suspend fun getAppData(): Response<AppData>
}
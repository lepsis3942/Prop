package com.cjapps.prop.data.network

import com.cjapps.prop.data.network.models.AppDataResponse
import com.cjapps.prop.data.network.models.TickerValueResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IPropAPIService {
    @GET("/appData")
    suspend fun getAppData(): Response<AppDataResponse>

    @GET("/currentTickerValue")
    suspend fun getCurrentTickerValue(@Query("symbols") symbols: String): Response<TickerValueResponse>
}
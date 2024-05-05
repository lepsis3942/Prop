package com.cjapps.prop.data

import com.cjapps.prop.data.network.IPropAPIService
import com.cjapps.prop.models.AppData
import javax.inject.Inject

class PropRepository @Inject constructor(
    private val propAPIService: IPropAPIService
) : IPropRepository {
    override suspend fun getAppData(): Result<AppData> {
        val response = propAPIService.getAppData()
        if (response.isSuccessful) {
            val appData = response.body()
            if (appData != null) {
                return Result.success(AppData(appData.minimumBuildVersion))
            }
        }
        return Result.failure(Exception("Failed to fetch app data"))
    }

    override suspend fun getCurrentTickerValue(symbols: List<String>): Result<Map<String, String>> {
        val response = propAPIService.getCurrentTickerValue(symbols.joinToString(","))
        if (response.isSuccessful) {
            val tickerValueResponse = response.body()
            if (tickerValueResponse != null) {
                return Result.success(tickerValueResponse.tickerValues.associate { it.ticker.uppercase() to it.value })
            }
        }
        return Result.failure(Exception("Failed to fetch ticker value"))
    }
}
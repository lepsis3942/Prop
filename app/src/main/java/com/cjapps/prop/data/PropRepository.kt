package com.cjapps.prop.data

import com.cjapps.prop.data.network.IPropAPIService
import com.cjapps.prop.models.AppData
import javax.inject.Inject

class PropRepository @Inject constructor(
    private val propAPIService: IPropAPIService
) : IPropRepository {
    override suspend fun getAppData(): AppData {
        val response = propAPIService.getAppData()
        if (response.isSuccessful) {
            val appData = response.body()
            if (appData != null) {
                return AppData(appData.minimumBuildVersion)
            }
        }
        throw Exception("Failed to fetch app data")
    }
}
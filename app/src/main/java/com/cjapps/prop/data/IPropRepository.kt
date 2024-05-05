package com.cjapps.prop.data

import com.cjapps.prop.models.AppData

interface IPropRepository {
    suspend fun getAppData(): Result<AppData>
    suspend fun getCurrentTickerValue(symbols: List<String>): Result<Map<String, String>>
}
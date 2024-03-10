package com.cjapps.prop.data

import com.cjapps.prop.models.AppData

interface IPropRepository {
    suspend fun getAppData(): AppData
}
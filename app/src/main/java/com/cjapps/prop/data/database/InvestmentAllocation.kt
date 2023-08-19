package com.cjapps.prop.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InvestmentAllocation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "ticker_name") val tickerName: String,
    @ColumnInfo(name = "current_invested_amount") val currentInvestedAmount: String,
    @ColumnInfo(name = "desired_percentage") val desiredPercentage: String
)
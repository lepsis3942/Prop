@file:Suppress("SpellCheckingInspection")

package com.cjapps.prop.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentAllocationDao {
    @Query("SELECT * FROM investmentallocation")
    fun getAll(): Flow<List<InvestmentAllocation>>

    @Query("SELECT * FROM investmentallocation WHERE ticker_name = :tickerName")
    fun getAllByTickerName(tickerName: String): List<InvestmentAllocation>

    @Insert
    suspend fun insert(investmentAllocation: InvestmentAllocation)

    @Update
    suspend fun update(investmentAllocation: InvestmentAllocation): Int

    @Delete
    suspend fun delete(investmentAllocation: InvestmentAllocation): Int
}

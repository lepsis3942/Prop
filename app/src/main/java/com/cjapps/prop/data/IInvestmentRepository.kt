package com.cjapps.prop.data

import com.cjapps.prop.models.InvestmentAllocation
import kotlinx.coroutines.flow.Flow

interface IInvestmentRepository {
    fun getInvestments(): Flow<List<InvestmentAllocation>>
    suspend fun addInvestment(investment: InvestmentAllocation): Result<Unit>
    suspend fun updateInvestment(investment: InvestmentAllocation): Result<Unit>
}
package com.cjapps.prop.data

import com.cjapps.prop.models.InvestmentAllocation
import kotlinx.coroutines.flow.Flow

interface IInvestmentRepository {
    fun getInvestmentsAsFlow(): Flow<List<InvestmentAllocation>>
    suspend fun getInvestments(): List<InvestmentAllocation>
    suspend fun addInvestment(investment: InvestmentAllocation): Result<Unit>
    suspend fun updateInvestment(investment: InvestmentAllocation): Result<Unit>
    suspend fun deleteInvestment(investment: InvestmentAllocation): Result<Unit>
}
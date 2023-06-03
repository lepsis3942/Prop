package com.cjapps.prop.data

import com.cjapps.prop.models.InvestmentAllocation

interface IInvestmentRepository {
    suspend fun getInvestments(): List<InvestmentAllocation>
}
package com.cjapps.prop.data

import com.cjapps.prop.models.InvestmentAllocation
import java.math.BigDecimal
import javax.inject.Inject

class InvestmentRepository @Inject constructor() : IInvestmentRepository {
    override suspend fun getInvestments(): List<InvestmentAllocation> {
        return listOf(
            InvestmentAllocation("SCHB", BigDecimal("0.23"), BigDecimal("54797.12")),
            InvestmentAllocation("SCHC", BigDecimal("0.54"), BigDecimal("12000.67")),
            InvestmentAllocation("TSLA", BigDecimal("0.12"), BigDecimal("3867.74")),
            InvestmentAllocation("MSFT", BigDecimal("0.08"), BigDecimal("230.11")),
            InvestmentAllocation("V", BigDecimal("0.03"), BigDecimal("230.11")),
        )
    }
}
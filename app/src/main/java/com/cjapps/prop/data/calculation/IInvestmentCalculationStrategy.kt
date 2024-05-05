package com.cjapps.prop.data.calculation

import com.cjapps.prop.models.InvestmentAllocation
import java.math.BigDecimal

interface IInvestmentCalculationStrategy {
    fun calculatePurchaseAmounts(
        currentInvestments: List<InvestmentAllocation>,
        amountToInvest: BigDecimal
    ): Map<InvestmentAllocation, BigDecimal>

    fun calculateSharesToBuy(
        investmentAmt: BigDecimal,
        marketPrice: BigDecimal
    ): Int
}
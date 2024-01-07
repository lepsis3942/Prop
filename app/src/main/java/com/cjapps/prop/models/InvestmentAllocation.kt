package com.cjapps.prop.models

import java.math.BigDecimal

data class InvestmentAllocation(
    val id: Int? = null,
    val tickerName: String,
    val desiredPercentage: BigDecimal,
    val currentInvestedAmount: BigDecimal
) {
    fun amountAwayFromIdealAmount(totalForAllAccounts: BigDecimal): BigDecimal {
        val desiredAmount = desiredPercentage * totalForAllAccounts
        return desiredAmount - currentInvestedAmount
    }

    /**
     * Percentage as an integer out of 100
     */
    fun realPercentage(totalForAllAccounts: BigDecimal): BigDecimal {
        return (currentInvestedAmount / totalForAllAccounts) * BigDecimal(100)
    }
}

package com.cjapps.prop.models

import java.math.BigDecimal

data class InvestmentAllocation(
    val id: Int?,
    val tickerName: String,
    val desiredPercentage: BigDecimal,
    val currentInvestedAmount: BigDecimal
) {
    fun amountAwayFromIdealAmount(totalForAllAccounts: BigDecimal): BigDecimal {
        val desiredAmount = desiredPercentage * totalForAllAccounts
        return desiredAmount - currentInvestedAmount
    }

    fun realPercentage(totalForAllAccounts: BigDecimal): BigDecimal {
        return currentInvestedAmount / totalForAllAccounts
    }
}

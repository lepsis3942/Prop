package com.cjapps.prop.data.calculation

import com.cjapps.prop.models.InvestmentAllocation
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Engine that calculates investments that are furthest below their ideal allocation and
 * maximizes incoming money to these accounts until they reach the desired investment before
 * moving to the next-furthest investment. Investments that are over-allocated will receive no
 * additional allocations.
 */
class DefaultInvestmentCalculationStrategy : IInvestmentCalculationStrategy {
    override fun calculatePurchaseAmounts(
        currentInvestments: List<InvestmentAllocation>,
        amountToInvest: BigDecimal
    ): Map<InvestmentAllocation, BigDecimal> {
        if (currentInvestments.isEmpty()) {
            return mapOf()
        }

        if (currentInvestments.any { it.desiredPercentage > BigDecimal("100.0") }) {
            throw IllegalArgumentException("Desired percentage cannot be greater than 100")
        }

        val totalCurrentInvestment = currentInvestments.fold(BigDecimal.ZERO) { total, item ->
            total + item.currentInvestedAmount
        }

        val investmentsInPriorityOrder =
            currentInvestments.sortedByDescending { investmentAllocation ->
                percentOffFromIdeal(
                    investment = investmentAllocation,
                    totalCurrentlyInvested = totalCurrentInvestment,
                    amountToInvest = amountToInvest
                )
            }

        val investmentsToMake = mutableMapOf<InvestmentAllocation, BigDecimal>()
        var amountLeftToInvest = amountToInvest.setScale(3, RoundingMode.HALF_EVEN)
        investmentsInPriorityOrder.forEach {
            if (amountLeftToInvest <= BigDecimal.ZERO) {
                investmentsToMake[it] = BigDecimal.ZERO
                return@forEach
            }
            val amountToInvestInThisAccount = (idealAmountAfterInvestment(
                investment = it,
                totalCurrentlyInvested = totalCurrentInvestment,
                amountToInvest = amountToInvest
            ) - it.currentInvestedAmount).setScale(3, RoundingMode.HALF_EVEN)
            if (amountLeftToInvest >= amountToInvestInThisAccount) {
                investmentsToMake[it] = amountToInvestInThisAccount
                amountLeftToInvest -= amountToInvestInThisAccount
            } else {
                investmentsToMake[it] = amountLeftToInvest
                amountLeftToInvest = BigDecimal.ZERO
            }
        }

        if (investmentsToMake.isEmpty()) {
            return investmentsToMake
        }

        // Perform accuracy checks
        val scaledOriginalAmountToInvest = amountToInvest.setScale(2, RoundingMode.DOWN)
        val scaledTotalInvestment = getMapSum(investmentsToMake).setScale(2, RoundingMode.DOWN)
        val originalVsCalculatedComparison =
            scaledOriginalAmountToInvest.compareTo(scaledTotalInvestment)
        if (originalVsCalculatedComparison != 0) {
            val difference = (scaledOriginalAmountToInvest - scaledTotalInvestment).abs()
            val firstInvestment = investmentsToMake.keys.firstOrNull() ?: return investmentsToMake
            val firstInvestmentAmount =
                investmentsToMake[firstInvestment] ?: return investmentsToMake

            if (originalVsCalculatedComparison == -1) {
                // Original amount to invest is less than the calculated total investment
                // This is due to rounding errors, so we will add the difference to the first investment
                investmentsToMake[firstInvestment] = firstInvestmentAmount - difference
            } else if (originalVsCalculatedComparison == 1) {
                // Original amount to invest is greater than the calculated total investment
                // This is due to rounding errors, so we will add the difference to the first investment
                investmentsToMake[firstInvestment] = firstInvestmentAmount + difference
            }
        }

        return investmentsToMake
    }

    override fun calculateSharesToBuy(investmentAmt: BigDecimal, marketPrice: BigDecimal): Int {
        if (investmentAmt <= BigDecimal.ZERO || marketPrice <= BigDecimal.ZERO) {
            return 0
        }
        return investmentAmt.divide(marketPrice, RoundingMode.DOWN).toInt()
    }

    private fun idealAmountAfterInvestment(
        investment: InvestmentAllocation,
        totalCurrentlyInvested: BigDecimal,
        amountToInvest: BigDecimal
    ): BigDecimal {
        val totalAfterInvestment = totalCurrentlyInvested + amountToInvest
        return totalAfterInvestment * (investment.desiredPercentage.setScale(3)
            .divide(BigDecimal("100.0"), RoundingMode.HALF_EVEN))
    }

    private fun percentOffFromIdeal(
        investment: InvestmentAllocation,
        totalCurrentlyInvested: BigDecimal,
        amountToInvest: BigDecimal
    ): BigDecimal {
        val totalAfterInvestment = totalCurrentlyInvested + amountToInvest
        val idealAmount = totalAfterInvestment * (investment.desiredPercentage.setScale(3)
            .divide(BigDecimal("100.0"), RoundingMode.HALF_EVEN))
        return (idealAmount - investment.currentInvestedAmount).divide(
            idealAmount,
            RoundingMode.HALF_EVEN
        )
    }

    private fun getMapSum(map: Map<InvestmentAllocation, BigDecimal>): BigDecimal {
        return map.values.fold(BigDecimal.ZERO) { total, item ->
            total + item
        }
    }
}
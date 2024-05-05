package com.cjapps.prop.data.calculation

import com.cjapps.prop.models.InvestmentAllocation
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class DefaultInvestmentCalculationStrategyTest {
    private lateinit var strategy: DefaultInvestmentCalculationStrategy
    private lateinit var defaultInvestments: List<InvestmentAllocation>

    @Before
    fun setUp() {
        strategy = DefaultInvestmentCalculationStrategy()
        defaultInvestments = listOf(
            InvestmentAllocation(
                id = 1,
                tickerName = "A",
                currentInvestedAmount = BigDecimal("38592.76"),
                desiredPercentage = BigDecimal("52.0")
            ),
            InvestmentAllocation(
                id = 2,
                tickerName = "B",
                currentInvestedAmount = BigDecimal("18700.05"),
                desiredPercentage = BigDecimal("25.0")
            ),
            InvestmentAllocation(
                id = 3,
                tickerName = "C",
                currentInvestedAmount = BigDecimal("6141.30"),
                desiredPercentage = BigDecimal("10.0")
            ),
            InvestmentAllocation(
                id = 4,
                tickerName = "D",
                currentInvestedAmount = BigDecimal("5861.52"),
                desiredPercentage = BigDecimal("8.0")
            ),
            InvestmentAllocation(
                id = 5,
                tickerName = "E",
                currentInvestedAmount = BigDecimal("3714.14"),
                desiredPercentage = BigDecimal("5.0")
            ),
        )
    }

    @Test
    fun testAllAllocationResultsSumToAmountToBeInvested() {
        val amountToInvest = BigDecimal("4514.58")

        val calculatedInvestments = strategy.calculatePurchaseAmounts(
            currentInvestments = defaultInvestments,
            amountToInvest = amountToInvest
        )

        assertEquals(amountToInvest, getMapSum(calculatedInvestments))
    }

    @Test
    fun testInvestmentsAllocatedInOrderOfDistanceFromIdeal() {
        // Enough to bring B & C to their desired percentages, but not
        // enough for A
        val amountToInvest = BigDecimal("1000.00")
        val investments = listOf(
            InvestmentAllocation(
                id = 1,
                tickerName = "A",
                currentInvestedAmount = BigDecimal("1950.00"),
                desiredPercentage = BigDecimal("50.0")
            ),
            InvestmentAllocation(
                id = 2,
                tickerName = "B",
                currentInvestedAmount = BigDecimal("600.00"),
                desiredPercentage = BigDecimal("25.0")
            ),
            InvestmentAllocation(
                id = 3,
                tickerName = "C",
                currentInvestedAmount = BigDecimal("340.00"),
                desiredPercentage = BigDecimal("25.0")
            )
        )

        val calculatedInvestments = strategy.calculatePurchaseAmounts(
            currentInvestments = investments,
            amountToInvest = amountToInvest
        )

        assertEquals(3, calculatedInvestments.size)
        assertEquals("C", calculatedInvestments.keys.first().tickerName)
        assertEquals("B", calculatedInvestments.keys.elementAt(1).tickerName)
        assertEquals("A", calculatedInvestments.keys.last().tickerName)
        // Ensure 0 got allocated to A as money ran out
        assertEquals(0, calculatedInvestments[investments[0]]?.compareTo(BigDecimal.ZERO))
        assertEquals(amountToInvest, getMapSum(calculatedInvestments))
    }

    @Test
    fun testInvestmentsAllocatedInDescendingOrderOfDistanceFromIdeal() {
        // Have enough to invest in B anc C, only enough leftover to add $1 to A
        val amountToInvest = BigDecimal("1012.00")
        val investments = listOf(
            InvestmentAllocation(
                id = 1,
                tickerName = "A",
                currentInvestedAmount = BigDecimal("1950.00"),
                desiredPercentage = BigDecimal("50.0")
            ),
            InvestmentAllocation(
                id = 2,
                tickerName = "B",
                currentInvestedAmount = BigDecimal("600.00"),
                desiredPercentage = BigDecimal("25.0")
            ),
            InvestmentAllocation(
                id = 3,
                tickerName = "C",
                currentInvestedAmount = BigDecimal("340.00"),
                desiredPercentage = BigDecimal("25.0")
            )
        )

        val calculatedInvestments = strategy.calculatePurchaseAmounts(
            currentInvestments = investments,
            amountToInvest = amountToInvest
        )

        assertEquals(3, calculatedInvestments.size)
        assertEquals("C", calculatedInvestments.keys.first().tickerName)
        assertEquals("B", calculatedInvestments.keys.elementAt(1).tickerName)
        assertEquals("A", calculatedInvestments.keys.last().tickerName)
        // Ensure 1 got allocated to A
        assertEquals(0, calculatedInvestments[investments[0]]?.compareTo(BigDecimal.ONE))
        assertEquals(amountToInvest, getMapSum(calculatedInvestments))
    }

    @Test
    fun testInvestmentsAllocatedCorrectlyWhenAlreadyIdeal() {
        val amountToInvest = BigDecimal("2345.00")
        val investments = listOf(
            InvestmentAllocation(
                id = 1,
                tickerName = "A",
                currentInvestedAmount = BigDecimal("1250.00"),
                desiredPercentage = BigDecimal("50.0")
            ),
            InvestmentAllocation(
                id = 2,
                tickerName = "B",
                currentInvestedAmount = BigDecimal("250.00"),
                desiredPercentage = BigDecimal("10.0")
            ),
            InvestmentAllocation(
                id = 3,
                tickerName = "C",
                currentInvestedAmount = BigDecimal("125.00"),
                desiredPercentage = BigDecimal("5.0")
            ),
            InvestmentAllocation(
                id = 4,
                tickerName = "D",
                currentInvestedAmount = BigDecimal("875.00"),
                desiredPercentage = BigDecimal("35.0")
            )
        )

        val totalAfterInvestment = getTotalInvestSum(investments, amountToInvest)

        val calculatedInvestments = strategy.calculatePurchaseAmounts(
            currentInvestments = investments,
            amountToInvest = amountToInvest
        )

        assertEquals(4, calculatedInvestments.size)
        calculatedInvestments.forEach {
            val expectedValueBasedOnIdealPercentage =
                totalAfterInvestment * (it.key.desiredPercentage.divide(BigDecimal("100.00")))
            val actualValueAfterCalculation = it.key.currentInvestedAmount + it.value
            assertEquals(
                0,
                expectedValueBasedOnIdealPercentage.compareTo(actualValueAfterCalculation)
            )
        }
    }

    @Test
    fun testInvestmentsAllocatedCorrectlyWhenSlightlyOffIdeal() {
        val amountToInvest = BigDecimal("2345.00")
        val investments = listOf(
            InvestmentAllocation(
                id = 1,
                tickerName = "A",
                currentInvestedAmount = BigDecimal("1000.00"),
                desiredPercentage = BigDecimal("50.0")
            ),
            InvestmentAllocation(
                id = 2,
                tickerName = "B",
                currentInvestedAmount = BigDecimal("300.00"),
                desiredPercentage = BigDecimal("10.0")
            ),
            InvestmentAllocation(
                id = 3,
                tickerName = "C",
                currentInvestedAmount = BigDecimal("225.00"),
                desiredPercentage = BigDecimal("5.0")
            ),
            InvestmentAllocation(
                id = 4,
                tickerName = "D",
                currentInvestedAmount = BigDecimal("975.00"),
                desiredPercentage = BigDecimal("35.0")
            )
        )

        val totalAfterInvestment = getTotalInvestSum(investments, amountToInvest)

        val calculatedInvestments = strategy.calculatePurchaseAmounts(
            currentInvestments = investments,
            amountToInvest = amountToInvest
        )

        assertEquals(4, calculatedInvestments.size)
        calculatedInvestments.forEach {
            val expectedValueBasedOnIdealPercentage =
                totalAfterInvestment * (it.key.desiredPercentage.divide(BigDecimal("100.00")))
            val actualValueAfterCalculation = it.key.currentInvestedAmount + it.value
            assertEquals(
                0,
                expectedValueBasedOnIdealPercentage.compareTo(actualValueAfterCalculation)
            )
        }
    }

    @Test
    fun testOverAllocatedAccountsHaveAmountDistributedToOthers() {
        val amountToInvest = BigDecimal("700.00")
        val investments = listOf(
            InvestmentAllocation(
                id = 1,
                tickerName = "A",
                currentInvestedAmount = BigDecimal("1000.00"),
                desiredPercentage = BigDecimal("50.0")
            ),
            InvestmentAllocation(
                id = 2,
                tickerName = "B",
                currentInvestedAmount = BigDecimal("500.00"),
                desiredPercentage = BigDecimal("10.0")
            ),
            InvestmentAllocation(
                id = 3,
                tickerName = "C",
                currentInvestedAmount = BigDecimal("90.00"),
                desiredPercentage = BigDecimal("5.0")
            ),
            InvestmentAllocation(
                id = 4,
                tickerName = "D",
                currentInvestedAmount = BigDecimal("600.00"),
                desiredPercentage = BigDecimal("35.0")
            )
        )

        val calculatedInvestments = strategy.calculatePurchaseAmounts(
            currentInvestments = investments,
            amountToInvest = amountToInvest
        )

        assertEquals(amountToInvest, getMapSum(calculatedInvestments))
        assertEquals(investments[3], calculatedInvestments.keys.first()) // D
        assertEquals(investments[2], calculatedInvestments.keys.elementAt(1)) // C
        assertEquals(investments[0], calculatedInvestments.keys.elementAt(2)) // A
        assertEquals(
            BigDecimal.ZERO,
            calculatedInvestments[investments[1]]
        ) // B, because it's already over allocated
        assertEquals(investments[1], calculatedInvestments.keys.last()) // B
    }

    @Test
    fun testSharesToBuyReturns0WhenMarketPriceIs0() {
        assertEquals(
            0,
            strategy.calculateSharesToBuy(
                investmentAmt = BigDecimal("100.00"),
                marketPrice = BigDecimal.ZERO
            )
        )
    }

    @Test
    fun testSharesToBuyReturns0WhenInvestmentAmtIs0() {
        assertEquals(
            0,
            strategy.calculateSharesToBuy(
                investmentAmt = BigDecimal.ZERO,
                marketPrice = BigDecimal("23.81")
            )
        )
    }

    @Test
    fun testSharesToBuyReturns0WhenMarketPriceIsNegative() {
        assertEquals(
            0,
            strategy.calculateSharesToBuy(
                investmentAmt = BigDecimal("100.00"),
                marketPrice = BigDecimal("-4.99")
            )
        )
    }

    @Test
    fun testSharesToBuyReturns0WhenInvestmentAmtIsNegative() {
        assertEquals(
            0,
            strategy.calculateSharesToBuy(
                investmentAmt = BigDecimal("-40.99"),
                marketPrice = BigDecimal("23.81")
            )
        )
    }

    @Test
    fun testSharesToBuyRoundsDownIfNeeded() {
        assertEquals(
            10,
            strategy.calculateSharesToBuy(
                investmentAmt = BigDecimal("105.00"),
                marketPrice = BigDecimal("10.00")
            )
        )
        assertEquals(
            8,
            strategy.calculateSharesToBuy(
                investmentAmt = BigDecimal("89.9999"),
                marketPrice = BigDecimal("10.00")
            )
        )
        assertEquals(
            9,
            strategy.calculateSharesToBuy(
                investmentAmt = BigDecimal("89.9999"),
                marketPrice = BigDecimal("9.99")
            ) // 9.008998998999
        )
    }

    @Test
    fun testSharesToBuyGivesExactValueIfNoRounding() {
        assertEquals(
            100,
            strategy.calculateSharesToBuy(
                investmentAmt = BigDecimal("2000.00"),
                marketPrice = BigDecimal("20.00")
            )
        )

        assertEquals(
            7,
            strategy.calculateSharesToBuy(
                investmentAmt = BigDecimal("348.46"),
                marketPrice = BigDecimal("49.78")
            )
        )
    }

    private fun getMapSum(map: Map<InvestmentAllocation, BigDecimal>): BigDecimal {
        return map.values.fold(BigDecimal.ZERO) { total, item ->
            total + item
        }.setScale(2, RoundingMode.HALF_EVEN)
    }

    private fun getTotalInvestSum(
        investments: List<InvestmentAllocation>,
        amountToBeInvested: BigDecimal
    ): BigDecimal {
        return (investments.fold(BigDecimal.ZERO) { total, item ->
            total + item.currentInvestedAmount
        } + amountToBeInvested).setScale(2, RoundingMode.HALF_EVEN)
    }
}
package com.cjapps.prop.data.mappers

import com.cjapps.prop.models.InvestmentAllocation
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject
import com.cjapps.prop.data.database.InvestmentAllocation as EntityInvestmentAllocation


class DaoMapper @Inject constructor() : IDaoMapper {
    private val monetaryNumberFormatter: DecimalFormat = DecimalFormat("#.00")
    private val percentageNumberFormatter: DecimalFormat = DecimalFormat("#.0")

    init {
        monetaryNumberFormatter.minimumFractionDigits = 2
        monetaryNumberFormatter.maximumFractionDigits = 2
        monetaryNumberFormatter.minimumIntegerDigits = 1
        monetaryNumberFormatter.isGroupingUsed = false
        monetaryNumberFormatter.roundingMode = RoundingMode.DOWN

        percentageNumberFormatter.minimumFractionDigits = 1
        percentageNumberFormatter.maximumFractionDigits = 1
        percentageNumberFormatter.maximumIntegerDigits = 3
        percentageNumberFormatter.isGroupingUsed = false
        percentageNumberFormatter.roundingMode = RoundingMode.DOWN
    }

    override fun investmentAllocationToEntity(investmentAllocation: InvestmentAllocation): EntityInvestmentAllocation {
        val formattedPercentage =
            percentageNumberFormatter.format(investmentAllocation.desiredPercentage)
        val splitPercentage = formattedPercentage.split(".")
        var trimmedPercentageStart = splitPercentage[0].trimStart('0')
        trimmedPercentageStart = trimmedPercentageStart.ifEmpty { "0" }

        return EntityInvestmentAllocation(
            id = investmentAllocation.id ?: 0,
            tickerName = investmentAllocation.tickerName,
            currentInvestedAmount = monetaryNumberFormatter.format(investmentAllocation.currentInvestedAmount),
            desiredPercentage = "$trimmedPercentageStart.${splitPercentage[1]}"
        )
    }

    override fun entityToInvestmentAllocation(investmentAllocationEntity: EntityInvestmentAllocation): InvestmentAllocation {
        return InvestmentAllocation(
            id = investmentAllocationEntity.id,
            tickerName = investmentAllocationEntity.tickerName,
            currentInvestedAmount = BigDecimal(investmentAllocationEntity.currentInvestedAmount),
            desiredPercentage = BigDecimal(investmentAllocationEntity.desiredPercentage)
        )
    }
}
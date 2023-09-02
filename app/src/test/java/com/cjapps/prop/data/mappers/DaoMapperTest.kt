package com.cjapps.prop.data.mappers

import com.cjapps.prop.models.InvestmentAllocation
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import com.cjapps.prop.data.database.InvestmentAllocation as EntityInvestmentAllocation

class DaoMapperTest {
    private lateinit var mapper: DaoMapper

    @Before
    fun setUp() {
        mapper = DaoMapper()
    }

    @Test
    fun mapperConvertsAllFieldsToDomainModel() {
        val expected = InvestmentAllocation(
            id = 3,
            tickerName = "SCHB",
            currentInvestedAmount = BigDecimal("13029.45"),
            desiredPercentage = BigDecimal("89.0")
        )
        val source = EntityInvestmentAllocation(
            id = 3,
            tickerName = "SCHB",
            currentInvestedAmount = "13029.45",
            desiredPercentage = "89.0"
        )

        val converted = mapper.entityToInvestmentAllocation(source)

        assertEquals(expected, converted)
    }

    @Test
    fun mapperConvertsAllFieldsToEntityModel() {
        val expected = EntityInvestmentAllocation(
            id = 3,
            tickerName = "SCHB",
            currentInvestedAmount = "13029.45",
            desiredPercentage = "89.0"
        )
        val source = InvestmentAllocation(
            id = 3,
            tickerName = "SCHB",
            currentInvestedAmount = BigDecimal("13029.45"),
            desiredPercentage = BigDecimal("89.0")
        )

        val converted = mapper.investmentAllocationToEntity(source)

        assertEquals(expected, converted)
    }

    @Test
    fun mapperConversionRoundTripIsEqual() {
        val source = EntityInvestmentAllocation(
            id = 3,
            tickerName = "SCHB",
            currentInvestedAmount = "13029.45",
            desiredPercentage = "89.0"
        )

        val entityToDomain = mapper.entityToInvestmentAllocation(source)
        val domainToEntity = mapper.investmentAllocationToEntity(entityToDomain)

        // Entity -> Domain -> Entity again is the same
        assertEquals(source, domainToEntity)
    }

    @Test
    fun mapperProvidesEntityWithMonetary2FractionalPlaces() {
        val source = InvestmentAllocation(
            id = 3,
            tickerName = "SCHB",
            currentInvestedAmount = BigDecimal("13029.4556"),
            desiredPercentage = BigDecimal("89.0")
        )

        val entity = mapper.investmentAllocationToEntity(source)

        assertEquals("13029.45", entity.currentInvestedAmount)
    }

    @Test
    fun mapperProvidesEntityWithPercentage1FractionalPlace() {
        val source = InvestmentAllocation(
            id = 3,
            tickerName = "SCHB",
            currentInvestedAmount = BigDecimal("13029.45"),
            desiredPercentage = BigDecimal("89.66")
        )

        val entity = mapper.investmentAllocationToEntity(source)

        assertEquals("89.6", entity.desiredPercentage)
    }

    @Test
    fun mapperRepresentsAllValidPercentages() {
        val source = InvestmentAllocation(
            id = 3,
            tickerName = "SCHB",
            currentInvestedAmount = BigDecimal("13029.45"),
            desiredPercentage = BigDecimal("89")
        )

        val eightyNine = mapper.investmentAllocationToEntity(source)
        val one =
            mapper.investmentAllocationToEntity(source.copy(desiredPercentage = BigDecimal("1")))
        val oneHundred =
            mapper.investmentAllocationToEntity(source.copy(desiredPercentage = BigDecimal("100")))
        val oneHundredWithDecimal =
            mapper.investmentAllocationToEntity(source.copy(desiredPercentage = BigDecimal("100.4")))
        val oneThousandWithDecimal =
            mapper.investmentAllocationToEntity(source.copy(desiredPercentage = BigDecimal("1000.4")))
        val zeroZero4 =
            mapper.investmentAllocationToEntity(source.copy(desiredPercentage = BigDecimal("004")))
        val fiftySixDot8 =
            mapper.investmentAllocationToEntity(source.copy(desiredPercentage = BigDecimal("56.8")))

        assertEquals("89.0", eightyNine.desiredPercentage)
        assertEquals("1.0", one.desiredPercentage)
        assertEquals("100.0", oneHundred.desiredPercentage)
        assertEquals("100.4", oneHundredWithDecimal.desiredPercentage)
        assertEquals("0.4", oneThousandWithDecimal.desiredPercentage)
        assertEquals("4.0", zeroZero4.desiredPercentage)
        assertEquals("56.8", fiftySixDot8.desiredPercentage)
    }

    @Test
    fun mapperRepresentsAllValidMonetaryValues() {
        val source = InvestmentAllocation(
            id = 3,
            tickerName = "SCHB",
            currentInvestedAmount = BigDecimal("13029.45"),
            desiredPercentage = BigDecimal("89")
        )

        val normalThousands = mapper.investmentAllocationToEntity(source)
        val intOnly =
            mapper.investmentAllocationToEntity(source.copy(currentInvestedAmount = BigDecimal("2853")))
        val oneDecimalPoint =
            mapper.investmentAllocationToEntity(source.copy(currentInvestedAmount = BigDecimal("456.9")))
        val oneDecimalPointLeadingZero =
            mapper.investmentAllocationToEntity(source.copy(currentInvestedAmount = BigDecimal("6.09")))
        val truncatedDecimal =
            mapper.investmentAllocationToEntity(source.copy(currentInvestedAmount = BigDecimal("6.99999")))

        assertEquals("13029.45", normalThousands.currentInvestedAmount)
        assertEquals("2853.00", intOnly.currentInvestedAmount)
        assertEquals("456.90", oneDecimalPoint.currentInvestedAmount)
        assertEquals("6.09", oneDecimalPointLeadingZero.currentInvestedAmount)
        assertEquals("6.99", truncatedDecimal.currentInvestedAmount)
    }

    @Test
    fun idSetToZeroIfNotSet() {
        val source = InvestmentAllocation(
            tickerName = "SCHB",
            currentInvestedAmount = BigDecimal("13029.45"),
            desiredPercentage = BigDecimal("89.0")
        )

        val converted = mapper.investmentAllocationToEntity(source)

        assertEquals(converted.id, 0)
    }
}
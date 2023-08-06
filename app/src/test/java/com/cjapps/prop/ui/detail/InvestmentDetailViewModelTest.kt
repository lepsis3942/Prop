package com.cjapps.prop.ui.detail

import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.data.IInvestmentRepository
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class InvestmentDetailViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var mockDispatcher: IDispatcherProvider

    @MockK
    lateinit var mockInvestmentRepository: IInvestmentRepository

    private lateinit var viewModel: InvestmentDetailViewModel

    @Before
    fun setUp() {
        viewModel = InvestmentDetailViewModel(mockDispatcher, mockInvestmentRepository)
    }

    @Test
    fun convertRawCurrencyInputConvertsEmptyString() {
        assertEquals(BigDecimal("0.00"), viewModel.convertRawCurrencyInput(""))
    }

    @Test
    fun convertRawCurrencyInputConvertsZeroValues() {
        assertEquals(BigDecimal("0.00"), viewModel.convertRawCurrencyInput("0"))
        assertEquals(BigDecimal("0.00"), viewModel.convertRawCurrencyInput("00"))
        assertEquals(BigDecimal("0.00"), viewModel.convertRawCurrencyInput("000"))
    }

    @Test
    fun convertRawCurrencyInputConvertsLessThanOneValues() {
        assertEquals(BigDecimal("0.01"), viewModel.convertRawCurrencyInput("1"))
        assertEquals(BigDecimal("0.01"), viewModel.convertRawCurrencyInput("01"))
        assertEquals(BigDecimal("0.94"), viewModel.convertRawCurrencyInput("94"))
    }

    @Test
    fun convertRawCurrencyInputConvertsMoreThanOneValues() {
        assertEquals(BigDecimal("1.00"), viewModel.convertRawCurrencyInput("100"))
        assertEquals(BigDecimal("9.01"), viewModel.convertRawCurrencyInput("901"))
        assertEquals(BigDecimal("7.35"), viewModel.convertRawCurrencyInput("735"))
        assertEquals(BigDecimal("944.58"), viewModel.convertRawCurrencyInput("94458"))
        assertEquals(
            BigDecimal("49823753.98"),
            viewModel.convertRawCurrencyInput("4982375398")
        )
    }

    @Test
    fun updateCurrentValueFiltersLeadingZeros() {
        viewModel.updateCurrentValue("0")
        assertEquals("", viewModel.uiState.value.currentInvestmentValue)

        viewModel.updateCurrentValue("00")
        assertEquals("", viewModel.uiState.value.currentInvestmentValue)

        viewModel.updateCurrentValue("0000")
        assertEquals("", viewModel.uiState.value.currentInvestmentValue)

        viewModel.updateCurrentValue("000067")
        assertEquals("67", viewModel.uiState.value.currentInvestmentValue)
    }

    @Test
    fun updateCurrentValueDoesNotFilterIntermediateZeros() {
        viewModel.updateCurrentValue("607")
        assertEquals("607", viewModel.uiState.value.currentInvestmentValue)

        viewModel.updateCurrentValue("6007")
        assertEquals("6007", viewModel.uiState.value.currentInvestmentValue)

        viewModel.updateCurrentValue("00230704")
        assertEquals("230704", viewModel.uiState.value.currentInvestmentValue)
    }

    @Test
    fun updateCurrentValueFiltersNonDigitChars() {
        viewModel.updateCurrentValue("60jf;7")
        assertEquals("607", viewModel.uiState.value.currentInvestmentValue)

        viewModel.updateCurrentValue("-60%07+")
        assertEquals("6007", viewModel.uiState.value.currentInvestmentValue)

        viewModel.updateCurrentValue("+6007-")
        assertEquals("6007", viewModel.uiState.value.currentInvestmentValue)

        viewModel.updateCurrentValue("0!023&07=04")
        assertEquals("230704", viewModel.uiState.value.currentInvestmentValue)
    }
}
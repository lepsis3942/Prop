package com.cjapps.prop.ui.detail

import androidx.lifecycle.SavedStateHandle
import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.MainDispatcherRule
import com.cjapps.prop.data.IInvestmentRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class InvestmentDetailViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    lateinit var mockDispatcher: IDispatcherProvider

    @MockK
    lateinit var mockInvestmentRepository: IInvestmentRepository

    @MockK
    lateinit var mockStateHandle: SavedStateHandle

    private lateinit var viewModel: InvestmentDetailViewModel

    @Before
    fun setUp() {
        every { mockStateHandle.get<String>("investmentId") } returns null
        every { mockInvestmentRepository.getInvestments() } returns flow { emit(listOf()) }
        viewModel = InvestmentDetailViewModel(
            mockDispatcher,
            mockStateHandle,
            mockInvestmentRepository
        )
    }

    @Test
    fun convertRawCurrencyInputConvertsEmptyString() {
        assertEquals(BigDecimal("0.00"), viewModel.rawCurrencyInputToBigDecimal(""))
    }

    @Test
    fun convertRawCurrencyInputConvertsZeroValues() {
        assertEquals(BigDecimal("0.00"), viewModel.rawCurrencyInputToBigDecimal("0"))
        assertEquals(BigDecimal("0.00"), viewModel.rawCurrencyInputToBigDecimal("00"))
        assertEquals(BigDecimal("0.00"), viewModel.rawCurrencyInputToBigDecimal("000"))
    }

    @Test
    fun convertRawCurrencyInputConvertsLessThanOneValues() {
        assertEquals(BigDecimal("0.01"), viewModel.rawCurrencyInputToBigDecimal("1"))
        assertEquals(BigDecimal("0.01"), viewModel.rawCurrencyInputToBigDecimal("01"))
        assertEquals(BigDecimal("0.94"), viewModel.rawCurrencyInputToBigDecimal("94"))
    }

    @Test
    fun convertRawCurrencyInputConvertsMoreThanOneValues() {
        assertEquals(BigDecimal("1.00"), viewModel.rawCurrencyInputToBigDecimal("100"))
        assertEquals(BigDecimal("9.01"), viewModel.rawCurrencyInputToBigDecimal("901"))
        assertEquals(BigDecimal("7.35"), viewModel.rawCurrencyInputToBigDecimal("735"))
        assertEquals(BigDecimal("944.58"), viewModel.rawCurrencyInputToBigDecimal("94458"))
        assertEquals(
            BigDecimal("49823753.98"),
            viewModel.rawCurrencyInputToBigDecimal("4982375398")
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

    @Test
    fun bigDecimalToRawCurrencyStripsDecimalPoint() {
        assertEquals("128567", viewModel.bigDecimalToRawCurrency(BigDecimal("1285.67")))
        assertEquals("501", viewModel.bigDecimalToRawCurrency(BigDecimal("5.01")))
    }

    @Test
    fun bigDecimalToRawCurrencyPadsDecimal() {
        assertEquals("500", viewModel.bigDecimalToRawCurrency(BigDecimal("5")))
        assertEquals("790", viewModel.bigDecimalToRawCurrency(BigDecimal("7.9")))
    }

    @Test
    fun bigDecimalToRawCurrencyCreatesZeroCorrectly() {
        assertEquals("000", viewModel.bigDecimalToRawCurrency(BigDecimal("0")))
    }

    @Test
    fun bigDecimalToRawCurrencyTruncatesDecimal() {
        assertEquals("7858", viewModel.bigDecimalToRawCurrency(BigDecimal("78.5893")))
    }
}
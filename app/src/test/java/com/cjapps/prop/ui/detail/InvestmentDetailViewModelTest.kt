package com.cjapps.prop.ui.detail

import androidx.lifecycle.SavedStateHandle
import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.MainDispatcherRule
import com.cjapps.prop.data.IInvestmentRepository
import com.cjapps.prop.ui.extensions.bigDecimalToRawCurrency
import com.cjapps.prop.ui.extensions.rawCurrencyInputToBigDecimal
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
        every { mockInvestmentRepository.getInvestmentsAsFlow() } returns flow { emit(listOf()) }
        viewModel = InvestmentDetailViewModel(
            mockDispatcher,
            mockStateHandle,
            mockInvestmentRepository
        )
    }

    @Test
    fun convertRawCurrencyInputConvertsEmptyString() {
        assertEquals(BigDecimal("0.00"), "".rawCurrencyInputToBigDecimal())
    }

    @Test
    fun convertRawCurrencyInputConvertsZeroValues() {
        assertEquals(BigDecimal("0.00"), "0".rawCurrencyInputToBigDecimal())
        assertEquals(BigDecimal("0.00"), "00".rawCurrencyInputToBigDecimal())
        assertEquals(BigDecimal("0.00"), "000".rawCurrencyInputToBigDecimal())
    }

    @Test
    fun convertRawCurrencyInputConvertsLessThanOneValues() {
        assertEquals(BigDecimal("0.01"), "1".rawCurrencyInputToBigDecimal())
        assertEquals(BigDecimal("0.01"), "01".rawCurrencyInputToBigDecimal())
        assertEquals(BigDecimal("0.94"), "94".rawCurrencyInputToBigDecimal())
    }

    @Test
    fun convertRawCurrencyInputConvertsMoreThanOneValues() {
        assertEquals(BigDecimal("1.00"), "100".rawCurrencyInputToBigDecimal())
        assertEquals(BigDecimal("9.01"), "901".rawCurrencyInputToBigDecimal())
        assertEquals(BigDecimal("7.35"), "735".rawCurrencyInputToBigDecimal())
        assertEquals(BigDecimal("944.58"), "94458".rawCurrencyInputToBigDecimal())
        assertEquals(
            BigDecimal("49823753.98"),
            "4982375398".rawCurrencyInputToBigDecimal()
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
        assertEquals("128567", BigDecimal("1285.67").bigDecimalToRawCurrency())
        assertEquals("501", BigDecimal("5.01").bigDecimalToRawCurrency())
    }

    @Test
    fun bigDecimalToRawCurrencyPadsDecimal() {
        assertEquals("500", BigDecimal("5").bigDecimalToRawCurrency())
        assertEquals("790", BigDecimal("7.9").bigDecimalToRawCurrency())
    }

    @Test
    fun bigDecimalToRawCurrencyCreatesZeroCorrectly() {
        assertEquals("000", BigDecimal("0").bigDecimalToRawCurrency())
    }

    @Test
    fun bigDecimalToRawCurrencyTruncatesDecimal() {
        assertEquals("7858", BigDecimal("78.5893").bigDecimalToRawCurrency())
    }
}
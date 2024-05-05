package com.cjapps.prop.ui.invest.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.prop.data.IInvestmentRepository
import com.cjapps.prop.data.IPropRepository
import com.cjapps.prop.data.calculation.InvestmentCalculationStrategyFactory
import com.cjapps.prop.models.InvestmentAllocation
import com.cjapps.prop.ui.extensions.bigDecimalToUiFormattedCurrency
import com.cjapps.prop.ui.extensions.rawCurrencyInputToBigDecimal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class InvestResultSummaryViewModel @Inject constructor(
    private val investmentRepository: IInvestmentRepository,
    private val investmentStrategyFactory: InvestmentCalculationStrategyFactory,
    private val propRepository: IPropRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val uiStateFlow = MutableStateFlow<InvestResultScreenUiState>(
        InvestResultScreenUiState.Loading
    )
    private val investmentCalculationStrategy get() = investmentStrategyFactory.getInvestmentCalculationStrategy()

    val uiState: MutableStateFlow<InvestResultScreenUiState> get() = uiStateFlow

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val amountToInvest = (savedStateHandle.get<String>("amountToInvest")
                ?: "0").rawCurrencyInputToBigDecimal()
            val investmentValues = investmentRepository.getInvestments()
            val calculatedInvestments = investmentCalculationStrategy.calculatePurchaseAmounts(
                investmentValues, amountToInvest
            )
            val tickerMarketPricesResult =
                propRepository.getCurrentTickerValue(calculatedInvestments.entries.map { it.key.tickerName })
            uiStateFlow.update {
                InvestResultScreenUiState.CalculationComplete(
                    amountToInvest = amountToInvest.bigDecimalToUiFormattedCurrency(),
                    investments = calculatedInvestments.toList().sortedByDescending { it.second }
                        .map {
                            mapCalculatedInvestmentsToScreenValue(
                                investmentAllocation = it.first,
                                amountToInvest = it.second,
                                tickerPricesResult = tickerMarketPricesResult
                            )
                        }.toImmutableList()
                )
            }
        }
    }

    fun onInvestTapped() {
        viewModelScope.launch {
            val uiState = uiStateFlow.value
            if (uiState is InvestResultScreenUiState.CalculationComplete) {
                val investments = uiState.investments
                uiStateFlow.update { InvestResultScreenUiState.Loading }
                updateInvestmentValues(investments)
                uiStateFlow.update { InvestResultScreenUiState.InvestmentsSaved }
            }
        }
    }

    private fun mapCalculatedInvestmentsToScreenValue(
        investmentAllocation: InvestmentAllocation,
        amountToInvest: BigDecimal,
        tickerPricesResult: Result<Map<String, String>>
    ): InvestmentScreenUpdatedInvestmentValue {
        val tickerPrices =
            tickerPricesResult.getOrDefault(emptyMap())
        return InvestmentScreenUpdatedInvestmentValue(
            id = investmentAllocation.id ?: -1,
            investmentName = investmentAllocation.tickerName,
            amountToInvest = amountToInvest.bigDecimalToUiFormattedCurrency(),
            shareInfo = if (tickerPricesResult.isSuccess && tickerPrices.containsKey(
                    investmentAllocation.tickerName
                )
            ) {
                val marketPrice =
                    BigDecimal(tickerPrices[investmentAllocation.tickerName])
                createInvestmentScreenUpdatedInvestmentShareInfo(
                    amountToInvest,
                    marketPrice
                )
            } else null
        )
    }

    private suspend fun updateInvestmentValues(investmentUiValues: List<InvestmentScreenUpdatedInvestmentValue>): List<InvestmentAllocation> {
        // This can be more efficient. Can store updated values in hashmap and only update changed values
        // List is likely to be quite short and performance not as large a concern for the moment
        val dbInvestments = investmentRepository.getInvestments()
        val updatedDbInvestments = dbInvestments.map { dbInvestment ->
            val updatedValue =
                investmentUiValues.firstOrNull { uiValue -> uiValue.id == dbInvestment.id }
            if (updatedValue != null) {
                val updatedAmount = updatedValue.amountToInvest.rawCurrencyInputToBigDecimal()
                    .plus(dbInvestment.currentInvestedAmount)
                return@map dbInvestment.copy(currentInvestedAmount = updatedAmount)
            } else {
                return@map dbInvestment
            }
        }
        updatedDbInvestments.forEach {
            investmentRepository.updateInvestment(it)
        }

        return updatedDbInvestments
    }

    private fun createInvestmentScreenUpdatedInvestmentShareInfo(
        calculatedInvestAmt: BigDecimal,
        marketPrice: BigDecimal
    ): InvestmentScreenUpdatedInvestmentShareInfo {
        return InvestmentScreenUpdatedInvestmentShareInfo(
            sharesToBuy = investmentCalculationStrategy.calculateSharesToBuy(
                calculatedInvestAmt,
                marketPrice
            ),
            marketPrice = marketPrice.bigDecimalToUiFormattedCurrency()
        )
    }
}

sealed class InvestResultScreenUiState {
    data object Loading : InvestResultScreenUiState()

    data class CalculationComplete(
        val amountToInvest: String,
        val investments: ImmutableList<InvestmentScreenUpdatedInvestmentValue>,
    ) : InvestResultScreenUiState()

    data object InvestmentsSaved : InvestResultScreenUiState()
}

data class InvestmentScreenUpdatedInvestmentValue(
    val id: Int,
    val investmentName: String,
    val amountToInvest: String,
    val shareInfo: InvestmentScreenUpdatedInvestmentShareInfo? = null
)

data class InvestmentScreenUpdatedInvestmentShareInfo(
    val sharesToBuy: Int, val marketPrice: String
)
package com.cjapps.prop.ui.invest.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.prop.data.IInvestmentRepository
import com.cjapps.prop.data.calculation.InvestmentCalculationStrategyFactory
import com.cjapps.prop.ui.extensions.bigDecimalToUiFormattedCurrency
import com.cjapps.prop.ui.extensions.rawCurrencyInputToBigDecimal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvestResultSummaryViewModel @Inject constructor(
    private val investmentRepository: IInvestmentRepository,
    private val investmentStrategyFactory: InvestmentCalculationStrategyFactory,
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
                investmentValues,
                amountToInvest
            )
            uiStateFlow.update {
                InvestResultScreenUiState.CalculationComplete(
                    amountToInvest = amountToInvest.bigDecimalToUiFormattedCurrency(),
                    investments = calculatedInvestments.map { entry ->
                        val investment = entry.key
                        InvestmentScreenUpdatedInvestmentValue(
                            id = investment.id ?: -1,
                            investmentName = investment.tickerName,
                            amountToInvest = entry.value.bigDecimalToUiFormattedCurrency()
                        )
                    }.toImmutableList()
                )
            }
        }
    }
}

sealed class InvestResultScreenUiState {
    data object Loading : InvestResultScreenUiState()

    data class CalculationComplete(
        val amountToInvest: String,
        val investments: ImmutableList<InvestmentScreenUpdatedInvestmentValue>,
    ) : InvestResultScreenUiState()
}

data class InvestmentScreenUpdatedInvestmentValue(
    val id: Int, val investmentName: String, val amountToInvest: String
)
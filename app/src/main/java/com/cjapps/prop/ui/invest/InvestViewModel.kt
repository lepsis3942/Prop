package com.cjapps.prop.ui.invest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.prop.data.IInvestmentRepository
import com.cjapps.prop.data.calculation.InvestmentCalculationStrategyFactory
import com.cjapps.prop.models.InvestmentAllocation
import com.cjapps.prop.ui.extensions.bigDecimalToRawCurrency
import com.cjapps.prop.ui.extensions.bigDecimalToUiFormattedCurrency
import com.cjapps.prop.ui.extensions.rawCurrencyInputToBigDecimal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvestViewModel @Inject constructor(
    private val investmentRepository: IInvestmentRepository,
    private val investmentStrategyFactory: InvestmentCalculationStrategyFactory
) : ViewModel() {
    private val uiStateFlow = MutableStateFlow<InvestScreenUiState>(
        InvestScreenUiState.Loading
    )
    private val investmentCalculationStrategy get() = investmentStrategyFactory.getInvestmentCalculationStrategy()

    val uiState: MutableStateFlow<InvestScreenUiState> get() = uiStateFlow

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val investmentUiValues = investmentRepository.getInvestments().map { investment ->
                InvestmentScreenCurrentInvestmentValue(
                    id = investment.id ?: -1,
                    investmentName = investment.tickerName,
                    investmentValue = investment.currentInvestedAmount.bigDecimalToRawCurrency()
                )
            }
            uiStateFlow.update {
                InvestScreenUiState.AdjustingValues(
                    amountToInvest = "", investments = investmentUiValues
                )
            }
        }
    }

    fun updateAmountToInvest(amountToInvest: String) {
        uiStateFlow.update { uiState ->
            if (uiState !is InvestScreenUiState.AdjustingValues) {
                return@update uiState
            }
            uiState.copy(amountToInvest = amountToInvest)
        }
    }

    fun updateInvestmentCurrentAmount(id: Int, updatedValue: String) {
        uiStateFlow.update { uiState ->
            if (uiState !is InvestScreenUiState.AdjustingValues) {
                return@update uiState
            }

            uiState.copy(investments = uiState.investments.map {
                if (it.id == id) {
                    InvestmentScreenCurrentInvestmentValue(
                        it.id, investmentName = it.investmentName, investmentValue = updatedValue
                    )
                } else it
            })
        }
    }

    fun investTapped() {
        viewModelScope.launch {
            val uiState = uiStateFlow.value
            if (uiState !is InvestScreenUiState.AdjustingValues) {
                return@launch
            }
            val updatedUiInvestments = uiState.investments
            val amountToInvestBigDecimal = uiState.amountToInvest.rawCurrencyInputToBigDecimal()
            val updatedInvestmentAllocations = updateInvestmentValues(updatedUiInvestments)
            val calculatedInvestments = investmentCalculationStrategy.calculatePurchaseAmounts(
                updatedInvestmentAllocations,
                amountToInvestBigDecimal
            )
            uiStateFlow.update {
                InvestScreenUiState.CalculationComplete(
                    amountToInvest = amountToInvestBigDecimal.bigDecimalToUiFormattedCurrency(),
                    investments = calculatedInvestments.map { entry ->
                        val investment = entry.key
                        InvestmentScreenUpdatedInvestmentValue(
                            id = investment.id ?: -1,
                            investmentName = investment.tickerName,
                            amountToInvest = entry.value.bigDecimalToUiFormattedCurrency()
                        )
                    }
                )

            }
        }
    }

    private suspend fun updateInvestmentValues(investmentUiValues: List<InvestmentScreenCurrentInvestmentValue>): List<InvestmentAllocation> {
        // This can be more efficient. Can store updated values in hashmap and only update changed values
        // List is likely to be quite short and performance not as large a concern for the moment
        val dbInvestments = investmentRepository.getInvestments()
        val updatedDbInvestments = dbInvestments.map { dbInvestment ->
            val updatedValue =
                investmentUiValues.firstOrNull { uiValue -> uiValue.id == dbInvestment.id }
            if (updatedValue != null) {
                return@map dbInvestment.copy(currentInvestedAmount = updatedValue.investmentValue.rawCurrencyInputToBigDecimal())
            } else {
                return@map dbInvestment
            }
        }
        updatedDbInvestments.forEach {
            investmentRepository.updateInvestment(it)
        }

        return updatedDbInvestments
    }
}

sealed class InvestScreenUiState {
    data object Loading : InvestScreenUiState()
    data class AdjustingValues(
        val amountToInvest: String, val investments: List<InvestmentScreenCurrentInvestmentValue>
    ) : InvestScreenUiState()

    data class CalculationComplete(
        val amountToInvest: String, val investments: List<InvestmentScreenUpdatedInvestmentValue>
    ) : InvestScreenUiState()
}

data class InvestmentScreenCurrentInvestmentValue(
    val id: Int, val investmentName: String, val investmentValue: String
)

data class InvestmentScreenUpdatedInvestmentValue(
    val id: Int, val investmentName: String, val amountToInvest: String
)
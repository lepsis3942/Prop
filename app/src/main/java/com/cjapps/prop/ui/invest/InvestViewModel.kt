package com.cjapps.prop.ui.invest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.prop.data.IInvestmentRepository
import com.cjapps.prop.models.InvestmentAllocation
import com.cjapps.prop.ui.extensions.bigDecimalToRawCurrency
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
class InvestViewModel @Inject constructor(
    private val investmentRepository: IInvestmentRepository,
) : ViewModel() {
    private val uiStateFlow = MutableStateFlow<InvestScreenUiState>(
        InvestScreenUiState.Loading
    )
    private var amountToInvest: String = ""
    private var investmentAmounts: List<InvestmentScreenCurrentInvestmentValue> = emptyList()

    val uiState: MutableStateFlow<InvestScreenUiState> get() = uiStateFlow

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            investmentRepository.getInvestmentsAsFlow().collect {
                val investmentUiValues = it.map { investment ->
                    InvestmentScreenCurrentInvestmentValue(
                        id = investment.id ?: -1,
                        investmentName = investment.tickerName,
                        investmentValue = investment.currentInvestedAmount.bigDecimalToRawCurrency()
                    )
                }
                investmentAmounts = investmentUiValues
                uiStateFlow.update {
                    InvestScreenUiState.AdjustingValues(
                        amountToInvest = amountToInvest,
                        investments = investmentUiValues.toImmutableList(),
                        investEnabled = isInvestEnabled()
                    )
                }
            }
        }
    }

    fun updateAmountToInvest(newAmountToInvest: String) {
        uiStateFlow.update { uiState ->
            if (uiState !is InvestScreenUiState.AdjustingValues) {
                // This shouldn't happen under normal circumstances
                return@update uiState
            }
            amountToInvest = newAmountToInvest
            uiState.copy(amountToInvest = newAmountToInvest, investEnabled = isInvestEnabled())
        }
    }

    fun updateInvestmentCurrentAmount(id: Int, updatedValue: String) {
        uiStateFlow.update { uiState ->
            if (uiState !is InvestScreenUiState.AdjustingValues) {
                return@update uiState
            }

            val updatedInvestments = uiState.investments.map {
                if (it.id == id) {
                    InvestmentScreenCurrentInvestmentValue(
                        it.id,
                        investmentName = it.investmentName,
                        investmentValue = updatedValue
                    )
                } else it
            }
            investmentAmounts = updatedInvestments
            uiState.copy(investments = updatedInvestments.toImmutableList())
        }
    }

    fun investTapped() {
        uiStateFlow.update {
            InvestScreenUiState.InvestRequested(amountToInvest = amountToInvest)
        }
    }

    fun navigationComplete() {
        uiStateFlow.update {
            InvestScreenUiState.AdjustingValues(
                amountToInvest = amountToInvest,
                investments = investmentAmounts.toImmutableList(),
                investEnabled = isInvestEnabled()
            )
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

    private fun isInvestEnabled(): Boolean {
        val amountToInvest = amountToInvest.rawCurrencyInputToBigDecimal()
        return amountToInvest.compareTo(BigDecimal.ZERO) > 0
    }
}

sealed class InvestScreenUiState {
    data object Loading : InvestScreenUiState()

    data class AdjustingValues(
        val amountToInvest: String,
        val investments: ImmutableList<InvestmentScreenCurrentInvestmentValue>,
        val investEnabled: Boolean
    ) : InvestScreenUiState()

    data class InvestRequested(val amountToInvest: String) : InvestScreenUiState()
}

data class InvestmentScreenCurrentInvestmentValue(
    val id: Int, val investmentName: String, val investmentValue: String
)
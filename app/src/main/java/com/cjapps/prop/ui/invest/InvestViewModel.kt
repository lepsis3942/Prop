package com.cjapps.prop.ui.invest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.prop.data.IInvestmentRepository
import com.cjapps.prop.ui.extensions.bigDecimalToRawCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvestViewModel @Inject constructor(
    private val investmentRepository: IInvestmentRepository
) : ViewModel() {
    private val uiStateFlow = MutableStateFlow(
        InvestScreenUiState(
            isLoading = true,
            amountToInvest = "",
            investments = listOf()
        )
    )

    val uiState: MutableStateFlow<InvestScreenUiState> get() = uiStateFlow

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            investmentRepository.getInvestments().collect { investments ->
                val investmentUiValues = investments.map { investment ->
                    InvestmentScreenCurrentInvestmentValue(
                        id = investment.id ?: -1,
                        investmentName = investment.tickerName,
                        investmentValue = investment.currentInvestedAmount.bigDecimalToRawCurrency()
                    )
                }
                uiStateFlow.update { uiState ->
                    uiState.copy(isLoading = false, investments = investmentUiValues)
                }
            }
        }
    }

    fun updateAmountToInvest(amountToInvest: String) {
        uiStateFlow.update { uiState ->
            uiState.copy(amountToInvest = amountToInvest)
        }
    }

    fun updateInvestmentCurrentAmount(id: Int, updatedValue: String) {
        uiStateFlow.update { uiState ->
            uiState.copy(investments = uiState.investments.map {
                if (it.id == id) {
                    InvestmentScreenCurrentInvestmentValue(
                        it.id,
                        investmentName = it.investmentName,
                        investmentValue = updatedValue
                    )
                } else it
            })
        }
    }
}

data class InvestScreenUiState(
    val isLoading: Boolean,
    val amountToInvest: String,
    val investments: List<InvestmentScreenCurrentInvestmentValue>
)

data class InvestmentScreenCurrentInvestmentValue(
    val id: Int,
    val investmentName: String,
    val investmentValue: String
)
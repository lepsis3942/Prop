package com.cjapps.prop.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.data.IInvestmentRepository
import com.cjapps.prop.models.InvestmentAllocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class InvestmentSummaryViewModel @Inject constructor(
    private val dispatcherProvider: IDispatcherProvider,
    private val investmentRepository: IInvestmentRepository,
) : ViewModel() {
    private val uiStateFlow = MutableStateFlow(
        HomeScreenUiState(
            isLoading = true,
            investmentAllocations = listOf(),
            totalForAllInvestments = BigDecimal.ZERO
        )
    )

    init {
        retrieveInvestments()
    }

    val uiState: MutableStateFlow<HomeScreenUiState> get() = uiStateFlow

    fun onInvestTapped() {
        viewModelScope.launch {
            investmentRepository.addInvestment(
                InvestmentAllocation(
                    tickerName = "MSFT",
                    desiredPercentage = BigDecimal("8"),
                    currentInvestedAmount = BigDecimal("230.11")
                )
            )
        }
    }

    private fun retrieveInvestments() {
        viewModelScope.launch {
            investmentRepository.getInvestments().collect { investments ->
                uiStateFlow.update { uiState ->
                    uiState.copy(
                        isLoading = false,
                        investmentAllocations = investments.sortedByDescending { it.desiredPercentage },
                        totalForAllInvestments = calculateInvestmentTotal(investments)
                    )
                }
            }
        }
    }

    private fun calculateInvestmentTotal(investmentAllocations: List<InvestmentAllocation>): BigDecimal =
        investmentAllocations.fold(BigDecimal.ZERO) { total, item -> total + item.currentInvestedAmount }
}

data class HomeScreenUiState(
    val isLoading: Boolean,
    val investmentAllocations: List<InvestmentAllocation>,
    val totalForAllInvestments: BigDecimal
)
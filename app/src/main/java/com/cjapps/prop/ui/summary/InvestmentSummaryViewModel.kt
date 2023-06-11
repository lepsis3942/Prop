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

    fun onAddInvestmentTapped() {
        viewModelScope.launch {
            investmentRepository.addInvestment(
                InvestmentAllocation(
                    "MSFT",
                    BigDecimal("0.08"),
                    BigDecimal("230.11")
                )
            )
            // TODO: the repo will eventually be flows so manually updating will be unnecessary
            retrieveInvestments()
        }
    }

    fun onInvestTapped() {

    }

    private fun retrieveInvestments() {
        viewModelScope.launch {
            val investments = investmentRepository.getInvestments()
                .sortedByDescending { item -> item.currentInvestedAmount }
            uiStateFlow.update {
                it.copy(
                    isLoading = false,
                    investmentAllocations = investments,
                    totalForAllInvestments = calculateInvestmentTotal(investments)
                )
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
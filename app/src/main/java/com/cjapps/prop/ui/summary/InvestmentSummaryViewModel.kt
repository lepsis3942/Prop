package com.cjapps.prop.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.prop.data.IInvestmentRepository
import com.cjapps.prop.models.InvestmentAllocation
import com.cjapps.prop.ui.extensions.isNumericalValueEqualTo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class InvestmentSummaryViewModel @Inject constructor(
    private val investmentRepository: IInvestmentRepository,
) : ViewModel() {
    private val uiStateFlow = MutableStateFlow(
        HomeScreenUiState(
            isLoading = true,
            isInvestButtonEnabled = false,
            investmentAllocations = listOf(),
            totalForAllInvestments = BigDecimal.ZERO,
            totalAllocatedPercent = 0
        )
    )

    init {
        retrieveInvestments()
    }

    val uiState: MutableStateFlow<HomeScreenUiState> get() = uiStateFlow

    private fun retrieveInvestments() {
        viewModelScope.launch {
            investmentRepository.getInvestments().collect { investments ->
                val desiredAllocationPercentageSum =
                    investments.fold(BigDecimal.ZERO) { total, item ->
                        total + item.desiredPercentage
                    }
                uiStateFlow.update { uiState ->
                    uiState.copy(
                        isLoading = false,
                        isInvestButtonEnabled = desiredAllocationPercentageSum.isNumericalValueEqualTo(
                            BigDecimal(100)
                        ),
                        investmentAllocations = investments.sortedByDescending { it.desiredPercentage },
                        totalForAllInvestments = calculateInvestmentTotal(investments),
                        totalAllocatedPercent = calculatePercentageTotal(investments)
                    )
                }
            }
        }
    }

    private fun calculateInvestmentTotal(investmentAllocations: List<InvestmentAllocation>): BigDecimal =
        investmentAllocations.fold(BigDecimal.ZERO) { total, item -> total + item.currentInvestedAmount }

    private fun calculatePercentageTotal(investmentAllocations: List<InvestmentAllocation>): Int =
        investmentAllocations.fold(0) { total, item -> total + item.desiredPercentage.toInt() }
}

data class HomeScreenUiState(
    val isLoading: Boolean,
    val isInvestButtonEnabled: Boolean,
    val investmentAllocations: List<InvestmentAllocation>,
    val totalForAllInvestments: BigDecimal,
    val totalAllocatedPercent: Int
)
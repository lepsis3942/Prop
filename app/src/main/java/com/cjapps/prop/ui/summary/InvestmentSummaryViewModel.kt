package com.cjapps.prop.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.prop.BuildConfig
import com.cjapps.prop.data.IInvestmentRepository
import com.cjapps.prop.data.IPropRepository
import com.cjapps.prop.models.InvestmentAllocation
import com.cjapps.prop.ui.extensions.isNumericalValueEqualTo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class InvestmentSummaryViewModel @Inject constructor(
    private val investmentRepository: IInvestmentRepository,
    private val propRepository: IPropRepository,
) : ViewModel() {
    private val uiStateFlow = MutableStateFlow(
        HomeScreenUiState(
            isLoading = true,
            isInvestButtonEnabled = false,
            isAddStockButtonEnabled = false,
            appMeetsBuildRequirements = true,
            investmentAllocations = persistentListOf(),
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
            val appData = propRepository.getAppData()
            val appMeetsBuildRequirements = appData.minimumBuildNumber <= BuildConfig.VERSION_CODE
            investmentRepository.getInvestmentsAsFlow().collect { investments ->
                val desiredAllocationPercentageSum =
                    investments.fold(BigDecimal.ZERO) { total, item ->
                        total + item.desiredPercentage
                    }
                val currentAllocatedPercentage = calculatePercentageTotal(investments)
                uiStateFlow.update { uiState ->
                    uiState.copy(
                        isLoading = false,
                        isInvestButtonEnabled = desiredAllocationPercentageSum.isNumericalValueEqualTo(
                            BigDecimal(100)
                        ),
                        appMeetsBuildRequirements = appMeetsBuildRequirements,
                        isAddStockButtonEnabled = currentAllocatedPercentage < 100,
                        investmentAllocations = investments.sortedByDescending { it.desiredPercentage }
                            .toImmutableList(),
                        totalForAllInvestments = calculateInvestmentTotal(investments),
                        totalAllocatedPercent = currentAllocatedPercentage
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
    val isAddStockButtonEnabled: Boolean,
    val appMeetsBuildRequirements: Boolean = true,
    val investmentAllocations: ImmutableList<InvestmentAllocation>,
    val totalForAllInvestments: BigDecimal,
    val totalAllocatedPercent: Int
)
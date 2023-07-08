package com.cjapps.prop.ui.detail

import androidx.lifecycle.ViewModel
import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.data.IInvestmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class InvestmentDetailViewModel @Inject constructor(
    private val dispatcherProvider: IDispatcherProvider,
    private val investmentRepository: IInvestmentRepository,
) : ViewModel() {
    private val uiStateFlow = MutableStateFlow(
        InvestmentDetailUiState(
            isLoading = true,
            tickerName = "",
            currentInvestmentValue = BigDecimal.ZERO,
            currentPercentageToInvest = 0,
            availablePercentageToInvest = 0
        )
    )

    val uiState: MutableStateFlow<InvestmentDetailUiState> get() = uiStateFlow

    fun updatePercentageToInvest(newPercentage: Int) {
        uiStateFlow.update {
            it.copy(currentPercentageToInvest = newPercentage)
        }
    }
}

data class InvestmentDetailUiState(
    val isLoading: Boolean,
    val tickerName: String,
    val currentInvestmentValue: BigDecimal,
    val currentPercentageToInvest: Int,
    val availablePercentageToInvest: Int
)
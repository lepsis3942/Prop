package com.cjapps.prop.ui.detail

import androidx.lifecycle.ViewModel
import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.data.IInvestmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
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
            currentInvestmentValue = "",
            currentPercentageToInvest = 0,
            availablePercentageToInvest = 0
        )
    )
    private var currentInvestmentValue = BigDecimal.ZERO

    val uiState: MutableStateFlow<InvestmentDetailUiState> get() = uiStateFlow

    fun updateCurrentValue(newValue: String) {
        val decimalFormat = NumberFormat.getInstance() as DecimalFormat
        decimalFormat.isParseBigDecimal = true
        decimalFormat.maximumFractionDigits = 2
        var parsedDecimal = currentInvestmentValue
        try {
            parsedDecimal = decimalFormat.parse(newValue) as BigDecimal
        } catch (_: Exception) {
        }

        uiStateFlow.update {
            it.copy(currentInvestmentValue = decimalFormat.format(parsedDecimal))
        }
    }

    fun updatePercentageToInvest(newPercentage: Int) {
        uiStateFlow.update {
            it.copy(currentPercentageToInvest = newPercentage)
        }
    }
}

data class InvestmentDetailUiState(
    val isLoading: Boolean,
    val tickerName: String,
    val currentInvestmentValue: String,
    val currentPercentageToInvest: Int,
    val availablePercentageToInvest: Int
)
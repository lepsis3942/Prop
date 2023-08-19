package com.cjapps.prop.ui.detail

import androidx.lifecycle.ViewModel
import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.data.IInvestmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            currentInvestmentValue = "",
            currentPercentageToInvest = 0,
            availablePercentageToInvest = 0
        )
    )

    val uiState: StateFlow<InvestmentDetailUiState> get() = uiStateFlow

    fun updateTickerName(newValue: String) {
        uiStateFlow.update {
            it.copy(tickerName = newValue)
        }
    }

    fun updateCurrentValue(newValue: String) {
        // Only allow numbers and block leading zeros
        val cleanedStr = newValue
            .filter { c -> c.isDigit() }
            .dropWhile { c -> c == '0' }

        uiStateFlow.update {
            it.copy(currentInvestmentValue = cleanedStr)
        }
    }

    fun updatePercentageToInvest(newPercentage: Int) {
        uiStateFlow.update {
            it.copy(currentPercentageToInvest = newPercentage)
        }
    }

    private suspend fun saveInvestmentAllocation() {

    }

    /**
     * Convert raw input to a BigDecimal. Input will format currency as a series of numbers:
     * EX: 78023.91 is represented as 7802391
     * EX: 101.00 is represented as 10100
     * EX: 0.01 is represented as 1
     */
    fun rawCurrencyInputToBigDecimal(rawString: String): BigDecimal {
        var formattedInput = rawString
        if (formattedInput.length < 2) {
            formattedInput = formattedInput.padStart(2, '0')
        }
        if (formattedInput.length == 2) {
            formattedInput = "0$formattedInput"
        }

        formattedInput =
            "${formattedInput.substring(0..formattedInput.length - 3)}.${formattedInput.takeLast(2)}"

        return BigDecimal(formattedInput)
    }
}

data class InvestmentDetailUiState(
    val isLoading: Boolean,
    val tickerName: String,
    val currentInvestmentValue: String,
    val currentPercentageToInvest: Int,
    val availablePercentageToInvest: Int
)
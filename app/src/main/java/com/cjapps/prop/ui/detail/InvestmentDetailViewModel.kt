package com.cjapps.prop.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.data.IInvestmentRepository
import com.cjapps.prop.data.exceptions.DuplicateRecordException
import com.cjapps.prop.data.exceptions.NoEntityFoundException
import com.cjapps.prop.models.InvestmentAllocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class InvestmentDetailViewModel @Inject constructor(
    private val dispatcherProvider: IDispatcherProvider,
    private val savedStateHandle: SavedStateHandle,
    private val investmentRepository: IInvestmentRepository,
) : ViewModel() {
    private val uiStateFlow = MutableStateFlow(
        InvestmentDetailUiState(
            isLoading = true,
            isSaveEnabled = false,
            isUpdateMode = false,
            tickerName = "",
            currentInvestmentValue = "",
            currentPercentageToInvest = 0,
            availablePercentageToInvest = 0
        )
    )
    private var investmentIdToUpdate: Int? = null

    val uiState: StateFlow<InvestmentDetailUiState> get() = uiStateFlow

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            investmentIdToUpdate = savedStateHandle.get<Int>("investmentId")
            updateUiState(
                uiStateFlow.value.copy(
                    isUpdateMode = investmentIdToUpdate != null
                )
            )
            val allocationUpdateId = investmentIdToUpdate // immutable prop for block logic
            investmentRepository.getInvestments().collect { investments ->
                val availablePercentageToInvest =
                    100 - investments.fold(BigDecimal.ZERO) { total, item ->
                        total + item.desiredPercentage
                    }.toInt()

                if (allocationUpdateId == null) {
                    updateUiState(
                        uiStateFlow.value.copy(
                            isLoading = false,
                            availablePercentageToInvest = availablePercentageToInvest
                        )
                    )
                } else {
                    val investment =
                        investments.firstOrNull { it.id == allocationUpdateId.toInt() }
                            ?: return@collect
                    updateUiState(
                        uiStateFlow.value.copy(
                            isLoading = false,
                            availablePercentageToInvest = availablePercentageToInvest,
                            tickerName = investment.tickerName,
                            currentInvestmentValue = bigDecimalToRawCurrency(investment.currentInvestedAmount),
                            currentPercentageToInvest = investment.desiredPercentage.toInt()
                        )
                    )
                }
            }
        }
    }

    fun updateTickerName(newValue: String) {
        updateUiState(
            uiStateFlow.value.copy(tickerName = newValue)
        )
    }

    fun updateCurrentValue(newValue: String) {
        // Only allow numbers and block leading zeros
        val cleanedStr = newValue
            .filter { c -> c.isDigit() }
            .dropWhile { c -> c == '0' }

        updateUiState(
            uiStateFlow.value.copy(currentInvestmentValue = cleanedStr)
        )
    }

    fun updatePercentageToInvest(newPercentage: Int) {
        updateUiState(
            uiStateFlow.value.copy(currentPercentageToInvest = newPercentage)
        )
    }

    fun errorDialogConfirmed() {
        updateUiState(
            uiStateFlow.value.copy(errorState = null)
        )
    }

    fun saveInvestmentAllocation(navigateHome: () -> Unit) {
        viewModelScope.launch {
            updateUiState(
                uiStateFlow.value.copy(isLoading = true, errorState = null)
            )
            val uiState = uiState.value
            val allocationToSave = InvestmentAllocation(
                id = investmentIdToUpdate,  // if create flow this will be null
                tickerName = uiState.tickerName,
                currentInvestedAmount = rawCurrencyInputToBigDecimal(uiState.currentInvestmentValue),
                desiredPercentage = BigDecimal(uiState.currentPercentageToInvest)
            )
            val saveResult: Result<Unit> = if (uiState.isUpdateMode) {
                val id = investmentIdToUpdate
                if (id == null) {
                    updateUiState(
                        uiStateFlow.value.copy(
                            isLoading = false,
                            errorState = ErrorUiState.UnknownError
                        )
                    )
                    return@launch
                }
                investmentRepository.updateInvestment(allocationToSave)
            } else {
                investmentRepository.addInvestment(allocationToSave)
            }

            if (saveResult.isSuccess) {
                navigateHome()
                return@launch
            }

            val error = when (saveResult.exceptionOrNull()) {
                is DuplicateRecordException -> ErrorUiState.DuplicateTickerError
                is NoEntityFoundException -> ErrorUiState.NoAllocationFoundError
                else -> ErrorUiState.UnknownError
            }
            updateUiState(
                uiStateFlow.value.copy(isLoading = false, errorState = error)
            )
        }
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

    fun bigDecimalToRawCurrency(decimal: BigDecimal): String {
        val splitDecimal = decimal.divideAndRemainder(BigDecimal.ONE)
        val intPart = splitDecimal[0].toInt().toString()
        val fractionalPart = splitDecimal[1].toString()
            .drop(2)
            .take(2)
            .padEnd(2, '0')
        return intPart + fractionalPart
    }

    private fun updateUiState(newUiState: InvestmentDetailUiState) {
        uiStateFlow.update {
            newUiState.copy(
                isSaveEnabled = newUiState.tickerName.isNotBlank()
                        && newUiState.currentPercentageToInvest != 0
            )
        }
    }
}

data class InvestmentDetailUiState(
    val isLoading: Boolean,
    val isSaveEnabled: Boolean,
    val isUpdateMode: Boolean,
    val tickerName: String,
    val currentInvestmentValue: String,
    val currentPercentageToInvest: Int,
    val availablePercentageToInvest: Int,
    val errorState: ErrorUiState? = null
)

sealed class ErrorUiState {
    data object DuplicateTickerError : ErrorUiState()
    data object NoAllocationFoundError : ErrorUiState()
    data object UnknownError : ErrorUiState()
}
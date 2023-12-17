package com.cjapps.prop.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.data.IInvestmentRepository
import com.cjapps.prop.data.exceptions.DuplicateRecordException
import com.cjapps.prop.data.exceptions.NoEntityFoundException
import com.cjapps.prop.models.InvestmentAllocation
import com.cjapps.prop.ui.extensions.bigDecimalToRawCurrency
import com.cjapps.prop.ui.extensions.rawCurrencyInputToBigDecimal
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
            availablePercentageToInvest = 0,
            saveCompleted = null,
            deleteCompleted = null,
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
                var availablePercentageToInvest =
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
                    // If update mode allow increases to the max available but also allow reducing
                    // Without this a desired percentage > available left will error out
                    availablePercentageToInvest += investment.desiredPercentage.toInt()
                    updateUiState(
                        uiStateFlow.value.copy(
                            isLoading = false,
                            availablePercentageToInvest = availablePercentageToInvest,
                            tickerName = investment.tickerName,
                            currentInvestmentValue = investment.currentInvestedAmount.bigDecimalToRawCurrency(),
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

    fun deleteInvestment() {
        viewModelScope.launch {
            val uiState = uiState.value
            val allocationToDelete = InvestmentAllocation(
                id = investmentIdToUpdate,
                tickerName = uiState.tickerName,
                currentInvestedAmount = uiState.currentInvestmentValue.rawCurrencyInputToBigDecimal(),
                desiredPercentage = BigDecimal(uiState.currentPercentageToInvest)
            )

            val result = investmentRepository.deleteInvestment(allocationToDelete)

            if (result.isSuccess) {
                updateUiState(
                    uiStateFlow.value.copy(
                        isLoading = false,
                        errorState = null,
                        deleteCompleted = true,
                    )
                )
                return@launch
            }

            val error = when (result.exceptionOrNull()) {
                is DuplicateRecordException -> ErrorUiState.DuplicateTickerError
                is NoEntityFoundException -> ErrorUiState.NoAllocationFoundError
                else -> ErrorUiState.UnknownError
            }
            updateUiState(
                uiStateFlow.value.copy(isLoading = false, errorState = error)
            )
        }
    }

    fun saveInvestmentAllocation() {
        viewModelScope.launch {
            val uiState = uiState.value
            val allocationToSave = InvestmentAllocation(
                id = investmentIdToUpdate,  // if create flow this will be null
                tickerName = uiState.tickerName,
                currentInvestedAmount = uiState.currentInvestmentValue.rawCurrencyInputToBigDecimal(),
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
                updateUiState(
                    uiStateFlow.value.copy(
                        isLoading = false,
                        errorState = null,
                        saveCompleted = true,
                    )
                )
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
    val errorState: ErrorUiState? = null,
    val saveCompleted: Boolean?,
    val deleteCompleted: Boolean?
)

sealed class ErrorUiState {
    data object DuplicateTickerError : ErrorUiState()
    data object NoAllocationFoundError : ErrorUiState()
    data object UnknownError : ErrorUiState()
}
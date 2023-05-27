package com.cjapps.prop

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.cjapps.prop.models.InvestmentAllocation
import java.math.BigDecimal

class InvestmentSummaryViewModel : ViewModel() {
    private val _investmentAllocations = listOf(
        InvestmentAllocation("SCHB", BigDecimal("0.23"), BigDecimal("54797.12")),
        InvestmentAllocation("SCHC", BigDecimal("0.54"), BigDecimal("12000.67")),
        InvestmentAllocation("TSLA", BigDecimal("0.12"), BigDecimal("3867.74")),
        InvestmentAllocation("MSFT", BigDecimal("0.08"), BigDecimal("230.11")),
        InvestmentAllocation("V", BigDecimal("0.03"), BigDecimal("230.11")),
    ).sortedByDescending { item -> item.currentInvestedAmount }.toMutableStateList()

    val investmentAllocations: List<InvestmentAllocation> get() = _investmentAllocations

    val totalForAllInvestments: BigDecimal get() = _investmentAllocations.fold(BigDecimal.ZERO)
    { total, item -> total + item.currentInvestedAmount }

    fun onAddInvestmentTapped() {
        _investmentAllocations.add(
            InvestmentAllocation(
                "SCHB",
                BigDecimal("0.23"),
                BigDecimal("5497.12")
            )
        )
    }
}
package com.cjapps.prop.data.calculation

import javax.inject.Inject

class InvestmentCalculationStrategyFactory @Inject constructor() {

    // Plan is for there to be different algorithms in the future depending on how the user wants
    // overages and such handled
    fun getInvestmentCalculationStrategy(): IInvestmentCalculationStrategy {
        return DefaultInvestmentCalculationStrategy()
    }
}
package com.cjapps.prop.data

import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.data.database.InvestmentAllocationDao
import com.cjapps.prop.models.InvestmentAllocation
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InvestmentRepository @Inject constructor(
    private val dispatcherProvider: IDispatcherProvider,
    private val investmentAllocationDao: InvestmentAllocationDao
) : IInvestmentRepository {
    private val investments: MutableList<InvestmentAllocation> =
        listOf<InvestmentAllocation>().toMutableList()

    override suspend fun getInvestments(): List<InvestmentAllocation> {
        // TODO: return a flow here
        // see whileSubscribed -> https://medium.com/androiddevelopers/things-to-know-about-flows-sharein-and-statein-operators-20e6ccb2bc74

        return withContext(dispatcherProvider.IO) {
            delay(2000L)

//            listOf(
//                InvestmentAllocation("SCHB", BigDecimal("0.23"), BigDecimal("54797.12")),
//                InvestmentAllocation("SCHC", BigDecimal("0.54"), BigDecimal("12000.67")),
//                InvestmentAllocation("TSLA", BigDecimal("0.12"), BigDecimal("3867.74")),
//                InvestmentAllocation("MSFT", BigDecimal("0.08"), BigDecimal("230.11")),
//                InvestmentAllocation("V", BigDecimal("0.03"), BigDecimal("230.11")),
//            )
            investments
        }
    }

    override suspend fun addInvestment(investment: InvestmentAllocation) {
        investments.add(investment)
    }


}
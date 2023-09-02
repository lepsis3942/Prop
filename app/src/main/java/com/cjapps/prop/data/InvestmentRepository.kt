package com.cjapps.prop.data

import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.data.database.InvestmentAllocationDao
import com.cjapps.prop.data.mappers.IDaoMapper
import com.cjapps.prop.models.InvestmentAllocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InvestmentRepository @Inject constructor(
    private val dispatcherProvider: IDispatcherProvider,
    private val investmentAllocationDao: InvestmentAllocationDao,
    private val daoMapper: IDaoMapper
) : IInvestmentRepository {

    override fun getInvestments(): Flow<List<InvestmentAllocation>> {
        return investmentAllocationDao.getAll().map { daoList ->
            daoList.map { dao -> daoMapper.entityToInvestmentAllocation(dao) }
        }
    }

    override suspend fun addInvestment(investment: InvestmentAllocation) {
        investmentAllocationDao.insert(daoMapper.investmentAllocationToEntity(investment))
    }
}
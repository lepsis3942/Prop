package com.cjapps.prop.data

import com.cjapps.prop.data.database.InvestmentAllocationDao
import com.cjapps.prop.data.exceptions.DuplicateRecordException
import com.cjapps.prop.data.mappers.IDaoMapper
import com.cjapps.prop.models.InvestmentAllocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InvestmentRepository @Inject constructor(
    private val investmentAllocationDao: InvestmentAllocationDao,
    private val daoMapper: IDaoMapper
) : IInvestmentRepository {

    override fun getInvestments(): Flow<List<InvestmentAllocation>> {
        return investmentAllocationDao.getAll().map { daoList ->
            daoList.map { dao -> daoMapper.entityToInvestmentAllocation(dao) }
        }
    }

    override suspend fun addInvestment(investment: InvestmentAllocation): Result<Unit> {
        val existingRecords = investmentAllocationDao.getAllByTickerName(investment.tickerName)
        if (existingRecords.isNotEmpty()) {
            return Result.failure(DuplicateRecordException())
        }

        investmentAllocationDao.insert(daoMapper.investmentAllocationToEntity(investment))
        return Result.success(Unit)
    }
}
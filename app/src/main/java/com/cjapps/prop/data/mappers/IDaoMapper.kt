package com.cjapps.prop.data.mappers

import com.cjapps.prop.models.InvestmentAllocation
import com.cjapps.prop.data.database.InvestmentAllocation as EntityInvestmentAllocation

interface IDaoMapper {
    fun investmentAllocationToEntity(investmentAllocation: InvestmentAllocation): EntityInvestmentAllocation

    fun entityToInvestmentAllocation(investmentAllocationEntity: EntityInvestmentAllocation): InvestmentAllocation
}
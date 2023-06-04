package com.cjapps.prop.di

import com.cjapps.prop.DispatcherProvider
import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.data.IInvestmentRepository
import com.cjapps.prop.data.InvestmentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent


@Module
@InstallIn(ViewModelComponent::class)
abstract class AppModule {
    @Binds
    abstract fun bindDispatcherProvider(
        dispatcherProvider: DispatcherProvider
    ): IDispatcherProvider
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindInvestmentRepository(
        investmentRepository: InvestmentRepository
    ): IInvestmentRepository
}
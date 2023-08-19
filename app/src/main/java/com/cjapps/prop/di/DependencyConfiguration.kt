package com.cjapps.prop.di

import android.content.Context
import androidx.room.Room
import com.cjapps.prop.DispatcherProvider
import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.data.IInvestmentRepository
import com.cjapps.prop.data.InvestmentRepository
import com.cjapps.prop.data.database.AppDatabase
import com.cjapps.prop.data.database.InvestmentAllocationDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "prop-database",
        ).build()
    }

    @Provides
    fun provideInvestmentAllocationDao(appDatabase: AppDatabase): InvestmentAllocationDao {
        return appDatabase.investmentAllocationDao()
    }
}
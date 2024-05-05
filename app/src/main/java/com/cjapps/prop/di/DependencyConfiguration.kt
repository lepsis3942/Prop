package com.cjapps.prop.di

import android.content.Context
import androidx.room.Room
import com.cjapps.prop.DispatcherProvider
import com.cjapps.prop.IDispatcherProvider
import com.cjapps.prop.data.IInvestmentRepository
import com.cjapps.prop.data.IPropRepository
import com.cjapps.prop.data.InvestmentRepository
import com.cjapps.prop.data.PropRepository
import com.cjapps.prop.data.database.AppDatabase
import com.cjapps.prop.data.database.InvestmentAllocationDao
import com.cjapps.prop.data.mappers.DaoMapper
import com.cjapps.prop.data.mappers.IDaoMapper
import com.cjapps.prop.data.network.CacheInterceptor
import com.cjapps.prop.data.network.IPropAPIService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
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

    @Binds
    abstract fun bindPropRepository(
        propRepository: PropRepository
    ): IPropRepository

    @Binds
    abstract fun bindDaoMapper(daoMapper: DaoMapper): IDaoMapper
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

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    @Singleton
    fun provideRetrofit(@ApplicationContext appContext: Context): Retrofit {
        val okHttpClient = OkHttpClient().newBuilder()
            .cache(Cache(File(appContext.cacheDir, "http-cache"), 10L * 1024L * 1024L)) // 10 MiB
            .addNetworkInterceptor(CacheInterceptor())
            .build()
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://prop-644bb.firebaseapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesPropApi(retrofit: Retrofit): IPropAPIService =
        retrofit.create(IPropAPIService::class.java)
}
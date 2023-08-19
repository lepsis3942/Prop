package com.cjapps.prop.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [InvestmentAllocation::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun investmentAllocationDao(): InvestmentAllocationDao
}

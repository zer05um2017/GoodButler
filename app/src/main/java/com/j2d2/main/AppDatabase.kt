package com.j2d2.main

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.j2d2.bloodglucose.BloodGlucose
import com.j2d2.bloodglucose.BloodGlucoseDao
import com.j2d2.feeding.Feeding
import com.j2d2.feeding.FeedingDao
import com.j2d2.insulin.Insulin
import com.j2d2.insulin.InsulinDao

//@Database(entities = arrayOf(InsulinDao::class), version = 1)
@Database(entities = [Insulin::class, Feeding::class, BloodGlucose::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun insulinDao(): InsulinDao
    abstract fun feedingDao(): FeedingDao
    abstract fun bloodGlucoseDao(): BloodGlucoseDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, "terry.db")
                .build()

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
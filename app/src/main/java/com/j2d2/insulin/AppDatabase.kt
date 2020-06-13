package com.j2d2.insulin

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

//@Database(entities = arrayOf(InsulinDao::class), version = 1)
@Database(entities = arrayOf(Insulin::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun insulinDao(): InsulinDao

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
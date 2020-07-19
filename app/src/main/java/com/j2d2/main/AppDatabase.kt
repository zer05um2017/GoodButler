package com.j2d2.main

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.j2d2.bloodglucose.BloodGlucose
import com.j2d2.bloodglucose.BloodGlucoseDao
import com.j2d2.feeding.Feeding
import com.j2d2.feeding.FeedingDao
import com.j2d2.graph.GraphDao
import com.j2d2.insulin.Insulin
import com.j2d2.insulin.InsulinDao
import com.j2d2.pedometer.Pedometer
import com.j2d2.pedometer.PedometerDao
import com.j2d2.petinfo.Breed
import com.j2d2.petinfo.BreedDao
import com.j2d2.petinfo.Pet
import com.j2d2.petinfo.PetDao
import com.j2d2.DatabaseMigrationsExample.Song

//@Database(entities = arrayOf(InsulinDao::class), version = 1)
@Database(entities = [Insulin::class,
    Feeding::class,
    BloodGlucose::class,
    Breed::class,
    Pet::class,
    Pedometer::class,
    Song::class], version = 1, exportSchema = false) // the version has to be changed when you modified the database
abstract class AppDatabase : RoomDatabase() {
    abstract fun insulinDao(): InsulinDao
    abstract fun feedingDao(): FeedingDao
    abstract fun bloodGlucoseDao(): BloodGlucoseDao
    abstract fun graphDao(): GraphDao
    abstract fun petDao(): PetDao
    abstract fun breedDao(): BreedDao
    abstract fun pedometerDao(): PedometerDao

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

        /* This is for the code to migrate the database
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "terry.db")
            .addMigrations(MIGRATION_1_2).build()
         */

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
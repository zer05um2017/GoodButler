package com.j2d2.bloodglucose

import androidx.room.*
import com.j2d2.feeding.Feeding

@Dao
interface BloodGlucoseDao {
    @Query("SELECT * " +
                "FROM bloodglucose " +
                "WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') " +
                "LIKE :today " +
                "ORDER BY millis")
    fun findByToday(today: String): List<BloodGlucose>

    @Query("SELECT date(date(millis/1000,'unixepoch','localtime'), '-1 month') as chkdate " +
                "FROM bloodglucose " +
                "WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') " +
                "LIKE :day " +
                "GROUP BY chkdate " +
                "ORDER BY chkdate DESC")
    fun getDayList(day:String): List<String>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bloodGlucose: BloodGlucose)

//    @Insert
//    fun insertAll(vararg users: Insulin)

    @Query("DELETE FROM bloodglucose")
    fun deleteAll()

    @Delete
    fun delete(bloodGlucose: BloodGlucose)
}
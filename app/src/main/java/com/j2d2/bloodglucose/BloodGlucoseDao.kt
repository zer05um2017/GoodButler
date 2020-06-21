package com.j2d2.bloodglucose

import androidx.room.*
import com.j2d2.feeding.Feeding

@Dao
interface BloodGlucoseDao {
    @Query("SELECT * FROM bloodglucose WHERE date LIKE :today ORDER BY date, time DESC")
    fun findByToday(today: String): List<BloodGlucose>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bloodGlucose: BloodGlucose)

//    @Insert
//    fun insertAll(vararg users: Insulin)

    @Query("DELETE FROM bloodglucose")
    fun deleteAll()

    @Delete
    fun delete(bloodGlucose: BloodGlucose)
}
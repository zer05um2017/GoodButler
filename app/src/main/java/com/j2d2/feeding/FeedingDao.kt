package com.j2d2.feeding

import androidx.room.*
import com.j2d2.insulin.Insulin

@Dao
interface FeedingDao {
    //    @Query("SELECT * FROM insulin")
//    fun getAll(): List<Insulin>

//    @Query("SELECT * FROM insulin WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Insulin>

    @Query("SELECT * FROM feeding WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') LIKE :today ORDER BY millis")
    fun findByToday(today: String): List<Feeding>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(feeding: Feeding)

//    @Insert
//    fun insertAll(vararg users: Insulin)

    @Query("DELETE FROM feeding")
    fun deleteAll()

    @Delete
    fun delete(feeding: Feeding)
}
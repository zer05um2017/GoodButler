package com.j2d2.feeding

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.j2d2.insulin.Insulin

interface FeedingDao {
    //    @Query("SELECT * FROM insulin")
//    fun getAll(): List<Insulin>

//    @Query("SELECT * FROM insulin WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Insulin>

    @Query("SELECT * FROM feeding WHERE date LIKE :today ORDER BY date, time DESC")
    fun findByToday(today: String): List<Feeding>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg feeding: Feeding)

//    @Insert
//    fun insertAll(vararg users: Insulin)

    @Query("DELETE FROM feeding")
    fun deleteAll()

    @Delete
    fun delete(feeding: Feeding)
}